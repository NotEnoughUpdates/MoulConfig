package io.github.notenoughupdates.moulconfig;

import io.github.notenoughupdates.moulconfig.annotations.ConfigOrder;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOverride;
import io.github.notenoughupdates.moulconfig.internal.MCLogger;
import io.github.notenoughupdates.moulconfig.internal.Warnings;
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigFieldOrderTest {
    public static class SingleClass {
        public Object a = new Object();
        public Object b = new Object();
        public Object c = new Object();
    }

    public static class OrderedSingleClass {
        @ConfigOrder(3) public Object c = new Object();
        @ConfigOrder(1) public Object a = new Object();
        @ConfigOrder(2) public Object b = new Object();
    }

    public static class Parent {
        public Object x = new Object();
        public Object y = new Object();
        public Object z = new Object();
    }

    public static class ChildAppendsField extends Parent {
        public Object w = new Object();
    }

    public static class ChildShadowsWithoutAnnotation extends Parent {
        public Object y = new Object();
    }

    public static class ChildShadowsWithAnnotation extends Parent {
        @ConfigOverride public Object y = new Object();
    }

    public static class ParentWithOrderedField {
        public Object a = new Object();
        @ConfigOrder(5) public Object b = new Object();
        public Object c = new Object();
    }

    public static class ChildInheritsParentOrder extends ParentWithOrderedField {
        @ConfigOverride public Object b = new Object();
    }

    public static class ChildExplicitOverrideOrder extends ParentWithOrderedField {
        @ConfigOverride(overrideOrder = 99) public Object b = new Object();
    }

    public static class GrandParent {
        public Object p = new Object();
        public Object q = new Object();
    }

    public static class MiddleParent extends GrandParent {
        public Object r = new Object();
    }

    public static class GrandChild extends MiddleParent {
        @ConfigOverride public Object q = new Object();
    }

    private final Method getSortedFields;
    private final List<String> capturedWarnings = new ArrayList<>();
    private MCLogger previousLogger;

    public ConfigFieldOrderTest() throws NoSuchMethodException {
        getSortedFields = ConfigProcessorDriver.class.getDeclaredMethod("getSortedFields", Class.class);
        getSortedFields.setAccessible(true);
    }

    @BeforeEach
    void installCapturingLogger() {
        previousLogger = Warnings.logger;
        Warnings.shouldWarn = true;
        Warnings.logger = new MCLogger() {
            @Override public void warn(String text) { capturedWarnings.add(text); }
            @Override public void info(String text) {}
            @Override public void error(String text, Throwable throwable) {}
        };
    }

    @AfterEach
    void restoreLogger() {
        Warnings.logger = previousLogger;
        capturedWarnings.clear();
    }

    @SuppressWarnings("unchecked")
    private List<Field> sortedFields(Class<?> type) {
        try {
            return (List<Field>) getSortedFields.invoke(null, type);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> fieldNames(Class<?> type) {
        List<String> names = new ArrayList<>();
        for (Field field : sortedFields(type)) {
            names.add(field.getName());
        }
        return names;
    }

    @Test
    void declarationOrderIsPreservedWithoutAnnotations() {
        Assertions.assertEquals(Arrays.asList("a", "b", "c"), fieldNames(SingleClass.class));
    }

    @Test
    void configOrderSortsFieldsWithinAClass() {
        Assertions.assertEquals(Arrays.asList("a", "b", "c"), fieldNames(OrderedSingleClass.class));
    }

    @Test
    void parentFieldsAppearBeforeChildFields() {
        Assertions.assertEquals(Arrays.asList("x", "y", "z", "w"), fieldNames(ChildAppendsField.class));
    }

    @Test
    void shadowingWithoutConfigOverrideEmitsAWarning() {
        sortedFields(ChildShadowsWithoutAnnotation.class);
        Assertions.assertTrue(capturedWarnings.stream().anyMatch(it -> it.contains("y")));
    }

    @Test
    void configOverrideSuppressesShadowWarning() {
        sortedFields(ChildShadowsWithAnnotation.class);
        Assertions.assertTrue(capturedWarnings.stream().noneMatch(it -> it.contains("y")));
    }

    @Test
    void configOverrideSlotsChildFieldIntoParentPosition() {
        List<Field> fields = sortedFields(ChildShadowsWithAnnotation.class);
        List<String> names = new ArrayList<>();
        for (Field field : fields) names.add(field.getName());
        Assertions.assertEquals(Arrays.asList("x", "y", "z"), names);
        Assertions.assertEquals(ChildShadowsWithAnnotation.class, fields.get(1).getDeclaringClass());
    }

    @Test
    void configOverrideWithoutOverrideOrderInheritsParentConfigOrderValue() {
        List<Field> fields = sortedFields(ChildInheritsParentOrder.class);
        List<String> names = new ArrayList<>();
        Field b = null;
        for (Field field : fields) {
            names.add(field.getName());
            if (field.getName().equals("b")) b = field;
        }
        Assertions.assertEquals(Arrays.asList("a", "c", "b"), names);
        Assertions.assertEquals(ChildInheritsParentOrder.class, b.getDeclaringClass());
    }

    @Test
    void configOverrideWithExplicitOverrideOrderUsesThatValueOverInherited() {
        List<String> names = fieldNames(ChildExplicitOverrideOrder.class);
        Assertions.assertEquals("b", names.get(names.size() - 1));
    }

    @Test
    void overrideWorksCorrectlyThroughMultipleInheritanceLevels() {
        List<Field> fields = sortedFields(GrandChild.class);
        List<String> names = new ArrayList<>();
        Field q = null;
        for (Field field : fields) {
            names.add(field.getName());
            if (field.getName().equals("q")) q = field;
        }
        Assertions.assertEquals(Arrays.asList("p", "q", "r"), names);
        Assertions.assertEquals(GrandChild.class, q.getDeclaringClass());
    }
}
