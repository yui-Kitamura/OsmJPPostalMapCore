package pro.eng.yui.oss.osm.lib.jppostalcore.api.overpass;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Overpass APIのJSONレスポンスをマッピングするクラス
 */
public class OverpassResponse {
    @SerializedName("elements")
    private List<Element> elements;

    public List<Element> getElements() {
        return elements;
    }

    public static class Element {
        private long id;
        private double lat;
        private double lon;
        private String type;
        private Center center;
        private Map<String, String> tags;
        private long version;

        public long getId() { return id; }
        public double getLat() { return lat; }
        public double getLon() { return lon; }
        public String getType() { return type; }
        public Center getCenter() { return center; }
        public Map<String, String> getTags() { return tags; }
        public long getVersion() { return version; }
    }

    public static class Center {
        private double lat;
        private double lon;

        public double getLat() { return lat; }
        public double getLon() { return lon; }
    }
}
