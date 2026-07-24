package pro.eng.yui.oss.osm.lib.jppostalcore.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DaysTest {
    
    @Test
    void getLabelTest(){
        assertEquals(Days.MONDAY, Days.getFromLabel("Mo"));
        assertEquals(Days.MONDAY, Days.getFromLabel("月"));
        assertThrows(IllegalArgumentException.class, ()->Days.getFromLabel("mo"));
    }

}