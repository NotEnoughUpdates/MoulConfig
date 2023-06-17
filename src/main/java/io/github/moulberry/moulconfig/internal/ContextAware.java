package io.github.moulberry.moulconfig.internal;

import io.github.moulberry.moulconfig.gui.GuiOptionEditor;

import java.lang.reflect.Field;

/**
 * Internal utility class for making stacktraces aware of the mod-site error location of a stacktrace.
 */
public class ContextAware {

    public static <T> T wrapErrorWithContext(GuiOptionEditor editor, ContextAwareRunnable<T> runnable) {
        try {
            return runnable.run();
        } catch (Exception e) {
            throw new ContextualException(e, (editor != null && editor.getOption() != null) ? editor.getOption().field : null);
        }
    }

    public static <T> T wrapErrorWithContext(Field field, ContextAwareRunnable<T> runnable) {
        try {
            return runnable.run();
        } catch (Exception e) {
            throw new ContextualException(e, field);
        }
    }

    @FunctionalInterface
    public interface ContextAwareRunnable<T> {
        T run() throws Exception;
    }

    public static class ContextualException extends RuntimeException {
        public ContextualException(Exception exception, Field field) {
            super("Editor at field " + (field == null ? "<null>" : field) + " crashed: " + exception.getMessage(), exception);
        }
    }
}
