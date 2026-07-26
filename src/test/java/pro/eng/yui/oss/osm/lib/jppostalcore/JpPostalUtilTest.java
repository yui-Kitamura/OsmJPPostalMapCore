package pro.eng.yui.oss.osm.lib.jppostalcore;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import pro.eng.yui.oss.osm.lib.jppostalcore.parser.CollectionTimeParser;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.CollectionTimes;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.Days;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.JpAddress;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.OsmPoi;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JpPostalUtilTest {

    @BeforeAll
    static void waitHolidays() throws InterruptedException {
        int retry = 0;
        while (!JpPostalUtil.isHolidaysLoaded() && retry < 100) {
            Thread.sleep(100);
            retry++;
        }
    }

    /* 祝日判定 */
    @Test
    void isHolidayYes() {
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), 1,1);
        assertTrue(JpPostalUtil.isHoliday(date));
    }
    @Test
    void isHolidayDeny(){
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), 12, 31);
        assertFalse(JpPostalUtil.isHoliday(date));
    }
    @Test
    void isHolidayNoSupportForLastYear(){
        LocalDate date = LocalDate.of(LocalDate.now().getYear()-1, 1,1);
        assertFalse(JpPostalUtil.isHoliday(date));
    }
    
    /* 曜日取得 */
    @Test
    void getDaysPH(){
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), 1,1);
        assertEquals(Days.PUBLIC_HOLIDAY, JpPostalUtil.getDays(date));
    }
    @Test
    void getDaysMon(){
        LocalDate date = LocalDate.of(2026, 7,13);
        assertEquals(Days.MONDAY, JpPostalUtil.getDays(date));
    }
    
    /* OverpassAPIコール */
    @Order(1)
    @Test
    void callOverpass(){
        String query = "node[\"name\"=\"合同会社北村由衣\"];";
        List<OsmPoi> result = assertDoesNotThrow(()->JpPostalUtil.callOverpass(query, 3, 10).join());
        assertEquals(1, result.size());
        OsmPoi poi = result.getFirst();
        assertEquals(11608885454L, poi.getId());
        assertEquals("node", poi.getType());
        assertEquals("合同会社北村由衣", poi.getTag("name"));
        assertTrue(1 < poi.getVer());
    }
    @Test
    void callOverpassEmpty(){
        String query = "way[\"eman\"=\"衣由村北社会同合\"];";
        List<OsmPoi> result = assertDoesNotThrow(()->JpPostalUtil.callOverpass(query, 3, 10).join());
        assertTrue(result.isEmpty());
    }
    @Test
    void callOverpass400(){
        String wrongQuery = "what?";
        assertThrows(
            Exception.class, ()->JpPostalUtil.callOverpass(wrongQuery, 3, 10).get()
        );
    }
    @Test
    void callOverpassWithRetry(){
        String query = "node[\"name\"=\"合同会社北村由衣\"];";
        List<OsmPoi> result = assertDoesNotThrow(()->JpPostalUtil.callOverpass(query, 3, 10).join());
        assertEquals(1, result.size());
        OsmPoi poi = result.getFirst();
        assertEquals(11608885454L, poi.getId());
        assertEquals("node", poi.getType());
        assertEquals("合同会社北村由衣", poi.getTag("name"));
        assertTrue(1 < poi.getVer());
    }
    @Test
    void callOverpassRetryIllegalArgument(){
        String query = "way[\"eman\"=\"衣由村北社会同合\"];";
        assertThrows(Exception.class, ()->JpPostalUtil.callOverpass(query, 0, 1).get());
    }
    
    @Test
    void getPrefectures(){
        Map<String, Integer> result = assertDoesNotThrow(()->{ return JpPostalUtil.getPrefectures().join(); });
        assertEquals(47, result.size());
        assertEquals(19, result.get("山梨県"));
    }
    
    @Test
    void getPrefecture(){
        assertEquals(19, JpPostalUtil.getPrefecture("山梨県").join());
    }
    @Test
    void getPrefectureNotExist(){
        assertEquals(-99, JpPostalUtil.getPrefecture("海無県").join());
    }
    
    @Test
    void decodeCollectionTime(){
        CollectionTimes value = new CollectionTimes("Mo-Su,PH 7:30,8:50,12:30,13:55,15:40,19:00;");
        Map<Days, CollectionTimeParser.DaySchedule> result = JpPostalUtil.decodeCollectionTimes(value);

        assertEquals(8, result.size());
    }
    
    @Test
    void getJpAddressToString(){
        Map<String, String> addrMap = new HashMap<>();
        final String input = "〒100-8994 東京都千代田区丸の内二丁目7-2";
        addrMap.put("addr:full", input);
        assertEquals("full: "+ input, JpPostalUtil.getAddressText(addrMap));
    }
    
    @Test
    void getJpAddressToStringWithParts(){
        Map<String,String> addrMap = new HashMap<>();
        addrMap.put("addr:postcode", "100-8994");
        addrMap.put("addr:province", "東京都");
        addrMap.put("addr:city", "千代田区");
        addrMap.put("addr:neighbourhood", "丸の内二丁目");
        addrMap.put("addr:block_number", "7");
        addrMap.put("addr:housenumber", "2");
        addrMap.put("addr:housename", "JPタワー");
        assertEquals("〒100-8994 東京都千代田区丸の内二丁目7-2 JPタワー", JpPostalUtil.getAddressText(addrMap));
    }
    
    @Test
    void getJpAddress(){
        Map<String,String> addrMap = new HashMap<>();
        addrMap.put("addr:postcode", "100-8994");
        addrMap.put("addr:province", "東京都");
        addrMap.put("addr:county", "架空郡");
        addrMap.put("addr:city", "架空町");
        addrMap.put("addr:suburb", "架空区");
        addrMap.put("addr:quarter", "架空");
        addrMap.put("addr:neighbourhood", "字雲");
        addrMap.put("addr:block_number", "7");
        addrMap.put("addr:housenumber", "2");
        addrMap.put("addr:housename", "JPタワー");
        addrMap.put("addr:floor", "2F");
        addrMap.put("addr:room", "201");
        JpAddress address = JpPostalUtil.getAddress(addrMap);
        assertEquals("〒100-8994 東京都架空郡架空町架空区架空字雲7-2 JPタワー 2F201", address.toString());
        assertEquals("100-8994", address.getPostcode());
        assertEquals("東京都", address.getProvince());
        assertEquals("架空郡", address.getCounty());
        assertEquals("架空町", address.getCity());
        assertEquals("架空区", address.getSuburb());
        assertEquals("架空", address.getQuarter());
        assertEquals("字雲", address.getNeighbourhood());
        assertEquals("7", address.getBlockNumber());
        assertEquals("2", address.getHousenumber());
        assertEquals("JPタワー", address.getHousename());
        assertEquals("2F", address.getFloor());
        assertEquals("201", address.getRoom());
    }
}