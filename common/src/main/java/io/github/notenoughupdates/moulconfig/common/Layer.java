package io.github.notenoughupdates.moulconfig.common;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Layer {
    /**
     * The sort index of this layer. Higher numbers render on top. Note that this does not correspond to a z offset.
     */
    int sortIndex;

    public static final Layer ROOT = new Layer(0);
    public static final Layer TOOLTIP = new Layer(300);
    public static final Layer OVERLAY = new Layer(100);
}
