package pro.eng.yui.oss.osm.lib.jppostalcore.api.overpass;

public class OverpassQuery {
    
    private OverpassQuery(){ /* util */ }
    
    /** 都道府県単位で抽出 */
    public static String getPostSearchQuery(String prefName){
        return
            "area[\"boundary\"=\"administrative\"][\"admin_level\"=\"4\"][\"name\"=\""+ prefName +"\"]->.a;"+
            "(" +
            "  node(area.a)[\"amenity\"=\"post_box\"];" +
            "  nw(area.a)[\"amenity\"=\"post_office\"][!\"operator\"];" +
            "  nw(area.a)[\"amenity\"=\"post_office\"][\"operator\"=\"日本郵便\"];" +
            ");";
    }
}
