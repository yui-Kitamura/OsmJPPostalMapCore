package pro.eng.yui.oss.osm.lib.jppostalcore.types;

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
