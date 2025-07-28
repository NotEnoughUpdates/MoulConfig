package io.github.notenoughupdates.moulconfig.common.text;

import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.stream.Stream;

@ApiStatus.NonExtendable
public interface StructuredText {
    interface Mutable extends StructuredText {
        @NotNull Mutable append(@NotNull StructuredText text);

        default @NotNull Mutable append(@NotNull String text) {
            return append(StructuredText.of(text));
        }

        default @NotNull Mutable withStyle(@NotNull StructuredStyle style) {
            setStyle(style);
            return this;
        }

        default @NotNull Mutable modifyStyle(@NotNull Function<@NotNull StructuredStyle, @NotNull StructuredStyle> operator) {
            // ew, non linear types
            return withStyle(operator.apply(getStyle()));
        }

        default @NotNull Mutable withColour(int rgb) {
            return modifyStyle(it -> it.withColour(rgb));
        }

        default @NotNull Mutable withColour(@NotNull DefaultFormattingColour colour) {
            return withColour(colour.getRgb());
        }

        default @NotNull Mutable bold() {
            return modifyStyle(it -> it.withBold(true));
        }

        default @NotNull Mutable underlined() {
            return modifyStyle(it -> it.withUnderline(true));
        }

        default @NotNull Mutable black() {
            return withColour(DefaultFormattingColour.BLACK);
        }

        default @NotNull Mutable darkBlue() {
            return withColour(DefaultFormattingColour.DARK_BLUE);
        }

        default @NotNull Mutable darkGreen() {
            return withColour(DefaultFormattingColour.DARK_GREEN);
        }

        default @NotNull Mutable darkAqua() {
            return withColour(DefaultFormattingColour.DARK_AQUA);
        }

        default @NotNull Mutable darkRed() {
            return withColour(DefaultFormattingColour.DARK_RED);
        }

        default @NotNull Mutable darkPurple() {
            return withColour(DefaultFormattingColour.DARK_PURPLE);
        }

        default @NotNull Mutable gold() {
            return withColour(DefaultFormattingColour.GOLD);
        }

        default @NotNull Mutable grey() {
            return withColour(DefaultFormattingColour.GREY);
        }

        default @NotNull Mutable darkGrey() {
            return withColour(DefaultFormattingColour.DARK_GREY);
        }

        default @NotNull Mutable blue() {
            return withColour(DefaultFormattingColour.BLUE);
        }

        default @NotNull Mutable green() {
            return withColour(DefaultFormattingColour.GREEN);
        }

        default @NotNull Mutable aqua() {
            return withColour(DefaultFormattingColour.AQUA);
        }

        default @NotNull Mutable red() {
            return withColour(DefaultFormattingColour.RED);
        }

        default @NotNull Mutable lightPurple() {
            return withColour(DefaultFormattingColour.LIGHT_PURPLE);
        }

        default @NotNull Mutable yellow() {
            return withColour(DefaultFormattingColour.YELLOW);
        }

        default @NotNull Mutable white() {
            return withColour(DefaultFormattingColour.WHITE);
        }

    }

    static @NotNull StructuredText.Mutable of(@NotNull String text) {
        return IMinecraft.INSTANCE.createLiteral(text);
    }

    static @NotNull StructuredText.Mutable empty() {
        return of("");
    }

    static @NotNull StructuredText.Mutable translatable(@NotNull String translationKey, @NotNull StructuredText @NotNull ... args) {
        return IMinecraft.INSTANCE.createTranslatable(translationKey, args);
    }

    Mutable copyShallow();

    /**
     * @return a string containing the text of this and any children. this is a lossy conversion.
     */
    @NotNull String getText();

    @NotNull
    Stream<@NotNull StructuredText> getChildren();

    @NotNull StructuredStyle getStyle();

    void setStyle(StructuredStyle style);

}
