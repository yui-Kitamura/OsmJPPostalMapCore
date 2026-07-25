package pro.eng.yui.oss.osm.lib.jppostalcore.types;

public class CollectionTimes implements TextValue {
    
    private final String value;
    
    public CollectionTimes(String value){
        this.value = value.trim();
    }

    @Override
    public String getOrigin() {
        return value;
    }
    
}
