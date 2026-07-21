package pro.eng.yui.oss.osm.lib.jppostalcore.api.osm;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ChangeSetInfo {
    
    private final long id;
    private final String comment;
    private final String createdBy;
    private final Map<String,String> otherTags;

    public ChangeSetInfo(long id, String comment, String createdBy, Map<String, String> otherTags) {
        this.id = id;
        this.comment = comment;
        this.createdBy = createdBy;
        this.otherTags = otherTags;
    }
    
    public long getId(){
        return id;
    }
    public String getComment(){
        return comment;
    }
    public String getEditor(){
        return createdBy;
    }
    @NotNull
    public Map<String,String> getTags(){
        return otherTags != null ? Map.copyOf(otherTags) : Map.of();
    }
}
