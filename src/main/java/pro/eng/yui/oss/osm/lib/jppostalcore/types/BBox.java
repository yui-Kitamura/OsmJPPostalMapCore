package pro.eng.yui.oss.osm.lib.jppostalcore.types;

/**
 * 境界ボックス(Bounding Box)を表すクラス。
 */
public class BBox {
    private final double minLat;
    private final double minLon;
    private final double maxLat;
    private final double maxLon;

    public BBox(double minLat, double minLon, double maxLat, double maxLon) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
    }

    public double getMinLat() { return minLat; }
    public double getMinLon() { return minLon; }
    public double getMaxLat() { return maxLat; }
    public double getMaxLon() { return maxLon; }

    @Override
    public String toString() {
        return minLat + "," + minLon + "," + maxLat + "," + maxLon;
    }
}
