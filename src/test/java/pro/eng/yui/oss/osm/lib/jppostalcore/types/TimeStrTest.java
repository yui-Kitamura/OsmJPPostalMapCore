package pro.eng.yui.oss.osm.lib.jppostalcore.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeStrTest {
    
    @Test
    void testValidate(){
        assertDoesNotThrow(()->new TimeStr("0:00"));
        assertDoesNotThrow(()->new TimeStr("00:00"));
        assertDoesNotThrow(()->new TimeStr("01:59"));
        assertDoesNotThrow(()->new TimeStr("12:34"));
        assertDoesNotThrow(()->new TimeStr("24:00"));
        assertDoesNotThrow(()->new TimeStr("29:59")); //ここまで許容
        assertThrows(IllegalArgumentException.class, ()->new TimeStr("30:00"));
        assertThrows(IllegalArgumentException.class, ()->new TimeStr("10"));
        assertThrows(IllegalArgumentException.class, ()->new TimeStr("0：00")); //全角コロン
        assertThrows(IllegalArgumentException.class, ()->new TimeStr("１２:３４")); //全角数字
    }

}