package io.github.notenoughupdates.moulconfig.internal;

public class InitUtil {
    @FunctionalInterface
    public interface ThrowingRunnable<T extends Throwable> {
        void run() throws T;
    }

    /**
     * Utility method to run code during a {@code super} or {@code this} call.
     */
    public static <T, E extends Throwable> T run(T value, ThrowingRunnable<E> check) throws E {
        check.run();
        return value;
    }

}
