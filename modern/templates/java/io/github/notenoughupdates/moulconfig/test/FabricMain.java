package io.github.notenoughupdates.moulconfig.test;

import io.github.notenoughupdates.moulconfig.common.IItemStack;
import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.common.MyResourceLocation;
import io.github.notenoughupdates.moulconfig.gui.CloseEventListener;
import io.github.notenoughupdates.moulconfig.managed.ManagedConfig;
import io.github.notenoughupdates.moulconfig.observer.ObservableList;
import io.github.notenoughupdates.moulconfig.platform.MoulConfigPlatform;
import io.github.notenoughupdates.moulconfig.xml.Bind;
import io.github.notenoughupdates.moulconfig.xml.XMLUniverse;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static io.github.notenoughupdates.moulconfig.test.CommandUtils.literal;

public class FabricMain implements ModInitializer {
    @Override
    public void onInitialize() {
        if (!"true".equals(System.getProperty("moulconfig.testmod"))) return;
        ManagedConfig<TestConfig> config = ManagedConfig.create(new File("config/moulconfig/test.json"), TestConfig.class);
        ClientCommandRegistrationCallback.EVENT.register((a, b) -> {
            a.register(literal("moulconfig").executes(ctx -> {
                Minecraft.getInstance().schedule(() -> {
                    var editor = config.getEditor();
                    editor.setWide(config.getInstance().getTestCategoryA().isWide());
                    IMinecraft.INSTANCE.openWrappedScreen(editor);
                });
                return 0;
            }));
            a.register(literal("moulconfigxml").executes(ctx -> {
                Minecraft.getInstance().schedule(() -> {
                    XMLUniverse xmlUniverse = XMLUniverse.getDefaultUniverse();
                    var scene = xmlUniverse.load(
                        new ObjectBound(),
                        IMinecraft.INSTANCE.loadResourceLocation(MyResourceLocation.parse("moulconfig:test.xml"))
                    );
                    IMinecraft.getInstance()
                            .openWrappedScreen(scene);
                });
                return 0;
            }));
        });
    }

    public static class Element {
        @Bind
        public String text;

        @Bind
        public boolean enabled = false;

        public Element(String text) {
            this.text = text;
        }

        @Bind
        public void randomize() {
            text = "§" + "abcdef0123456789".charAt(new Random().nextInt(16)) + text.replaceAll("§.", "");
        }
    }

    public static class ObjectBound {
        @Bind
        public Runnable requestClose = null;

        @Bind
        public void afterClose() {
            System.out.println("After close");
        }

        @Bind
        public CloseEventListener.CloseAction beforeClose() {
            System.out.println("Before close");
            return CloseEventListener.CloseAction.NO_OBJECTIONS_TO_CLOSE;
        }

        @Bind
        public IItemStack itemStack = MoulConfigPlatform.wrap(new ItemStack(Blocks.SAND));

        @Bind
        public boolean value = false;

        @Bind
        public String textField = "";

        @Bind
        public float slider = 0f;

        @Bind
        public void addElement() {
            data.add(new Element(textField));
            textField = "";
        }

        @Bind
        public ObservableList<Element> data =
            new ObservableList<>(new ArrayList<>(Arrays.asList(
                new Element("Test 1"), new Element("Test 2"), new Element("Test 3")
            )));
    }
}
