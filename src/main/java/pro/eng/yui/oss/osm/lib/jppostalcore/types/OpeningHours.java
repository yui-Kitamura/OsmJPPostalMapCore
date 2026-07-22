package pro.eng.yui.oss.osm.lib.jppostalcore.types;

public class OpeningHours implements TextValue {
    private final String value;
    
    public OpeningHours(String value){
        this.value = value;
    }

    @Override
    public String getOrigin() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        OpeningHours other = (OpeningHours) obj;
        return value.equals(other.value);
    }
}
