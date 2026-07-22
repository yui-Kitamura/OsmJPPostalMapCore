package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import pro.eng.yui.oss.osm.lib.jppostalcore.types.Days;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.TextValue;

import java.util.*;

public interface IParser<T> {
    
    Map<Days,T> decode(TextValue tagValue);
    
    TextValue encode(Map<Days,T> map);
    
    default String[] reverse(String[] array){
        String[] reversed = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

    default String addDefaultDaysIfNeeded(String part) {
        // 時間パターンで始まる場合は曜日がないと判断
        if (part.matches("^\\d+:\\d+.*")) {
            return "Mo-Su " + part;
        }
        // "off" だけの場合も曜日がない
        if (part.equals("off")) {
            return "Mo-Su off";
        }
        return part;
    }

    default List<String> expandDayRange(String dayGroup) {
        List<String> result = new ArrayList<>();

        if (dayGroup.contains("-")) {
            String[] range = dayGroup.split("-");
            String startDay = range[0].trim();
            String endDay = range[1].trim();

            int startIdx = -1;
            int endIdx = -1;

            String[] dayLabels = Days.labels();

            for (int i = 0; i < dayLabels.length -1; i++) { //PH除外
                if (dayLabels[i].equals(startDay)) startIdx = i;
                if (dayLabels[i].equals(endDay)) endIdx = i;
            }

            if (startIdx != -1 && endIdx != -1) {
                if (startIdx <= endIdx) {
                    // 通常の範囲
                    for (int i = startIdx; i <= endIdx; i++) {
                        result.add(dayLabels[i]);
                    }
                } else {
                    // 週跨ぎ (We-Mo など)
                    for (int i = startIdx; i < dayLabels.length-1; i++) { //PH除外
                        result.add(dayLabels[i]);
                    }
                    for (int i = 0; i <= endIdx; i++) {
                        result.add(dayLabels[i]);
                    }
                }
            } else {
                result.add(dayGroup);
            }
        } else {
            result.add(dayGroup);
        }

        return result;
    }

    default List<String> merge(Map<Days, String> progress){
        if (progress.isEmpty()) { return List.of(); } // all UNKNOWN
        List<String> result = new ArrayList<>();

        Set<Days> checked = EnumSet.noneOf(Days.class);

        List<Days> monTo = findSame(progress, Days.MONDAY);
        if (progress.get(Days.MONDAY).equals("0:00-24:00") && monTo.size() == 8) {
            result.add("24/7");
            return result;
        }

        List<Days> looking = Arrays.asList(Days.values());

        for (Days day : looking) {
            if (day == Days.PUBLIC_HOLIDAY){ continue; }
            if (checked.contains(day) == false) {
                List<Days> dayTo = findSame(progress, day);
                checked.addAll(dayTo);
                if (progress.get(day) != null) {
                    String fromDay = buildDayRangeString(dayTo, day);
                    result.add(fromDay + " " + progress.get(day));
                }
            }
        }

        if (checked.contains(Days.PUBLIC_HOLIDAY) == false) {
            result.add(Days.PUBLIC_HOLIDAY.getLabel() + " " + progress.get(Days.PUBLIC_HOLIDAY));
        }
        return result;
    }

    default String buildDayRangeString(List<Days> days, Days first) {
        List<Days> dayOrder = Arrays.asList(Days.values());
        int startIdx = dayOrder.indexOf(first);

        StringBuilder daysStr = new StringBuilder(first.getLabel());
        for (int i = startIdx + 1; i < dayOrder.size()-1; i++) {
            Days currentDay = dayOrder.get(i);
            if (days.contains(currentDay)) {
                Days previousDay = dayOrder.get(i - 1);
                boolean hasPrevious = days.contains(previousDay);
                daysStr.append(appendDay(currentDay, hasPrevious));
            }
        }
        if (days.contains(Days.PUBLIC_HOLIDAY)) {
            daysStr.append(",PH");
        }
        // Mo-Tu-We-Tu-Fr-Sa-Su のような -Xx- を除去（-にする）
        // Mo-Tu-We,Fr-Sa-Su の場合、Mo-We,Fr-Su になる
        return daysStr.toString().replaceAll("-(?:[A-Z][a-z]-)+","-");
    }
    default String appendDay(Days day, boolean contains) {
        return contains ? "-"+day.getLabel() : ","+day.getLabel();
    }

    default List<Days> findSame(Map<Days, String> list, Days key) {
        final String currentTarget = list.get(key);
        List<Days> result = new ArrayList<>();
        for (Days d : list.keySet()) {
            if (list.get(d).equals(currentTarget)) {
                result.add(d);
            }
        }
        return result;
    }
}
