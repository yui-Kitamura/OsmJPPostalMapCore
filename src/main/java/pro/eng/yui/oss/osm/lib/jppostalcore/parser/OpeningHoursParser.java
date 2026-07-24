package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import org.jetbrains.annotations.NotNull;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.Days;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.OpenCloseTime;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.OpeningHours;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.TextValue;

import java.util.*;

public class OpeningHoursParser extends AbstParser<OpeningHoursParser.DaySchedule> {
    
    public OpeningHoursParser(){}
    
    public enum DayStatus{
        /** 24時間営業 */
        OPEN24,
        /** 営業時間あり */
        OPEN_DAY,
        /** 休業日 */
        CLOSED_DAY,
        /** 不明 */
        UNKNOWN;
    }
    
    public static class DaySchedule {
        public final DayStatus status;
        @NotNull
        public final List<OpenCloseTime> schedule;
        
        public DaySchedule(){
            status = OpeningHoursParser.DayStatus.UNKNOWN;
            schedule = new ArrayList<>();
        }
        public DaySchedule(DayStatus status, @NotNull List<OpenCloseTime> schedule){
            this.status = status;
            this.schedule = schedule;
        }
        @Override
        public String toString(){
            return "{"+status.name()+",["+schedule.size()+"]";
        }
    }

    @Override
    public Map<Days, OpeningHoursParser.DaySchedule> decode(TextValue tagValue) {
        if (!(tagValue instanceof OpeningHours)){ throw new ClassCastException("type miss match"); }
        Map<Days, OpeningHoursParser.DaySchedule> resultMap = new HashMap<>();
        for (Days d : Days.values()) {
            resultMap.put(d, new DaySchedule());
        }
        
        String plane = tagValue.getOrigin();

        String[] parts = plane.split(";");
        List<String> expandedParts = new ArrayList<>();

        for (String part : parts) {
            part = part.trim();

            // 24/7 を Mo-Su,PH 0:00-24:00 に展開して再代入
            if (part.equals("24/7")) {
                part = "Mo-Su,PH 0:00-24:00";
            }

            // 曜日なしの要素は Mo-Su と見做す
            part = addDefaultDaysIfNeeded(part);

            // partの曜日 Mo-Fr を Mo,Tu,We,Th,Fr のように展開して再代入
            // We-Mo のような週跨ぎに留意
            String[] subParts = part.split(" ", 2);
            String daysPart = subParts[0];
            String timesPart = subParts.length > 1 ? subParts[1] : "";

            String[] dayGroups = daysPart.split(",");
            for (String dayGroup : dayGroups) {
                dayGroup = dayGroup.trim();
                List<String> expandedDays = expandDayRange(dayGroup);
                for (String day : expandedDays) {
                    expandedParts.add(day + (timesPart.isEmpty() ? "" : " " + timesPart));
                }
            }
        }

        parts = expandedParts.toArray(new String[0]);
        parts = reverse(parts); //後方のルール優先
        try {
            for (Days d : Days.values()) {
                for (String part : parts) {
                    DaySchedule schedule = fetchRuleToDay(d, part);
                    if (schedule != null) {
                        resultMap.put(d, schedule);
                        break;
                    }
                }
            }
        }catch (Exception e) {
            System.err.println("decode err: caused by: "+ e.getMessage());
            return null;
        }

        return resultMap;
    }

    /**
     * @param part "Mo,Tu,We,Th,Fr 10:00-12:00, 13:00-17:00"のように、;でsplitされて曜日展開された塊
     * @return マッチした場合DayScheduleインスタンス。ない場合<code>null</code>*/
    private DaySchedule fetchRuleToDay(Days day, String part){
        String[] parts = part.split(" ", 2);
        if (parts.length < 2){ throw new IllegalArgumentException(part); }
        if (day.getLabel().equals(parts[0])) {
            if (parts[1].equals("off")) {
                return new DaySchedule(DayStatus.CLOSED_DAY, new ArrayList<>());
            }
            List<OpenCloseTime> openHours = new ArrayList<>();
            String[] hourParts = parts[1].split(",");
            for (int i=0; i<hourParts.length; i++) {
                //10:00-12:00 の分解処理
                String[] hours = hourParts[i].trim().split("-",2);
                openHours.add(new OpenCloseTime(hours[0], hours[1]));
                if (i == 0 && hourParts.length == 1 && hours[0].equals("0:00") && hours[1].equals("24:00")) {
                    return new DaySchedule(DayStatus.OPEN24, openHours);
                }
            }
            return new DaySchedule(DayStatus.OPEN_DAY, openHours);
        }else {
            if (Arrays.asList(Days.labels()).contains(parts[0])) {
                return null;
            } else {
                throw new IllegalArgumentException(parts[0]);
            }
        }
        
    }

    @Override
    public OpeningHours encode(Map<Days, OpeningHoursParser.DaySchedule> schedule) {
        Map<Days, String> wip = new HashMap<>();
        for (Days d : schedule.keySet()) {
            List<OpenCloseTime> schedulesInDay = schedule.get(d).schedule;
            switch (schedule.get(d).status) {
                case OPEN_DAY :
                    StringBuilder openingHours = new StringBuilder();
                    for (int i = 0; i < schedulesInDay.size(); i++) {
                        openingHours.append(schedulesInDay.get(i).toPairStr()).append(",");
                    }
                    openingHours.deleteCharAt(openingHours.length()-1);
                    wip.put(d, openingHours.toString());
                    break;
                case CLOSED_DAY:
                    wip.put(d, "off");
                    break;
                case UNKNOWN:
                    break;
                case OPEN24:
                    wip.put(d, "0:00-24:00");
                    break;
            }
        }

        List<String> parts = merge(wip);
        
        String result = String.join("; ", parts);
        return new OpeningHours(result);
    }

}
