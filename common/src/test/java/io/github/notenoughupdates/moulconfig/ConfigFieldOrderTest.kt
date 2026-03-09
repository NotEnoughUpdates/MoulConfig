package io.github.notenoughupdates.moulconfig

import io.github.notenoughupdates.moulconfig.annotations.ConfigOrder
import io.github.notenoughupdates.moulconfig.annotations.ConfigOverride
import io.github.notenoughupdates.moulconfig.internal.Warnings
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.reflect.Field

/**
 * Tests for field ordering and override deduplication in [ConfigProcessorDriver.getSortedFields].
 *
 * Covers:
 * - Declaration order is preserved without annotations
 * - [ConfigOrder] sorts fields within and across classes
 * - Shadowed fields without [ConfigOverride] emit a warning
 * - [ConfigOverride] suppresses that warning and slots into the parent's position
 * - [ConfigOverride] inherits the parent's [ConfigOrder] value by default
 * - [ConfigOverride.overrideOrder] takes precedence over inherited order when set
 * - Correct behavior through multiple levels of inheritance
 */
class ConfigFieldOrderTest {

    open class SingleClass { val a = Unit; val b = Unit; val c = Unit }
    open class OrderedSingleClass { @ConfigOrder(3) val c = Unit; @ConfigOrder(1) val a = Unit; @ConfigOrder(2) val b = Unit }

    open class Parent { val x = Unit; open val y = Unit; val z = Unit }
    open class ChildAppendsField : Parent() { val w = Unit }
    open class ChildShadowsWithoutAnnotation : Parent() { override val y = Unit }
    open class ChildShadowsWithAnnotation : Parent() { @ConfigOverride override val y = Unit }

    open class ParentWithOrderedField { val a = Unit; @ConfigOrder(5) open val b = Unit; val c = Unit }
    open class ChildInheritsParentOrder : ParentWithOrderedField() { @ConfigOverride override val b = Unit }
    open class ChildExplicitOverrideOrder : ParentWithOrderedField() { @ConfigOverride(overrideOrder = 99) override val b = Unit }

    open class GrandParent { val p = Unit; open val q = Unit }
    open class MiddleParent : GrandParent() { val r = Unit }
    open class GrandChild : MiddleParent() { @ConfigOverride override val q = Unit }

    private val getSortedFields = ConfigProcessorDriver::class.java
        .getDeclaredMethod("getSortedFields", Class::class.java)
        .also { it.isAccessible = true }

    private var previousShouldWarn = false
    private var previousShouldCrash = false

    @BeforeEach
    fun captureWarningsState() {
        previousShouldWarn = Warnings.shouldWarn
        previousShouldCrash = Warnings.shouldCrash
    }

    @AfterEach
    fun restoreWarningsState() {
        Warnings.shouldWarn = previousShouldWarn
        Warnings.shouldCrash = previousShouldCrash
    }

    @Suppress("UNCHECKED_CAST")
    private fun sortedFields(type: Class<*>) = getSortedFields.invoke(null, type) as List<Field>
    private fun fieldNames(type: Class<*>) = sortedFields(type).map { it.name }

    private fun assertWarningEmitted(type: Class<*>) {
        Warnings.shouldWarn = true
        Warnings.shouldCrash = true
        assertThrows(RuntimeException::class.java) { sortedFields(type) }
    }

    private fun assertNoWarningEmitted(type: Class<*>) {
        Warnings.shouldWarn = true
        Warnings.shouldCrash = true
        assertDoesNotThrow { sortedFields(type) }
    }

    /** Declaration order of fields within a single class should be preserved when no ordering annotations are present. */
    @Test fun `declaration order is preserved without annotations`() =
        assertEquals(listOf("a", "b", "c"), fieldNames(SingleClass::class.java))

    /** [ConfigOrder] should sort fields by ascending value, regardless of declaration order. */
    @Test fun `ConfigOrder sorts fields within a class`() =
        assertEquals(listOf("a", "b", "c"), fieldNames(OrderedSingleClass::class.java))

    /** Fields declared in a parent class should appear before fields declared in the child. */
    @Test fun `parent fields appear before child fields`() =
        assertEquals(listOf("x", "y", "z", "w"), fieldNames(ChildAppendsField::class.java))

    /** Shadowing a parent field without [ConfigOverride] should emit a warning. */
    @Test fun `shadowing without ConfigOverride emits a warning`() =
        assertWarningEmitted(ChildShadowsWithoutAnnotation::class.java)

    /** [ConfigOverride] should suppress the shadow warning. */
    @Test fun `ConfigOverride suppresses shadow warning`() =
        assertNoWarningEmitted(ChildShadowsWithAnnotation::class.java)

    /** [ConfigOverride] should slot the child field back into the parent field's original position. */
    @Test fun `ConfigOverride slots child field into parent position`() {
        val fields = sortedFields(ChildShadowsWithAnnotation::class.java)
        assertEquals(listOf("x", "y", "z"), fields.map { it.name })
        assertEquals(ChildShadowsWithAnnotation::class.java, fields[1].declaringClass)
    }

    /** [ConfigOverride] without an explicit [ConfigOverride.overrideOrder] should inherit the parent's [ConfigOrder] value. */
    @Test fun `ConfigOverride without overrideOrder inherits parent ConfigOrder value`() {
        val fields = sortedFields(ChildInheritsParentOrder::class.java)
        assertEquals(listOf("a", "b", "c"), fields.map { it.name })
        assertEquals(ChildInheritsParentOrder::class.java, fields.first { it.name == "b" }.declaringClass)
    }

    /** An explicit [ConfigOverride.overrideOrder] value should take precedence over the inherited parent order. */
    @Test fun `ConfigOverride with explicit overrideOrder uses that value over inherited`() =
        assertEquals("b", fieldNames(ChildExplicitOverrideOrder::class.java).last())

    /** [ConfigOverride] should correctly resolve position through multiple levels of inheritance. */
    @Test fun `override works correctly through multiple inheritance levels`() {
        val fields = sortedFields(GrandChild::class.java)
        assertEquals(listOf("p", "q", "r"), fields.map { it.name })
        assertEquals(GrandChild::class.java, fields.first { it.name == "q" }.declaringClass)
    }
}
