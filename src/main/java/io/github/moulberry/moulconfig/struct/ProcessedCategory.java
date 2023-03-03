package io.github.moulberry.moulconfig.struct;

import java.util.LinkedHashMap;

public class ProcessedCategory {
    public final String field;
    public final String name;
    public final String desc;
    public final LinkedHashMap<String, ProcessedOption> options = new LinkedHashMap<>();

    public ProcessedCategory(String field, String name, String desc) {
        this.field = field;
        this.name = name;
        this.desc = desc;
    }
}
