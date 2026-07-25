package pro.eng.yui.oss.osm.lib.jppostalcore.types;

public class CollectionTimes implements TextValue {
    
    private final String value;
    
    public CollectionTimes(String value){
        if (value != null){ value = value.trim(); }
        this.value = value;
    }

    @Override
    public String getOrigin() {
        return value;
    }
    
}
