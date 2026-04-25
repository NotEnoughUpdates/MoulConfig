package io.github.notenoughupdates.moulconfig.processor;

import io.github.notenoughupdates.moulconfig.observer.Property;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class ProcessedOptionImplTest {
    public static class NumericOptions {
        public int primitiveInt;
        public Integer boxedInteger;
        public float primitiveFloat;
        public Float boxedFloat;
        public double primitiveDouble;
        public Double boxedDouble;
        public long primitiveLong;
        public Long boxedLong;
        public short primitiveShort;
        public Short boxedShort;
        public byte primitiveByte;
        public Byte boxedByte;
        public Property<Integer> propertyInteger = Property.of(0);
    }

    private final NumericOptions options = new NumericOptions();

    @Test
    void setCoercesPrimitiveInt() {
        Assertions.assertTrue(option("primitiveInt").set(14F));
        Assertions.assertEquals(14, options.primitiveInt);
    }

    @Test
    void setCoercesBoxedInteger() {
        Assertions.assertTrue(option("boxedInteger").set(15F));
        Assertions.assertEquals(Integer.valueOf(15), options.boxedInteger);
    }

    @Test
    void setCoercesPrimitiveFloat() {
        Assertions.assertTrue(option("primitiveFloat").set(1D));
        Assertions.assertEquals(1F, options.primitiveFloat);
    }

    @Test
    void setCoercesBoxedFloat() {
        Assertions.assertTrue(option("boxedFloat").set(2D));
        Assertions.assertEquals(Float.valueOf(2F), options.boxedFloat);
    }

    @Test
    void setCoercesPrimitiveDouble() {
        Assertions.assertTrue(option("primitiveDouble").set(3F));
        Assertions.assertEquals(3D, options.primitiveDouble);
    }

    @Test
    void setCoercesBoxedDouble() {
        Assertions.assertTrue(option("boxedDouble").set(4F));
        Assertions.assertEquals(Double.valueOf(4D), options.boxedDouble);
    }

    @Test
    void setCoercesPrimitiveLong() {
        Assertions.assertTrue(option("primitiveLong").set(5F));
        Assertions.assertEquals(5L, options.primitiveLong);
    }

    @Test
    void setCoercesBoxedLong() {
        Assertions.assertTrue(option("boxedLong").set(6F));
        Assertions.assertEquals(Long.valueOf(6L), options.boxedLong);
    }

    @Test
    void setCoercesPrimitiveShort() {
        Assertions.assertTrue(option("primitiveShort").set(7F));
        Assertions.assertEquals(7, options.primitiveShort);
    }

    @Test
    void setCoercesBoxedShort() {
        Assertions.assertTrue(option("boxedShort").set(8F));
        Assertions.assertEquals(Short.valueOf((short) 8), options.boxedShort);
    }

    @Test
    void setCoercesPrimitiveByte() {
        Assertions.assertTrue(option("primitiveByte").set(9F));
        Assertions.assertEquals(9, options.primitiveByte);
    }

    @Test
    void setCoercesBoxedByte() {
        Assertions.assertTrue(option("boxedByte").set(10F));
        Assertions.assertEquals(Byte.valueOf((byte) 10), options.boxedByte);
    }

    @Test
    void setCoercesPropertyInteger() {
        Assertions.assertTrue(option("propertyInteger").set(11F));
        Assertions.assertEquals(Integer.valueOf(11), options.propertyInteger.get());
    }

    private ProcessedOptionImpl option(String fieldName) {
        try {
            Field field = NumericOptions.class.getField(fieldName);
            return new ProcessedOptionImpl(null, null, fieldName, field, null, options, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
