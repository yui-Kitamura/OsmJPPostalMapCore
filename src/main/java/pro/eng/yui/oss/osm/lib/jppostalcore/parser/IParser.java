package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import pro.eng.yui.oss.osm.lib.jppostalcore.types.Days;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.TextValue;

import java.util.*;

public interface IParser<T> {
    
    Map<Days,T> decode(TextValue tagValue);
    
    TextValue encode(Map<Days,T> map);
    
}
