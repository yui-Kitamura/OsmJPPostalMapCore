package pro.eng.yui.oss.osm.lib.jppostalcore;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.OsmPoi;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JpPostalUtilTest {

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
    
    /* OverpassAPIコール */
    @Order(1)
    @Test
    void callOverpass(){
        String query = "node[\"name\"=\"合同会社北村由衣\"];";
        List<OsmPoi> result = assertDoesNotThrow(()->JpPostalUtil.callOverpass(query));
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
        List<OsmPoi> result = assertDoesNotThrow(()->JpPostalUtil.callOverpass(query, 3, 10));
        assertTrue(result.isEmpty());
    }
    @Test
    void callOverpass400(){
        String wrongQuery = "what?";
        IllegalArgumentException argEx = assertThrows(
            IllegalArgumentException.class, ()->JpPostalUtil.callOverpass(wrongQuery, 3, 10)
        );
        assertTrue(argEx.getMessage().startsWith("HTTP 400 error"));
    }
    @Test
    void callOverpassWithRetry(){
        String query = "node[\"name\"=\"合同会社北村由衣\"];";
        List<OsmPoi> result = assertDoesNotThrow(()->JpPostalUtil.callOverpass(query, 3, 10));
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
        assertThrows(IllegalStateException.class, ()->JpPostalUtil.callOverpass(query, 0, 1));
    }
    
    @Test
    void getPrefectures(){
        Map<String, Integer> result = assertDoesNotThrow(()->{ return JpPostalUtil.getPrefectures(); });
        assertEquals(47, result.size());
        assertEquals(19, result.get("山梨県"));
    }
    
    @Test
    void getPrefecture(){
        assertEquals(19, JpPostalUtil.getPrefecture("山梨県"));
    }
    @Test
    void getPrefectureNotExist(){
        assertEquals(-99, JpPostalUtil.getPrefecture("海無県"));
    }
}