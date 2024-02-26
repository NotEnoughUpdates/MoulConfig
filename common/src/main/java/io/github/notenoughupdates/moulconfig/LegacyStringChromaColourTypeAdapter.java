package io.github.notenoughupdates.moulconfig;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;

/**
 * Gson type adapter for reading and writing {@link ChromaColour} elements in their string format.
 */
@AllArgsConstructor
@ToString
@Getter
public class LegacyStringChromaColourTypeAdapter extends TypeAdapter<ChromaColour> {

    /**
     * When set to {@code true}, the chroma colour will be serialized as a string and read as a string.
     * When set to {@code false}, the chroma colour will be serialized as an object and read in whichever format it is present (including legacy strings)
     */
    private final boolean serializeAsLegacyString;

    /**
     * Helper instance of gson for the default serialization of gson elements.
     */
    private static final Gson defaultGson = new Gson();

    @Override
    public void write(JsonWriter jsonWriter, ChromaColour chromaColour) throws IOException {
        if (serializeAsLegacyString) {
            //noinspection deprecation
            jsonWriter.value(chromaColour.toLegacyString());
        } else {
            defaultGson.toJson(chromaColour, ChromaColour.class, jsonWriter);
        }
    }

    @Override
    public ChromaColour read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.STRING) {
            //noinspection deprecation
            return ChromaColour.forLegacyString(jsonReader.nextString());
        } else {
            return defaultGson.fromJson(jsonReader, ChromaColour.class);
        }
    }
}
