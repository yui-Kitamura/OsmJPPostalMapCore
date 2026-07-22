package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import pro.eng.yui.oss.osm.lib.jppostalcore.types.Days;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.TextValue;

import java.util.Map;

public interface IParser<T> {
    
    Map<Days,T> decode(TextValue tagValue);
    
    TextValue encode(Map<Days,T> map);
    
    default String[] reverse(String[] array){
        String[] reversed = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    } 
}
