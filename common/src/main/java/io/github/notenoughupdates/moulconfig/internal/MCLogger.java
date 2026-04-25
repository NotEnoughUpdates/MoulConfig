package io.github.notenoughupdates.moulconfig.internal;

public interface MCLogger {
    void warn(String text);

    void info(String text);

    void error(String text, Throwable throwable);
}
