package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import org.junit.jupiter.api.Test;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CollectionTimeParserTest {

    @Test
    void decodeBasic() {
        CollectionTimes input = new CollectionTimes("Mo-Fr 10:00,17:00; Sa-Su,PH 9:00");
        
        CollectionTimeParser parser = new CollectionTimeParser();
        Map<Days, CollectionTimeParser.DaySchedule> result = parser.decode(input);

        assertEquals(8, result.size());
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.MONDAY).status());
        assertEquals(new CollectionTime("10:00"), result.get(Days.MONDAY).schedule().get(0));
        assertEquals(new CollectionTime("17:00"), result.get(Days.MONDAY).schedule().get(1));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.TUESDAY).status());
        assertEquals(new CollectionTime("10:00"), result.get(Days.TUESDAY).schedule().get(0));
        assertEquals(new CollectionTime("17:00"), result.get(Days.TUESDAY).schedule().get(1));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.WEDNESDAY).status());
        assertEquals(new CollectionTime("10:00"), result.get(Days.WEDNESDAY).schedule().get(0));
        assertEquals(new CollectionTime("17:00"), result.get(Days.WEDNESDAY).schedule().get(1));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.THURSDAY).status());
        assertEquals(new CollectionTime("10:00"), result.get(Days.THURSDAY).schedule().get(0));
        assertEquals(new CollectionTime("17:00"), result.get(Days.THURSDAY).schedule().get(1));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.FRIDAY).status());
        assertEquals(new CollectionTime("10:00"), result.get(Days.FRIDAY).schedule().get(0));
        assertEquals(new CollectionTime("17:00"), result.get(Days.FRIDAY).schedule().get(1));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.SATURDAY).status());
        assertEquals(new CollectionTime("9:00"), result.get(Days.SATURDAY).schedule().get(0));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.SUNDAY).status());
        assertEquals(new CollectionTime("9:00"), result.get(Days.SUNDAY).schedule().get(0));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.PUBLIC_HOLIDAY).status());
        assertEquals(new CollectionTime("9:00"), result.get(Days.PUBLIC_HOLIDAY).schedule().get(0));
    }

    @Test
    void decodeBasicWithSpace() {
        CollectionTimes input = new CollectionTimes("Mo-Fr 10:00, 15:00; Sa-Su,PH 11:00");

        CollectionTimeParser parser = new CollectionTimeParser();
        Map<Days, CollectionTimeParser.DaySchedule> result = parser.decode(input);

        assertEquals(8, result.size());
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.MONDAY).status());
        assertEquals(new CollectionTime("10:00"), result.get(Days.MONDAY).schedule().get(0));
        assertEquals(new CollectionTime("15:00"), result.get(Days.MONDAY).schedule().get(1));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.TUESDAY).status());
        assertEquals(new CollectionTime("10:00"), result.get(Days.TUESDAY).schedule().get(0));
        assertEquals(new CollectionTime("15:00"), result.get(Days.TUESDAY).schedule().get(1));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.WEDNESDAY).status());
        assertEquals(new CollectionTime("10:00"), result.get(Days.WEDNESDAY).schedule().get(0));
        assertEquals(new CollectionTime("15:00"), result.get(Days.WEDNESDAY).schedule().get(1));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.THURSDAY).status());
        assertEquals(new CollectionTime("10:00"), result.get(Days.THURSDAY).schedule().get(0));
        assertEquals(new CollectionTime("15:00"), result.get(Days.THURSDAY).schedule().get(1));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.FRIDAY).status());
        assertEquals(new CollectionTime("10:00"), result.get(Days.FRIDAY).schedule().get(0));
        assertEquals(new CollectionTime("15:00"), result.get(Days.FRIDAY).schedule().get(1));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.SATURDAY).status());
        assertEquals(new CollectionTime("11:00"), result.get(Days.SATURDAY).schedule().get(0));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.SUNDAY).status());
        assertEquals(new CollectionTime("11:00"), result.get(Days.SUNDAY).schedule().get(0));
        assertEquals(CollectionTimeParser.DayStatus.OPEN_DAY, result.get(Days.PUBLIC_HOLIDAY).status());
        assertEquals(new CollectionTime("11:00"), result.get(Days.PUBLIC_HOLIDAY).schedule().get(0));
    }

    @Test
    void encodeBasic(){
        CollectionTimes input = new CollectionTimes("Mo-Fr 10:00,17:00; Sa-Su,PH 9:00");

        CollectionTimeParser parser = new CollectionTimeParser();
        Map<Days, CollectionTimeParser.DaySchedule> result = parser.decode(input);

        assertEquals(input.getOrigin(), parser.encode(result).getOrigin());
    }

    @Test
    void decodeOverrideOnWeekdays(){
        CollectionTimes input = new CollectionTimes("Mo-Fr 9:00,17:00; Mo,We 9:00,12:00");
        CollectionTimeParser parser = new CollectionTimeParser();
        Map<Days, CollectionTimeParser.DaySchedule> result = parser.decode(input);
        assertEquals(new CollectionTime("9:00"), result.get(Days.MONDAY).schedule().get(0));
        assertEquals(new CollectionTime("12:00"), result.get(Days.MONDAY).schedule().get(1));
        assertEquals(new CollectionTime("9:00"), result.get(Days.WEDNESDAY).schedule().get(0));
        assertEquals(new CollectionTime("12:00"), result.get(Days.WEDNESDAY).schedule().get(1));
        assertEquals(new CollectionTime("9:00"), result.get(Days.FRIDAY).schedule().get(0));
        assertEquals(new CollectionTime("17:00"), result.get(Days.FRIDAY).schedule().get(1));
    }
}