package pro.eng.yui.oss.osm.lib.jppostalcore;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class JpPostalUtilTest {

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
}