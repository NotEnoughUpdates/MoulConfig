package io.github.moulberry.moulconfig;

import net.minecraft.util.ResourceLocation;

public class GuiTextures {
    public static ResourceLocation
        DELETE, RESET, BUTTON, BUTTON_WHITE,
        TOGGLE_OFF, TOGGLE_ONE, TOGGLE_TWO, TOGGLE_THREE, TOGGLE_ON, TOGGLE_BAR,
        SLIDER_OFF_CAP, SLIDER_OFF_NOTCH, SLIDER_OFF_SEGMENT,
        SLIDER_ON_CAP, SLIDER_ON_NOTCH, SLIDER_ON_SEGMENT,
        SLIDER_BUTTON,
        COLOUR_SELECTOR_DOT, COLOUR_SELECTOR_BAR, COLOUR_SELECTOR_BAR_ALPHA, COLOUR_SELECTOR_CHROMA,
        COLOUR_PICKER_INTERNAL, COLOUR_PICKER_INTERNAL_VALUE, COLOUR_PICKER_INTERNAL_OPACITY,
        SEARCH;

    private static ResourceLocation root;

    static {
        setTextureRoot(new ResourceLocation("moulconfig", ""));
    }

    private static ResourceLocation r(String name) {
        return new ResourceLocation(root.getResourceDomain(), ("".equals(root.getResourcePath()) ? name : root.getResourcePath() + "/" + name));
    }

    public static void setTextureRoot(ResourceLocation root) {
        GuiTextures.root = root;
        DELETE = r("delete.png");
        RESET = r("reset.png");
        BUTTON = r("button.png");
        BUTTON_WHITE = r("button_white.png");
        TOGGLE_OFF = r("toggle_off.png");
        TOGGLE_ONE = r("toggle_1.png");
        TOGGLE_TWO = r("toggle_2.png");
        TOGGLE_THREE = r("toggle_3.png");
        TOGGLE_ON = r("toggle_on.png");
        TOGGLE_BAR = r("bar.png");
        SLIDER_OFF_CAP = r("slider/slider_off_cap.png");
        SLIDER_OFF_NOTCH = r("slider/slider_off_notch.png");
        SLIDER_OFF_SEGMENT = r("slider/slider_off_segment.png");
        SLIDER_ON_CAP = r("slider/slider_on_cap.png");
        SLIDER_ON_NOTCH = r("slider/slider_on_notch.png");
        SLIDER_ON_SEGMENT = r("slider/slider_on_segment.png");
        SLIDER_BUTTON = r("slider/slider_button.png");
        COLOUR_SELECTOR_DOT = r("colour_selector_dot.png");
        COLOUR_SELECTOR_BAR = r("colour_selector_bar.png");
        COLOUR_SELECTOR_BAR_ALPHA = r("colour_selector_bar_alpha.png");
        COLOUR_SELECTOR_CHROMA = r("colour_selector_chroma.png");
        COLOUR_PICKER_INTERNAL = r("internal/colourpicker");
        COLOUR_PICKER_INTERNAL_VALUE = r("internal/colourpicker_value");
        COLOUR_PICKER_INTERNAL_OPACITY = r("internal/colourpicker_opacity");
        SEARCH = r("search.png");
    }


}
