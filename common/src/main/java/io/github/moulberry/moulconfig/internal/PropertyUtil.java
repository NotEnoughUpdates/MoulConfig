package io.github.moulberry.moulconfig.internal;

public class PropertyUtil {
    public static boolean getBooleanWithFallback(String name, boolean fallback) {
        if (System.getProperties().contains(name))
            return Boolean.getBoolean(name);
        return fallback;
    }
}
