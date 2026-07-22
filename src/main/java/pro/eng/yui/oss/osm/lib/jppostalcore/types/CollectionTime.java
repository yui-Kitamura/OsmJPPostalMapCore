package pro.eng.yui.oss.osm.lib.jppostalcore.types;

public class CollectionTime {

    public final TimeStr collectAt;

    public CollectionTime(String at){
        this.collectAt = new TimeStr(at);
    }

    public String toString(){
        return collectAt.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CollectionTime other = (CollectionTime) obj;
        return collectAt.equals(other.collectAt);
    }
}
