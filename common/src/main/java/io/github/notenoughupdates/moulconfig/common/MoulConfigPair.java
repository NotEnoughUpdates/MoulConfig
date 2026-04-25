package io.github.notenoughupdates.moulconfig.common;

import java.util.Objects;

public final class MoulConfigPair<A, B> {
    private final A first;
    private final B second;

    public MoulConfigPair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoulConfigPair)) return false;
        MoulConfigPair<?, ?> that = (MoulConfigPair<?, ?>) o;
        return Objects.equals(first, that.first) && Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
