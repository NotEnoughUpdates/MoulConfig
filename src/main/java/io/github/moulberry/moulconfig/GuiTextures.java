/*
 * Copyright (C) 2023 NotEnoughUpdates contributors
 *
 * This file is part of MoulConfig.
 *
 * MoulConfig is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * MoulConfig is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MoulConfig. If not, see <https://www.gnu.org/licenses/>.
 *
 */

/**/
package io.github.moulberry.moulconfig;

import net.minecraft.util.Identifier;

public class GuiTextures {
    public static Identifier
            DELETE, RESET, BUTTON, BUTTON_WHITE,
            TOGGLE_OFF, TOGGLE_ONE, TOGGLE_TWO, TOGGLE_THREE, TOGGLE_ON, TOGGLE_BAR,
            SLIDER_OFF_CAP, SLIDER_OFF_NOTCH, SLIDER_OFF_SEGMENT,
            SLIDER_ON_CAP, SLIDER_ON_NOTCH, SLIDER_ON_SEGMENT,
            SLIDER_BUTTON,
            COLOUR_SELECTOR_DOT, COLOUR_SELECTOR_BAR, COLOUR_SELECTOR_BAR_ALPHA, COLOUR_SELECTOR_CHROMA,
            COLOUR_PICKER, COLOUR_PICKER_VALUE, COLOUR_PICKER_OPACITY,
            SEARCH;

    private static Identifier root;

    static {
        setTextureRoot(new Identifier("moulconfig", ""));
    }

    private static Identifier r(String name) {
        return new Identifier(root.getNamespace(), ("".equals(root.getPath()) ? name : root.getPath() + "/" + name));
    }

    public static void setTextureRoot(Identifier root) {
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
        COLOUR_PICKER = r("colorcircle/colorpicker.png");
        COLOUR_PICKER_VALUE = r("colorcircle/value.png");
        COLOUR_PICKER_OPACITY = r("colorcircle/opacity.png");
        SEARCH = r("search.png");
    }


}
