package io.github.moulberry.moulconfig.internal;

import io.github.moulberry.moulconfig.Config;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Warnings {
    public static boolean isDevEnv = Launch.blackboard.get("fml.deobfuscatedEnvironment") == Boolean.TRUE;
    public static boolean shouldWarn = Boolean.getBoolean("moulconfig.warn") || isDevEnv;
    public static Logger logger = LogManager.getLogger("MoulConfig");
    public static String basePackage = Config.class.getPackage().getName() + ".";
    public static String testPackage = basePackage + "test.";

    public static void warn(String warningText) {
        if (shouldWarn) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            int i = 0;
            StackTraceElement modCall = null;
            for (StackTraceElement stackTraceElement : stackTrace) {
                if (i++ < 2 || (stackTraceElement.getClassName().startsWith(basePackage) &&
                    !stackTraceElement.getClassName().startsWith(testPackage)))
                    continue;
                modCall = stackTraceElement;
                break;
            }
            logger.warn("Warning: " + warningText + " at " + stackTrace[2] + " called by " + modCall);
        }
    }
}
