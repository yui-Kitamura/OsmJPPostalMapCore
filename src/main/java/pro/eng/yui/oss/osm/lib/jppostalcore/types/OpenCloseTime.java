package pro.eng.yui.oss.osm.lib.jppostalcore.types;

public class OpenCloseTime implements ITagPart{
    public final TimeStr openAt;
    public final TimeStr closeAt;
    
    public OpenCloseTime(String from, String to){
        this.openAt = new TimeStr(from);
        this.closeAt = new TimeStr(to);
    }
    
    /** @return 0:00-12:00 formatted string */
    public String toPairStr(){
        return openAt.value + "-" + closeAt.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        OpenCloseTime other = (OpenCloseTime) obj;
        return openAt.equals(other.openAt) && closeAt.equals(other.closeAt);
    }
}
