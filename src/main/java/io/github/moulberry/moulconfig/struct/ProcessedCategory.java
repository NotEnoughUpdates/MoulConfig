package io.github.moulberry.moulconfig.struct;

import java.util.ArrayList;
import java.util.List;

public class ProcessedCategory {
    public final String field;
    public final String name;
    public final String desc;
    public final List<ProcessedOption> options = new ArrayList<>();

    public ProcessedCategory(String field, String name, String desc) {
        this.field = field;
        this.name = name;
        this.desc = desc;
    }
}
