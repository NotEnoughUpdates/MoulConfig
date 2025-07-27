package io.github.notenoughupdates.moulconfig.common.text;

import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.stream.Stream;

@ApiStatus.NonExtendable
public interface StructuredText {
    static @NotNull StructuredText of(@NotNull String text) {
        return IMinecraft.instance.createLiteral(text);
    }

    static @NotNull StructuredText empty() {
        return of("");
    }

    static @NotNull StructuredText translatable(@NotNull String translationKey, @NotNull StructuredText @NotNull ... args) {
        return IMinecraft.instance.createTranslatable(translationKey, args);
    }

    /**
     * @return a string containing the text of this and any children. this is a lossy conversion.
     */
    @NotNull String getText();

    @NotNull
    Stream<@NotNull StructuredText> getChildren();

    @NotNull StructuredText append(@NotNull StructuredText text);
    default @NotNull StructuredText append(@NotNull String text) {
        return append(StructuredText.of(text));
    }

    @NotNull StructuredStyle getStyle();

    void setStyle(StructuredStyle style);

    default @NotNull StructuredText withStyle(@NotNull StructuredStyle style) {
        setStyle(style);
        return this;
    }

    default @NotNull StructuredText modifyStyle(@NotNull Function<@NotNull StructuredStyle, @NotNull StructuredStyle> operator) {
        // ew, non linear types
        return withStyle(operator.apply(getStyle()));
    }

    default @NotNull StructuredText withColour(int rgb) {
        return modifyStyle(it -> it.withColour(rgb));
    }

    default @NotNull StructuredText withColour(@NotNull DefaultFormattingColour colour) {
        return withColour(colour.getRgb());
    }

    default @NotNull StructuredText bold() {
        return modifyStyle(it -> it.withBold(true));
    }

    default @NotNull StructuredText underlined() {
        return modifyStyle(it -> it.withUnderline(true));
    }

    default @NotNull StructuredText black() {
        return withColour(DefaultFormattingColour.BLACK);
    }

    default @NotNull StructuredText darkBlue() {
        return withColour(DefaultFormattingColour.DARK_BLUE);
    }

    default @NotNull StructuredText darkGreen() {
        return withColour(DefaultFormattingColour.DARK_GREEN);
    }

    default @NotNull StructuredText darkAqua() {
        return withColour(DefaultFormattingColour.DARK_AQUA);
    }

    default @NotNull StructuredText darkRed() {
        return withColour(DefaultFormattingColour.DARK_RED);
    }

    default @NotNull StructuredText darkPurple() {
        return withColour(DefaultFormattingColour.DARK_PURPLE);
    }

    default @NotNull StructuredText gold() {
        return withColour(DefaultFormattingColour.GOLD);
    }

    default @NotNull StructuredText grey() {
        return withColour(DefaultFormattingColour.GREY);
    }

    default @NotNull StructuredText darkGrey() {
        return withColour(DefaultFormattingColour.DARK_GREY);
    }

    default @NotNull StructuredText blue() {
        return withColour(DefaultFormattingColour.BLUE);
    }

    default @NotNull StructuredText green() {
        return withColour(DefaultFormattingColour.GREEN);
    }

    default @NotNull StructuredText aqua() {
        return withColour(DefaultFormattingColour.AQUA);
    }

    default @NotNull StructuredText red() {
        return withColour(DefaultFormattingColour.RED);
    }

    default @NotNull StructuredText lightPurple() {
        return withColour(DefaultFormattingColour.LIGHT_PURPLE);
    }

    default @NotNull StructuredText yellow() {
        return withColour(DefaultFormattingColour.YELLOW);
    }

    default @NotNull StructuredText white() {
        return withColour(DefaultFormattingColour.WHITE);
    }

}
