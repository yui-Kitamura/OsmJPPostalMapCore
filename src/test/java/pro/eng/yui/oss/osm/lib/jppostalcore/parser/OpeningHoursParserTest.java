package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import org.junit.jupiter.api.Test;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.Days;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.OpenCloseTime;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.OpeningHours;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OpeningHoursParserTest {
    
    @Test
    void decodeBasic(){
        OpeningHours input = new OpeningHours("Mo-Fr 10:00-17:00; Sa-Su,PH off");
        
        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);
        
        assertEquals(8, result.size());
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.MONDAY).status);
        assertEquals(new OpenCloseTime("10:00","17:00"), result.get(Days.MONDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.TUESDAY).status);
        assertEquals(new OpenCloseTime("10:00","17:00"), result.get(Days.TUESDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.WEDNESDAY).status);
        assertEquals(new OpenCloseTime("10:00","17:00"), result.get(Days.WEDNESDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.THURSDAY).status);
        assertEquals(new OpenCloseTime("10:00","17:00"), result.get(Days.THURSDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.FRIDAY).status);
        assertEquals(new OpenCloseTime("10:00","17:00"), result.get(Days.FRIDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.CLOSED_DAY, result.get(Days.SATURDAY).status);
        assertEquals(0, result.get(Days.SATURDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.CLOSED_DAY, result.get(Days.SUNDAY).status);
        assertEquals(0, result.get(Days.SUNDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.CLOSED_DAY, result.get(Days.PUBLIC_HOLIDAY).status);
        assertEquals(0, result.get(Days.PUBLIC_HOLIDAY).schedule.size());
    }

    @Test
    void decodeDayLess(){
        OpeningHours input = new OpeningHours("10:00-17:00; Sa-Su,PH off");

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);

        assertEquals(8, result.size());
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.MONDAY).status);
        assertEquals(new OpenCloseTime("10:00","17:00"), result.get(Days.MONDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.TUESDAY).status);
        assertEquals(new OpenCloseTime("10:00","17:00"), result.get(Days.TUESDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.WEDNESDAY).status);
        assertEquals(new OpenCloseTime("10:00","17:00"), result.get(Days.WEDNESDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.THURSDAY).status);
        assertEquals(new OpenCloseTime("10:00","17:00"), result.get(Days.THURSDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.FRIDAY).status);
        assertEquals(new OpenCloseTime("10:00","17:00"), result.get(Days.FRIDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.CLOSED_DAY, result.get(Days.SATURDAY).status);
        assertEquals(0, result.get(Days.SATURDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.CLOSED_DAY, result.get(Days.SUNDAY).status);
        assertEquals(0, result.get(Days.SUNDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.CLOSED_DAY, result.get(Days.PUBLIC_HOLIDAY).status);
        assertEquals(0, result.get(Days.PUBLIC_HOLIDAY).schedule.size());
    }

    @Test
    void decodeLest(){
        OpeningHours input = new OpeningHours("Mo-Fr 10:00-12:00, 13:30-17:00; Sa-Su 10:00-12:00; PH off;");

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);

        assertEquals(8, result.size());
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.MONDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.MONDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","17:00"), result.get(Days.MONDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.TUESDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.TUESDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","17:00"), result.get(Days.TUESDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.WEDNESDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.WEDNESDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","17:00"), result.get(Days.WEDNESDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.THURSDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.THURSDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","17:00"), result.get(Days.THURSDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.FRIDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.FRIDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","17:00"), result.get(Days.FRIDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.SATURDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.SATURDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.SUNDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.SUNDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.CLOSED_DAY, result.get(Days.PUBLIC_HOLIDAY).status);
        assertEquals(0, result.get(Days.PUBLIC_HOLIDAY).schedule.size());
    }

    @Test
    void decodeOverride(){
        OpeningHours input = new OpeningHours("Mo-Su 10:00-12:00, 13:30-17:00; Sa-Su 10:00-12:00; PH off;");

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);

        assertEquals(8, result.size());
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.MONDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.MONDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","17:00"), result.get(Days.MONDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.TUESDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.TUESDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","17:00"), result.get(Days.TUESDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.WEDNESDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.WEDNESDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","17:00"), result.get(Days.WEDNESDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.THURSDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.THURSDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","17:00"), result.get(Days.THURSDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.FRIDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.FRIDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","17:00"), result.get(Days.FRIDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.SATURDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.SATURDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.SUNDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.SUNDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.CLOSED_DAY, result.get(Days.PUBLIC_HOLIDAY).status);
        assertEquals(0, result.get(Days.PUBLIC_HOLIDAY).schedule.size());
    }

    @Test
    void decode24_7(){
        OpeningHours input = new OpeningHours("24/7");

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);

        assertEquals(8, result.size());
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.MONDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.MONDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.TUESDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.TUESDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.WEDNESDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.WEDNESDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.THURSDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.THURSDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.FRIDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.FRIDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.SATURDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.SATURDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.SUNDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.SUNDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.PUBLIC_HOLIDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.PUBLIC_HOLIDAY).schedule.get(0));
    }

    @Test
    void decode24_7Override(){
        OpeningHours input = new OpeningHours("24/7; PH off;");

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);

        assertEquals(8, result.size());
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.MONDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.MONDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.TUESDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.TUESDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.WEDNESDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.WEDNESDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.THURSDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.THURSDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.FRIDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.FRIDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.SATURDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.SATURDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.OPEN24, result.get(Days.SUNDAY).status);
        assertEquals(new OpenCloseTime("0:00","24:00"), result.get(Days.SUNDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.CLOSED_DAY, result.get(Days.PUBLIC_HOLIDAY).status);
        assertEquals(0, result.get(Days.PUBLIC_HOLIDAY).schedule.size());
    }

    @Test
    void decodeCrossWeek(){
        OpeningHours input = new OpeningHours("Fr-Tu 10:00-12:00, 13:30-03:00");

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);

        assertEquals(8, result.size());
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.MONDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.MONDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","03:00"), result.get(Days.MONDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.TUESDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.TUESDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","03:00"), result.get(Days.TUESDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.UNKNOWN, result.get(Days.WEDNESDAY).status);
        assertEquals(0, result.get(Days.WEDNESDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.UNKNOWN, result.get(Days.THURSDAY).status);
        assertEquals(0, result.get(Days.THURSDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.FRIDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.FRIDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","03:00"), result.get(Days.FRIDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.SATURDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.SATURDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","03:00"), result.get(Days.SATURDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.SUNDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.SUNDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","03:00"), result.get(Days.SUNDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.UNKNOWN, result.get(Days.PUBLIC_HOLIDAY).status);
        assertEquals(0, result.get(Days.PUBLIC_HOLIDAY).schedule.size());
    }

    @Test
    void decodeCrossWeekAndHoliday(){
        OpeningHours input = new OpeningHours("Fr-Tu 10:00-12:00, 13:30-03:00; PH 10:00-24:00");

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);

        assertEquals(8, result.size());
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.MONDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.MONDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","03:00"), result.get(Days.MONDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.TUESDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.TUESDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","03:00"), result.get(Days.TUESDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.UNKNOWN, result.get(Days.WEDNESDAY).status);
        assertEquals(0, result.get(Days.WEDNESDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.UNKNOWN, result.get(Days.THURSDAY).status);
        assertEquals(0, result.get(Days.THURSDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.FRIDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.FRIDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","03:00"), result.get(Days.FRIDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.SATURDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.SATURDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","03:00"), result.get(Days.SATURDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.SUNDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.SUNDAY).schedule.get(0));
        assertEquals(new OpenCloseTime("13:30","03:00"), result.get(Days.SUNDAY).schedule.get(1));
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.PUBLIC_HOLIDAY).status);
        assertEquals(new OpenCloseTime("10:00","24:00"), result.get(Days.PUBLIC_HOLIDAY).schedule.get(0));
    }

    @Test
    void decodeLessData(){
        OpeningHours input = new OpeningHours("Mo 10:00-12:00");

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);

        assertEquals(8, result.size());
        assertEquals(OpeningHoursParser.DayStatus.OPEN_DAY, result.get(Days.MONDAY).status);
        assertEquals(new OpenCloseTime("10:00","12:00"), result.get(Days.MONDAY).schedule.get(0));
        assertEquals(OpeningHoursParser.DayStatus.UNKNOWN, result.get(Days.TUESDAY).status);
        assertEquals(0, result.get(Days.TUESDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.UNKNOWN, result.get(Days.WEDNESDAY).status);
        assertEquals(0, result.get(Days.WEDNESDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.UNKNOWN, result.get(Days.THURSDAY).status);
        assertEquals(0, result.get(Days.THURSDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.UNKNOWN, result.get(Days.FRIDAY).status);
        assertEquals(0, result.get(Days.FRIDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.UNKNOWN, result.get(Days.SATURDAY).status);
        assertEquals(0, result.get(Days.SATURDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.UNKNOWN, result.get(Days.SUNDAY).status);
        assertEquals(0, result.get(Days.SUNDAY).schedule.size());
        assertEquals(OpeningHoursParser.DayStatus.UNKNOWN, result.get(Days.PUBLIC_HOLIDAY).status);
        assertEquals(0, result.get(Days.PUBLIC_HOLIDAY).schedule.size());
    }

    @Test
    void decodeCalendar(){
        OpeningHours input = new OpeningHours("10:00-12:00;Jan 1 off;");
        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = assertDoesNotThrow(()->parser.decode(input));
        assertNull(result);
    }

    @Test
    void decodeIllegal(){
        OpeningHours input = new OpeningHours(";;PH off");

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);
        assertNull(result);
    }
    
    @Test 
    void decodeIllegalEmpty(){
        OpeningHours input = new OpeningHours("");
        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = assertDoesNotThrow(()->parser.decode(input));
        assertNull(result);
    }
    @Test
    void decodeIllegalNoHour(){
        OpeningHours input = new OpeningHours("Mo-Fr");
        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = assertDoesNotThrow(()->parser.decode(input));
        assertNull(result);
    }
    @Test
    void decodeIllegalNoEnd(){
        OpeningHours input = new OpeningHours("Mo-Fr 10:00");
        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = assertDoesNotThrow(()->parser.decode(input));
        assertNull(result); //fail
    }
    @Test
    void decodeIllegalNoEndButHy(){
        OpeningHours input = new OpeningHours("Mo-Fr 10:00-");
        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = assertDoesNotThrow(()->parser.decode(input));
        assertNull(result); //fail
    }
    @Test
    void decodeIllegalWeekDaySymbol(){
        OpeningHours input = new OpeningHours("wd 10:00-12:00");
        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = assertDoesNotThrow(()->parser.decode(input));
        assertNull(result);
    }
    
    @Test
    void encodeBasic(){
        OpeningHours input = new OpeningHours("Mo-Fr 10:00-17:00; Sa-Su,PH off");

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);
        
        assertEquals(input.getOrigin(), parser.encode(result).getOrigin());
    }

    @Test
    void encode24_7(){
        OpeningHours input = new OpeningHours("24/7");

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);

        assertEquals(input.getOrigin(), parser.encode(result).getOrigin());
    }

    @Test
    void encodeBasicSame(){
        OpeningHours input = new OpeningHours("Mo-Su,PH 10:00-17:00");

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);

        assertEquals(input.getOrigin(), parser.encode(result).getOrigin());
    }

    @Test
    void encodeMissedDay(){
        OpeningHours input = new OpeningHours("Mo-Fr 10:00-17:00; PH off"); //Sa-Su UNKNOWN

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);

        assertEquals(input.getOrigin(), parser.encode(result).getOrigin());
    }

    @Test
    void encodeMissedDayMid(){
        OpeningHours input = new OpeningHours("Mo-Tu,Fr-Sa 10:00-17:00; PH off"); //Sa-Su UNKNOWN

        OpeningHoursParser parser = new OpeningHoursParser();
        Map<Days, OpeningHoursParser.DaySchedule> result = parser.decode(input);

        assertEquals(input.getOrigin(), parser.encode(result).getOrigin());
    }
    
}