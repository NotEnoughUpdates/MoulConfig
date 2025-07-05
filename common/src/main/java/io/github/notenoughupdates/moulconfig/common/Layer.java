package io.github.notenoughupdates.moulconfig.common;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@AllArgsConstructor
public class Layer implements Comparable<Layer> {
    /**
     * The sort index of this layer. Higher numbers render on top. Note that this does not correspond to a z offset.
     */
    int sortIndex;

    public Layer next() {
        return new Layer(sortIndex + 1);
    }

    public static final Layer ROOT = new Layer(0);
    public static final Layer TOOLTIP = new Layer(300);
    public static final Layer OVERLAY = new Layer(100);

    @Override
    public int compareTo(@NotNull Layer o) {
        return Integer.compare(this.sortIndex, o.sortIndex);
    }
}
