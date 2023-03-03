package io.github.moulberry.moulconfig;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public abstract class Social {

    public static Social forLink(String name, ResourceLocation icon, String link) {
        try {
            return new URLSocial(name, new URI(link), icon);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void onClick();

    public abstract List<String> getTooltip();

    public abstract ResourceLocation getIcon();

    private static class URLSocial extends Social {
        private final String name;
        private final URI url;
        private final ResourceLocation icon;

        private URLSocial(String name, URI url, ResourceLocation icon) {
            this.name = name;
            this.url = url;
            this.icon = icon;
        }

        @Override
        public void onClick() {
            try {
                Desktop.getDesktop().browse(url);
            } catch (IOException e) {
                ChatComponentText text = new ChatComponentText("Click here to open " + name);
                text.setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url.toString())));
                Minecraft.getMinecraft().thePlayer.addChatMessage(text);
            }
        }

        @Override
        public List<String> getTooltip() {
            return Arrays.asList(name);
        }

        @Override
        public ResourceLocation getIcon() {
            return icon;
        }
    }
}
