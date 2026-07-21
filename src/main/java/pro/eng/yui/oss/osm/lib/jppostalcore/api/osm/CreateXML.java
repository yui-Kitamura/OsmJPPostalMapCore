package pro.eng.yui.oss.osm.lib.jppostalcore.api.osm;

import pro.eng.yui.oss.osm.lib.jppostalcore.types.OsmPoi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class CreateXML {
    
    /** XMLパースエラーを起こしうる文字のエスケープ */
    private static String escapeXml(String origin){
        if (origin == null) return "";
        return origin.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
    
    public static String createChangeset(ChangeSetInfo info){
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<osm version=\"0.6\" generator=\"OsmJPPostalMap\">\n");
        xml.append("  <changeset>\n");
        xml.append("    <tag k=\"created_by\" v=\"").append(escapeXml(info.getEditor())).append("\"/>\n");
        xml.append("    <tag k=\"comment\" v=\"").append(escapeXml(info.getComment())).append("\"/>\n");

        for (var entry : info.getTags().entrySet()) {
            xml.append("    <tag k=\"").append(escapeXml(entry.getKey()))
                    .append("\" v=\"").append(escapeXml(entry.getValue())).append("\"/>\n");
        }

        xml.append("  </changeset>\n");
        xml.append("</osm>");
        return xml.toString();
    }
    
    public static String createElement(ChangeSetInfo id, OsmPoi newPoi){
        return changePoiXml(id, newPoi, false);
    }
    
    public static String modifyElement(ChangeSetInfo id, OsmPoi updPoi){
        return changePoiXml(id, updPoi, true);
    }
    
    private static String changePoiXml(ChangeSetInfo id, OsmPoi poi, boolean isUpdate){
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<osm version=\"0.6\" generator=\"OsmJPPostalMap\">\n");
        xml.append("  <").append(poi.getType()).append(" ")
                .append("id=\"").append(poi.getId()).append("\" ");
        if (isUpdate) {
            xml.append("version=\"").append(poi.getVer()).append("\" ")
                    .append("changeset=\"").append(id.getId()).append("\" ");
        }
        if ("node".equals(poi.getType())) {
            xml.append("lat=\"").append(poi.getLat())
                    .append("\" lon=\"").append(poi.getLon()).append("\" ");
        }
        xml.append(">\n");

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        Map<String, String> tags = poi.getTags();
        tags.put("check_date", today);

        for (Map.Entry<String, String> entry : tags.entrySet()) {
            xml.append("    <tag k=\"").append(escapeXml(entry.getKey()))
                    .append("\" v=\"").append(escapeXml(entry.getValue())).append("\"/>\n");
        }
        xml.append("  </").append(poi.getType()).append(">\n");
        xml.append("</osm>");
        return xml.toString();
    }
}
