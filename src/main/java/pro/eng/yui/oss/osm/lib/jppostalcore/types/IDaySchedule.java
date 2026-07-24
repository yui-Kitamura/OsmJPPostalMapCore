package pro.eng.yui.oss.osm.lib.jppostalcore.types;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IDaySchedule {
    
    public IDayStatus status();
    @NotNull
    public List<? extends ITagPart> schedule();
}
