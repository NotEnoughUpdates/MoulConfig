package io.github.notenoughupdates.moulconfig.internal;

public class PropertyUtil {
    public static boolean getBooleanWithFallback(String name, boolean fallback) {
        if (System.getProperties().containsKey(name))
            return Boolean.getBoolean(name);
        return fallback;
    }
}
