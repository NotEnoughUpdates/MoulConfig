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

package io.github.moulberry.moulconfig;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

public abstract class Social {

    public static Social forLink(String name, Identifier icon, String link) {
        try {
            return new URLSocial(name, new URI(link), icon);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void onClick();

    public abstract List<String> getTooltip();

    public abstract Identifier getIcon();

    private static class URLSocial extends Social {
        private final String name;
        private final URI url;
        private final Identifier icon;

        private URLSocial(String name, URI url, Identifier icon) {
            this.name = name;
            this.url = url;
            this.icon = icon;
        }

        @Override
        public void onClick() {
            try {
                Desktop.getDesktop().browse(url);
            } catch (Exception e) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player == null) return;
                player.sendMessage(
                        Text.literal("Click here to open " + name)
                                .styled(style -> style
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url.toString()))
                                )
                );
            }
        }

        @Override
        public List<String> getTooltip() {
            return Collections.singletonList(name);
        }

        @Override
        public Identifier getIcon() {
            return icon;
        }
    }
}
