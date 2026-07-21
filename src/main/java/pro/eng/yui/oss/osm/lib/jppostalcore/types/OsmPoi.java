package pro.eng.yui.oss.osm.lib.jppostalcore.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.awt.image.Kernel;
import java.util.HashMap;
import java.util.Map;

/**
 * OpenStreetMapのPOI(Point of Interest)を表す基底モデル。
 * ノード(node)とウェイ(way)の両方をサポートし、タグ情報をMap形式で保持します。
 */
public class OsmPoi {
    private final long id;
    private final double lat;
    private final double lon;
    private final String type; // "node" or "way"
    private final Map<String, String> tags;
    private final long ver;

    /**
     * 既存POI
     * @param id OSM要素ID
     * @param lat 緯度
     * @param lon 経度
     * @param type 要素タイプ ("node" または "way")
     * @param tags タグのマップ
     */
    public OsmPoi(long id, double lat, double lon, String type, Map<String, String> tags, long ver) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.tags = tags;
        this.ver = ver;
    }
    public OsmPoi(JsonObject json){
        this.id = json.get("id").getAsLong();
        this.lat = json.get("lat").getAsDouble();
        this.lon = json.get("lon").getAsDouble();
        this.type = json.get("type").getAsString();
        this.ver = json.get("ver").getAsLong();
        Map<String, JsonElement> tags = json.get("tags").getAsJsonObject().asMap();
        Map<String, String> tagStr = new HashMap<>();
        for (String k : tags.keySet()) {
            tagStr.put(k,tags.get(k).getAsString());
        }
        this.tags = tagStr;
    }
    /** 新規POI */
    public OsmPoi(double lat, double lon, String type, Map<String, String> tags){
        this.id = 0; this.ver = 0;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.tags = tags;
    }
    /** 新規POIの情報更新
     * @return 作成された新しいPOI情報 */
    public OsmPoi created(long createdId, long ver){
        if (id != 0 || ver != 0){ throw new IllegalStateException("already set"); }
        return new OsmPoi(createdId, lat, lon, type, tags, ver);
    }
    
    /** OSMのPOI-ID */
    public long getId() { return id; }
    /** nodeまたはwayのcenter緯度 */
    public double getLat() { return lat; }
    /** nodeまたはwayのcenter経度 */
    public double getLon() { return lon; }
    /** node or way */
    public String getType() { return type; }
    /** POIに設定されているすべてのタグ */
    public Map<String, String> getTags() { return tags; }
    /** POIに設定されているタグの値
     * @param key タグのキー文字列
     * @return 設定されている場合その値。無い場合<code>null</code>*/
    public String getTag(String key) {
        return tags != null ? tags.get(key) : null;
    }
    /** 取得時点のPOIバージョン */
    public long getVer(){ return ver; }

}
