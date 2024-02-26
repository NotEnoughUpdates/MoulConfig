package io.github.notenoughupdates.moulconfig.internal;

import java.util.List;

public class CollectionUtils {

    public static <T> T getSingleOrThrow(List<? extends T> list) {
        if (list.size() != 1)
            throw new IllegalStateException("Expected a list of exactly length 1");
        return list.get(0);
    }
}
