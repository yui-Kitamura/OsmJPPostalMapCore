package pro.eng.yui.oss.osm.lib.jppostalcore.types;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JpAddressTest {

    @Test
    void testQuarterValidation() {
        JpAddress addr = new JpAddress();

        // 1. quarterがあり、neighbourhoodがある場合 -> YES
        addr.setQuarter("大字テスト");
        addr.setNeighbourhood("小字テスト");
        assertEquals(JpAddress.Avail.YES, addr.isQuarterAvail(), "Should be YES when both are present");

        // 2. quarterがあり、neighbourhoodがない場合 -> NO
        JpAddress addr2 = new JpAddress();
        addr2.setQuarter("大字テスト");
        assertEquals(JpAddress.Avail.NO, addr2.isQuarterAvail(), "Should be NO when neighbourhood is missing");

        // 3. 後からneighbourhoodをセットした場合 -> YESに変わる
        addr2.setNeighbourhood("小字テスト");
        assertEquals(JpAddress.Avail.YES, addr2.isQuarterAvail(), "Should change to YES when neighbourhood is added");

        // 4. neighbourhoodを消した場合 -> NOに戻る
        addr2.setNeighbourhood(null);
        assertEquals(JpAddress.Avail.NO, addr2.isQuarterAvail(), "Should return to NO when neighbourhood is removed");
    }
}
