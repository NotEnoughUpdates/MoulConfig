package io.github.notenoughupdates.moulconfig.internal;

import lombok.SneakyThrows;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class InitUtil {
    @FunctionalInterface
    public interface ThrowingRunnable<T extends Throwable> {
        void run() throws T;
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T, E extends Throwable> {
        T get() throws E;
    }

    /**
     * Utility method to run code during a {@code super} or {@code this} call.
     */
    public static <T, E extends Throwable> T run(T value, ThrowingRunnable<E> check) throws E {
        check.run();
        return value;
    }

    @SneakyThrows
    public static <T> T makeUnchecked(ThrowingSupplier<T, Exception> supplier) {
        return supplier.get();
    }

    public static <T> T make(T obj, Consumer<T> constructor) {
        constructor.accept(obj);
        return obj;
    }

    public static <T> T make(Supplier<T> constructor) {
        return constructor.get();
    }

}
