package com.travelplanner.ui.gui.theme;
import javax.swing.UIManager;
public final class ThemeManager {
    private ThemeManager() {
    }
    public static void installDarkTheme() {
        try {
            Class<?> clazz = Class.forName("com.formdev.flatlaf.FlatDarkLaf");
            clazz.getMethod("setup").invoke(null);
        } catch (Exception flatLafMissing) {
            installNimbus();
        }
        applyCommonSettings();
    }
    public static void installLightTheme() {
        try {
            Class<?> clazz = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            clazz.getMethod("setup").invoke(null);
        } catch (Exception flatLafMissing) {
            installNimbus();
        }
        applyCommonSettings();
    }
    private static void installNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Last-resort fallback: default Swing LAF.
        }
    }
    private static void applyCommonSettings() {
        UIManager.put("Button.arc", 18);
        UIManager.put("Component.arc", 18);
        UIManager.put("TextComponent.arc", 14);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("Table.showHorizontalLines", false);
        UIManager.put("Table.showVerticalLines", false);
    }
}