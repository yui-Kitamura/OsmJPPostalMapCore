package pro.eng.yui.oss.osm.lib.jppostalcore.api.osm;

import org.junit.jupiter.api.Test;
import pro.eng.yui.oss.osm.lib.jppostalcore.types.OsmPoi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CreateXMLTest {

    @Test
    void testCreateChangeset() {
        Map<String, String> tags = new HashMap<>();
        tags.put("tag1", "val1");
        tags.put("special", "& < > \" '");
        ChangeSetInfo info = new ChangeSetInfo(123, "comment & more", "editor <name>", tags);

        String xml = CreateXML.createChangeset(info);

        assertTrue(xml.contains("<tag k=\"created_by\" v=\"editor &lt;name&gt;\"/>"));
        assertTrue(xml.contains("<tag k=\"comment\" v=\"comment &amp; more\"/>"));
        assertTrue(xml.contains("<tag k=\"tag1\" v=\"val1\"/>"));
        assertTrue(xml.contains("<tag k=\"special\" v=\"&amp; &lt; &gt; &quot; &apos;\"/>"));
        assertTrue(xml.endsWith("</osm>"));
    }

    @Test
    void testCreateElement() {
        ChangeSetInfo info = new ChangeSetInfo(123, "cmt", "edt", null);
        Map<String, String> tags = new HashMap<>();
        tags.put("name", "POI & Test");
        OsmPoi poi = new OsmPoi(35.0, 135.0, "node", tags);

        String xml = CreateXML.createElement(info, poi);

        assertTrue(xml.contains("<node id=\"0\" lat=\"35.0\" lon=\"135.0\" >"));
        assertTrue(xml.contains("<tag k=\"name\" v=\"POI &amp; Test\"/>"));
        
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        assertTrue(xml.contains("<tag k=\"check_date\" v=\"" + today + "\"/>"));
        assertTrue(xml.contains("</node>"));
    }

    @Test
    void testModifyElement() {
        ChangeSetInfo info = new ChangeSetInfo(456, "cmt", "edt", null);
        Map<String, String> tags = new HashMap<>();
        tags.put("k\">", "v&");
        OsmPoi poi = new OsmPoi(789, 35.1, 135.1, "way", tags, 5);

        String xml = CreateXML.modifyElement(info, poi);

        assertTrue(xml.contains("<way id=\"789\" version=\"5\" changeset=\"456\" >"));
        assertTrue(xml.contains("<tag k=\"k&quot;&gt;\" v=\"v&amp;\"/>"));
        assertTrue(xml.contains("</way>"));
    }
}
