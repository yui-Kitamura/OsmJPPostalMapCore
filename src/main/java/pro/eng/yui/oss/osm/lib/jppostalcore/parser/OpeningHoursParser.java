package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import org.jetbrains.annotations.NotNull;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.*;

import java.util.*;

public class OpeningHoursParser extends AbstParser<OpeningHoursParser.DaySchedule> {
    
    public OpeningHoursParser(){}
    
    public enum DayStatus implements IDayStatus {
        /** 24時間営業 */
        OPEN24,
        /** 営業時間あり */
        OPEN_DAY,
        /** 休業日 */
        CLOSED_DAY,
        /** 不明 */
        UNKNOWN;
    }
    
    public static class DaySchedule implements IDaySchedule {
        private final DayStatus status;
        public DayStatus status(){ return status; }
        private final List<OpenCloseTime> schedule;
        @Override
        @NotNull
        public List<OpenCloseTime> schedule(){ return schedule; }
        
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
        if (plane == null || plane.trim().isEmpty()) {
            return null;
        }

        String[] parts = plane.split(";");
        List<String> expandedParts = new ArrayList<>();

        for (String part : parts) {
            if (part.trim().isEmpty()) {
                return null;
            }
            part = part.trim();

            // 24/7 を Mo-Su,PH 0:00-24:00 に展開して再代入
            if (part.trim().equals("24/7")) {
                part = "Mo-Su,PH 0:00-24:00";
            }

            // 曜日なしの要素は Mo-Su と見做す
            part = addDefaultDaysIfNeeded(part);

            // partの曜日 Mo-Fr を Mo,Tu,We,Th,Fr のように展開して再代入
            // We-Mo のような週跨ぎに留意
            String[] spaced = part.split(" ");
            StringBuilder daysBuilder = new StringBuilder();
            StringBuilder hoursBuilder = new StringBuilder();
            boolean hoursStarted = false;
            for (String s : spaced) {
                if (s.isEmpty()){ continue; }
                String trimmedS = s.trim();
                if (!hoursStarted) {
                    try {
                        // 曜日のラベル(Mo, Tu... PH)が含まれているかチェック
                        boolean containsDay = false;
                        for (String label : Days.labels()) {
                            if (trimmedS.contains(label)) {
                                containsDay = true;
                                break;
                            }
                        }
                        if (containsDay) {
                            if (daysBuilder.length() > 0){ daysBuilder.append(","); }
                            String dayStr = trimmedS;
                            while (dayStr.endsWith(",")) {
                                dayStr = dayStr.substring(0, dayStr.length() - 1);
                            }
                            daysBuilder.append(dayStr);
                        } else {
                            hoursStarted = true;
                        }
                    } catch (Exception e) {
                        hoursStarted = true;
                    }
                }
                
                if (hoursStarted) {
                    if (hoursBuilder.length() > 0){ hoursBuilder.append(" "); }
                    hoursBuilder.append(trimmedS);
                }
            }
            String daysPart = daysBuilder.toString();
            String timesPart = hoursBuilder.toString();

            if (daysPart.isEmpty()) {
                // If it's pure "off" or time-only, it should have been handled by addDefaultDaysIfNeeded
                // If we reach here with empty daysPart, it means it was an invalid format or empty segment.
                return null;
            }

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
        }catch (IllegalArgumentException e) {
            return null;
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
        if (day.label.equals(parts[0])) {
            if (parts[1].trim().equals("off")) {
                return new DaySchedule(DayStatus.CLOSED_DAY, new ArrayList<>());
            }
            List<OpenCloseTime> openHours = new ArrayList<>();
            String[] hourParts = parts[1].split(",");
            for (int i=0; i<hourParts.length; i++) {
                //10:00-12:00 の分解処理
                String[] hours = hourParts[i].trim().split("-",2);
                if (hours.length < 2) {
                    throw new IllegalArgumentException("invalid time format: " + hourParts[i]);
                }
                openHours.add(new OpenCloseTime(hours[0].trim(), hours[1].trim()));
                if (i == 0 && hourParts.length == 1 && hours[0].trim().equals("0:00") && hours[1].trim().equals("24:00")) {
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
