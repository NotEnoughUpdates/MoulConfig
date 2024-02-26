package io.github.notenoughupdates.moulconfig.internal;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public class ClipboardUtils {
    public static void copyToClipboard(String selection) {
        try {
            Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(selection), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getClipboardContent() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            return "";
        }
    }
}
