package pro.eng.yui.oss.osm.lib.jppostalcore.types;

/** OSM形式の曜日 */
public enum Days {
    MONDAY("Mo", WeekDay.WEEK_DAY),
    TUESDAY("Tu", WeekDay.WEEK_DAY),
    WEDNESDAY("We", WeekDay.WEEK_DAY),
    THURSDAY("Th", WeekDay.WEEK_DAY),
    FRIDAY("Fr", WeekDay.WEEK_DAY),
    SATURDAY("Sa", WeekDay.WEEKEND),
    SUNDAY("Su", WeekDay.WEEKEND),
    PUBLIC_HOLIDAY("PH", WeekDay.HOLIDAY);
    
    private final String label;
    private final WeekDay dayType;
    public enum WeekDay {
        WEEK_DAY, WEEKEND, HOLIDAY;
    }
    
    Days(String label, WeekDay dayType){
        this.label = label;
        this.dayType = dayType;
    }
    
    public String getLabel(){ return label; }
    public Days getFromLabel(String in){
        for (Days day: Days.values()) {
            if ((day.label.equalsIgnoreCase(in))) {
                return day;
            }
        }
        throw new IllegalArgumentException(in + " not matched");
    }
    
    public WeekDay getDayType(){
        return dayType;
    }
    
    
}
