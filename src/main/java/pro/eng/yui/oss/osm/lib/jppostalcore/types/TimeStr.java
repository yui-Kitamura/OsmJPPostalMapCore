package pro.eng.yui.oss.osm.lib.jppostalcore.types;

import java.time.LocalTime;

public class TimeStr {
    public final String value;
    public final int totalMinute;

    public TimeStr(String timeStr) {
        if (validate(timeStr) == false){ throw new IllegalArgumentException("invalid time format"); }
        this.value = timeStr;
        String[] split = value.split(":");
        this.totalMinute = Integer.parseInt(split[0])*60 + Integer.parseInt(split[1]);
    }
    
    public static boolean validate(String input){
        if (input == null || !input.matches("^([0-9]|[0-2][0-9]):[0-5][0-9]$")) {
            return false;
        }
        return true;
    }

    public LocalTime getTime(){
        String[] split = value.split(":");
        int hour = Integer.parseInt(split[0])%24;
        return LocalTime.of(hour,Integer.parseInt(split[1]));
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
