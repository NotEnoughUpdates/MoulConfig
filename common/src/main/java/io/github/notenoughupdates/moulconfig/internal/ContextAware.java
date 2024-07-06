package io.github.notenoughupdates.moulconfig.internal;

import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor;

import java.lang.reflect.Field;

/**
 * Internal utility class for making stacktraces aware of the mod-site error location of a stacktrace.
 */
public class ContextAware {

    public static <T> T wrapErrorWithContext(GuiOptionEditor editor, ContextAwareRunnable<T> runnable) {
        try {
            return runnable.run();
        } catch (Exception e) {
            throw new ContextualException(e, (editor != null && editor.getOption() != null) ? editor.getOption().getCodeLocation() : null);
        }
    }

    public static <T> T wrapErrorWithContext(Field field, ContextAwareRunnable<T> runnable) {
        try {
            return runnable.run();
        } catch (Exception e) {
            throw new ContextualException(e, field.toString());
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
