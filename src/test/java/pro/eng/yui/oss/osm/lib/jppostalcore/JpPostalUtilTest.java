package pro.eng.yui.oss.osm.lib.jppostalcore;

import org.junit.jupiter.api.Test;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.OsmPoi;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    @Test
    void callOverpass(){
        String query = "node[\"name\"=\"合同会社北村由衣\"];";
        List<OsmPoi> result = assertDoesNotThrow(()->JpPostalUtil.callOverpass(query, 3, 5));
        assertEquals(1, result.size());
        OsmPoi poi = result.getFirst();
        assertEquals(11608885454L, poi.getId());
        assertEquals("node", poi.getType());
        assertEquals("合同会社北村由衣", poi.getTag("name"));
    }
    @Test
    void callOverpassEmpty(){
        String query = "way[\"eman\"=\"衣由村北社会同合\"];";
        List<OsmPoi> result = assertDoesNotThrow(()->JpPostalUtil.callOverpass(query, 3, 5));
        assertTrue(result.isEmpty());
    }
    @Test
    void callOverpass400(){
        String wrongQuery = "what?";
        IllegalArgumentException argEx = assertThrows(
            IllegalArgumentException.class, ()->JpPostalUtil.callOverpass(wrongQuery, 3, 5)
        );
        assertTrue(argEx.getMessage().startsWith("HTTP 400 error"));
    }
    @Test
    void callOverpassWithRetry(){
        String query = "node[\"name\"=\"合同会社北村由衣\"];";
        List<OsmPoi> result = assertDoesNotThrow(()->JpPostalUtil.callOverpass(query, 3, 1));
        assertEquals(1, result.size());
        OsmPoi poi = result.getFirst();
        assertEquals(11608885454L, poi.getId());
        assertEquals("node", poi.getType());
        assertEquals("合同会社北村由衣", poi.getTag("name"));
    }
    @Test
    void callOverpassRetryIllegalArgument(){
        String query = "way[\"eman\"=\"衣由村北社会同合\"];";
        assertThrows(IllegalStateException.class, ()->JpPostalUtil.callOverpass(query, 0, 1));
    }
}