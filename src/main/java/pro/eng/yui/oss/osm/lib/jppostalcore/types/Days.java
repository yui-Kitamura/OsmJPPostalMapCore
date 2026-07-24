package pro.eng.yui.oss.osm.lib.jppostalcore.types;

/** OSM形式の曜日 */
public enum Days {
    MONDAY("Mo", "月", WeekDay.WEEK_DAY),
    TUESDAY("Tu", "火", WeekDay.WEEK_DAY),
    WEDNESDAY("We", "水", WeekDay.WEEK_DAY),
    THURSDAY("Th", "木", WeekDay.WEEK_DAY),
    FRIDAY("Fr", "金", WeekDay.WEEK_DAY),
    SATURDAY("Sa", "土", WeekDay.WEEKEND),
    SUNDAY("Su", "日", WeekDay.WEEKEND),
    PUBLIC_HOLIDAY("PH", "祝", WeekDay.HOLIDAY);
    
    public final String label;
    public final String jaLabel;
    public final WeekDay dayType;
    /** 平日/週末/祝日 */
    public enum WeekDay {
        WEEK_DAY("Mo-Fr","平日"),
        WEEKEND("Sa-Su","週末"),
        HOLIDAY("PH","祝日");
        
        public final String OSMFormat;
        public final String jaLabel;
        
        WeekDay(String osmFormat, String label){
            this.OSMFormat = osmFormat;
            this.jaLabel = label;
        }
    }
    
    Days(String label, String jaLabel, WeekDay dayType){
        this.label = label;
        this.jaLabel = jaLabel;
        this.dayType = dayType;
    }

    /** 文字列から曜日を返します
     * @param in OSM形式または日本語1文字の曜日表現
     * @return 文字列から解釈した曜日
     * @throws IllegalArgumentException 解釈できなかった時 */
    public static Days getFromLabel(String in){
        for (Days day: Days.values()) {
            if (day.label.equals(in) || day.jaLabel.equals(in)) {
                return day;
            }
        }
        throw new IllegalArgumentException(in + " not matched");
    }
    
    public WeekDay getDayType(){
        return dayType;
    }
    
    public static String[] labels(){
        String[] res = {MONDAY.label, TUESDAY.label, WEDNESDAY.label, THURSDAY.label, FRIDAY.label,
                SATURDAY.label, SUNDAY.label, PUBLIC_HOLIDAY.label};       
        return res;
    }
    
}
