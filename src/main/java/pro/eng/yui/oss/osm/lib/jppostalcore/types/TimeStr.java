package pro.eng.yui.oss.osm.lib.jppostalcore.types;

public class TimeStr {
    public final String value;

    public TimeStr(String timeStr) {
        if (validate(timeStr) == false){ throw new IllegalArgumentException("invalid time format"); }
        this.value = timeStr;
    }
    
    public static boolean validate(String input){
        if (input == null || !input.matches("^([0-9]|[0-2][0-9]):[0-5][0-9]$")) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString(){
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TimeStr other = (TimeStr) obj;
        return value.equals(other.value);
    }
}
