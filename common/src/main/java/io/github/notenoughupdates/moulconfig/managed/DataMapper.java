package io.github.notenoughupdates.moulconfig.managed;

public interface DataMapper<T> {
    String serialize(T value);

    T createDefault();

    T deserialize(String string);
}
