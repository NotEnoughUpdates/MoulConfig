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
package io.github.notenoughupdates.moulconfig.internal;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String cleanColour(String before) {
        return before.replaceAll("§.", "");
    }

    private static long startTime = 0L;
    private static final EnumChatFormatting[] rainbow = new EnumChatFormatting[]{
        EnumChatFormatting.RED,
        EnumChatFormatting.GOLD,
        EnumChatFormatting.YELLOW,
        EnumChatFormatting.GREEN,
        EnumChatFormatting.AQUA,
        EnumChatFormatting.LIGHT_PURPLE,
        EnumChatFormatting.DARK_PURPLE
    };


    public static String chromaString(String str, String styles, boolean bold) {
        str = cleanColour(str);

        long currentTimeMillis = System.currentTimeMillis();
        if (startTime == 0) startTime = currentTimeMillis;

        StringBuilder rainbowText = new StringBuilder();
        int len = 0;
        for (char c : str.toCharArray()) {
            int index = ((int) (len / 12f - (currentTimeMillis - startTime) / 500)) % rainbow.length;
            len += Minecraft.getMinecraft().fontRendererObj.getCharWidth(c);
            if (bold) len++;
            if (index < 0) index += rainbow.length;
            rainbowText.append(rainbow[index]);
            if (bold) rainbowText.append(EnumChatFormatting.BOLD);
            rainbowText.append(c);
        }
        return rainbowText.toString();
    }

    private static Pattern CHROMA_REPLACE_PATTERN = Pattern.compile("§(.)([^§]+)");

    public static String chromaStringByColourCode(String str) {
        Matcher matcher = CHROMA_REPLACE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean isBold = false;
        String styles = "";
        while (matcher.find()) {
            String format = matcher.group(1).intern().toLowerCase(Locale.ENGLISH);
            String replacement = matcher.group(2);
            if ("z".equals(format)) {
                replacement = chromaString(replacement, styles, isBold);
            } else if ("l".equals(format)) {
                isBold = true;
            } else if ("0123456789abcdef".contains(format)) {
                styles = "";
                isBold = false;
            } else {
                styles += "§" + format;
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
