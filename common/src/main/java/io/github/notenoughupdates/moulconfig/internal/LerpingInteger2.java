package io.github.notenoughupdates.moulconfig.internal;

import lombok.ToString;

@ToString
public class LerpingInteger2 {
    private int value;
    private int target;
    private final int speed;
    private final int scale;
    private long lastUpdate = System.currentTimeMillis();

    public LerpingInteger2(int value, int speed, int scale) {
        this.value = this.target = value;
        this.speed = speed;
        this.scale = scale;
    }

    public void setTarget(int target) {
        this.target = target * scale;
    }

    public void update() {
        int d = target - value;
        long currentTime = System.currentTimeMillis();
        int distance = (int) (currentTime - lastUpdate) * speed;
        lastUpdate = currentTime;
        value += MathUtil.copySign(d, Math.min(distance, Math.abs(d)));
    }

    public int getValue() {
        update();
        return value / scale;
    }

}
