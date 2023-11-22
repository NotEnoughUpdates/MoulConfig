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

package io.github.moulberry.moulconfig.internal;

import io.github.moulberry.moulconfig.GuiTextures;
import io.github.moulberry.moulconfig.common.IMinecraft;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Warnings {
    public static boolean isDevEnv = IMinecraft.instance.isDevelopmentEnvironment();
    public static boolean shouldWarn = PropertyUtil.getBooleanWithFallback("moulconfig.warn", isDevEnv);
    public static boolean shouldCrash = shouldWarn && PropertyUtil.getBooleanWithFallback("moulconfig.warn.crash", isDevEnv);
    public static Logger logger = LogManager.getLogManager().getLogger("MoulConfig");
    public static String basePackage = GuiTextures.class.getPackage().getName() + ".";
    public static String testPackage = basePackage + "test.";
    public static HashSet<Object> warnedObjects = new HashSet<>();

    public static void warnOnce(String warningText, Object... warningBucketEntries) {
        if (!shouldWarn) return;
        List<Object> warningBucket = Arrays.asList(warningBucketEntries);
        if (warnedObjects.contains(warningBucket)) return;
        warnedObjects.add(warningBucket);
        warn0(warningText, 3);
    }

    private static void warn0(String warningText, int depth) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int i = 0;
        StackTraceElement modCall = null;
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (i++ < depth || (stackTraceElement.getClassName().startsWith(basePackage) &&
                !stackTraceElement.getClassName().startsWith(testPackage)))
                continue;
            modCall = stackTraceElement;
            break;
        }
        logger.warning("Warning: " + warningText + " at " + stackTrace[depth] + " called by " + modCall);
        if (shouldCrash)
            throw new RuntimeException(warningText);
    }

    public static void warn(String warningText, int depth) {
        if (shouldWarn) warn0(warningText, depth);
    }

    public static void warn(String warningText) {
        warn(warningText, 4);
    }
}
