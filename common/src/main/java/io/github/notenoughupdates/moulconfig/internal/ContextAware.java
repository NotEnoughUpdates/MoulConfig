package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.processor.HasDebugLocation;

import java.lang.reflect.Field;

/**
 * Internal utility class for making stacktraces aware of the mod-site error location of a stacktrace.
 */
public class ContextAware {

    public static <T> T wrapErrorWithContext(Field field, ContextAwareRunnable<T> runnable) {
        return wrapErrorWithContext(field != null ? field::toString : null, runnable);
    }

    public static <T> T wrapErrorWithContext(HasDebugLocation debugLocation, ContextAwareRunnable<T> runnable) {
        try {
            return runnable.run();
        } catch (Exception e) {
            throw new ContextualException(e, debugLocation != null ? debugLocation.getDebugDeclarationLocation() : null);
        }
    }

    @FunctionalInterface
    public interface ContextAwareRunnable<T> {
        T run() throws Exception;
    }

    public static class ContextualException extends RuntimeException {
        public ContextualException(Exception exception, String codeLocation) {
            super("Editor at " + (codeLocation == null ? "<unknown code location>" : codeLocation) + " crashed: " + exception.getMessage(), exception);
        }
    }
}
