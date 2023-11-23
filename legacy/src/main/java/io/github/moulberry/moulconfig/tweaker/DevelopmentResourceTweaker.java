package io.github.moulberry.moulconfig.tweaker;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.List;


/**
 * Tweaker to inject resource packs into Minecraft on 1.8.9.
 * Use instead of a devenvMod by specifying {@code --tweakClass io.github.moulberry.moulconfig.tweaker.DevelopmentResourceTweaker} as a run argument.
 */
public class DevelopmentResourceTweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer(MetaTransformer.class.getName());
    }

    @Override
    public String getLaunchTarget() {
        return null;
    }


    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
