package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import org.junit.jupiter.api.Test;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.Days;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.TextValue;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IParserTest {
    
    static class ParserMock extends AbstParser<Object>{
        @Override
        public Map<Days, Object> decode(TextValue tagValue) { return Map.of(); }
        @Override
        public TextValue encode(Map<Days, Object> map) { return null; }
    } 
    
    @Test
    void reverseOne(){
        String[] input = {"A"};
        String[] expected = {"A"};
        ParserMock mock = new ParserMock();
        String[] result = mock.reverse(input);
        assertEquals(expected[0], result[0]);
    }
    
    @Test
    void reverseTwo(){
        String[] input = {"A","Z"};
        String[] expected = {"Z","A"};
        ParserMock mock = new ParserMock();
        String[] result = mock.reverse(input);
        assertEquals(expected[0], result[0]);
        assertEquals(expected[1], result[1]);
    }
    
    @Test
    void reverseEmpty(){
        String[] input = {};
        String[] expected = {};
        ParserMock mock = new ParserMock();
        String[] result = mock.reverse(input);
        assertEquals(expected.length, result.length);
    }

}