package io.github.notenoughupdates.moulconfig.xml;

import lombok.NonNull;
import lombok.Value;

import java.lang.reflect.Type;

@Value(staticConstructor = "of")
class TypePath {
    @NonNull
    Type source;
    @NonNull
    Type destination;
}
