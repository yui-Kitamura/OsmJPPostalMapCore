package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import pro.eng.yui.oss.osm.lib.jppostalcore.types.*;

import java.util.*;

public class CollectionTimeParser implements IParser<CollectionTimeParser.DaySchedule> {
    public CollectionTimeParser(){}

    public enum DayStatus{
        /** 営業時間あり */
        OPEN_DAY,
        /** 休業日 */
        CLOSED_DAY,
        /** 不明 */
        UNKNOWN;
    }
    
    public static class DaySchedule {
        CollectionTimeParser.DayStatus status;
        List<CollectionTime> schedule;
        public DaySchedule(){
            status = CollectionTimeParser.DayStatus.UNKNOWN;
            schedule = new ArrayList<>();
        }
        public DaySchedule(CollectionTimeParser.DayStatus status, List<CollectionTime> schedule){
            this.status = status;
            this.schedule = schedule;
        }
        @Override
        public String toString(){
            return "{"+status.name()+",["+schedule.size()+"]";
        }
    }
    
    @Override
    public Map<Days, CollectionTimeParser.DaySchedule> decode(TextValue tagValue) {
        if (!(tagValue instanceof CollectionTimes)){ throw new ClassCastException("type miss match"); }
        Map<Days, CollectionTimeParser.DaySchedule> resultMap = new HashMap<>();
        for (Days d : Days.values()) {
            resultMap.put(d, new CollectionTimeParser.DaySchedule());
        }

        String plane = tagValue.getOrigin();

        String[] parts = plane.split(";");
        List<String> expandedParts = new ArrayList<>();

        for (String part : parts) {
            part = part.trim();

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
                    CollectionTimeParser.DaySchedule schedule = fetchRuleToDay(d, part);
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
     * @param part "Mo,Tu,We,Th,Fr 10:00, 13:00"のように、;でsplitされて曜日展開された塊
     * @return マッチした場合DayScheduleインスタンス。ない場合<code>null</code>*/
    private CollectionTimeParser.DaySchedule fetchRuleToDay(Days day, String part){
        String[] parts = part.split(" ", 2);
        if (parts.length < 2){ throw new IllegalArgumentException(part); }
        if (day.getLabel().equals(parts[0])) {
            if (parts[1].equals("off")) {
                return new CollectionTimeParser.DaySchedule(CollectionTimeParser.DayStatus.CLOSED_DAY, new ArrayList<>());
            }
            List<CollectionTime> openHours = new ArrayList<>();
            String[] hourParts = parts[1].split(",");
            for (int i=0; i<hourParts.length; i++) {
                openHours.add(new CollectionTime(hourParts[i]));
            }
            return new CollectionTimeParser.DaySchedule(CollectionTimeParser.DayStatus.OPEN_DAY, openHours);
        }else {
            if (Arrays.asList(Days.labels()).contains(parts[0])) {
                return null;
            } else {
                throw new IllegalArgumentException(parts[0]);
            }
        }

    }

    @Override
    public CollectionTimes encode(Map<Days, CollectionTimeParser.DaySchedule> schedule) {
        Map<Days, String> wip = new HashMap<>();
        for (Days d : schedule.keySet()) {
            List<CollectionTime> schedulesInDay = schedule.get(d).schedule;
            switch (schedule.get(d).status) {
                case OPEN_DAY :
                    StringBuilder openingHours = new StringBuilder();
                    for (int i = 0; i < schedulesInDay.size(); i++) {
                        openingHours.append(schedulesInDay.get(i).toString()).append(",");
                    }
                    openingHours.deleteCharAt(openingHours.length()-1);
                    wip.put(d, openingHours.toString());
                    break;
                case CLOSED_DAY:
                    wip.put(d, "off");
                    break;
                case UNKNOWN:
                    break;
            }
        }

        List<String> parts = merge(wip);

        String result = String.join("; ", parts);
        return new CollectionTimes(result);
    }
}
