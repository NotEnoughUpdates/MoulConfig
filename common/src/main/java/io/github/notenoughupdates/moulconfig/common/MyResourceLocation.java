package io.github.notenoughupdates.moulconfig.common;

import java.util.Objects;

public final class MyResourceLocation {
    private final String root;
    private final String path;

    public MyResourceLocation(String root, String path) {
        if (root.contains(":")) {
            throw new IllegalArgumentException("Root must not contain ':'");
        }
        if (path.contains(":")) {
            throw new IllegalArgumentException("Path must not contain ':'");
        }
        this.root = root;
        this.path = path;
    }

    public String getRoot() {
        return root;
    }

    public String getPath() {
        return path;
    }

    public static MyResourceLocation parse(String string) {
        String[] split = string.split(":", -1);
        if (split.length == 1) {
            return new MyResourceLocation("minecraft", split[0]);
        }
        if (split.length == 2) {
            return new MyResourceLocation(split[0], split[1]);
        }
        throw new IllegalArgumentException("Resource location has to be in the format `namespace:path`, with `namespace:` being optional");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyResourceLocation)) return false;
        MyResourceLocation that = (MyResourceLocation) o;
        return Objects.equals(root, that.root) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root, path);
    }

    @Override
    public String toString() {
        return "MyResourceLocation(root=" + root + ", path=" + path + ")";
    }
}
