package com.travelplanner.ui.gui.theme;

import javax.swing.*;
import java.awt.*;

/**
 * FlatLaf theme manager — zero hard imports, fully reflective.
 *
 * ── How to enable FlatLaf (one-time setup) ───────────────────────────────
 *
 *  OPTION A  Maven — add to pom.xml:
 *    <dependency>
 *      <groupId>com.formdev</groupId>
 *      <artifactId>flatlaf</artifactId>
 *      <version>3.4.1</version>
 *    </dependency>
 *
 *  OPTION B  Manual JAR
 *    1. Download flatlaf-3.4.1.jar from
 *       https://github.com/JFormDesigner/FlatLaf/releases/tag/3.4.1
 *    2. IntelliJ IDEA:
 *         File → Project Structure (Ctrl+Alt+Shift+S)
 *         → Libraries → + → Java → select the JAR → OK → Apply
 *    3. Eclipse:
 *         Right-click project → Build Path → Add External Archives
 *         → select the JAR → Apply and Close
 *    4. VS Code (with Language Support for Java):
 *         Add the JAR path to "java.project.referencedLibraries" in
 *         .vscode/settings.json:
 *         "java.project.referencedLibraries": ["lib/flatlaf-3.4.1.jar"]
 *
 * ── After adding the JAR ─────────────────────────────────────────────────
 *   No code changes needed. The reflective loader will find the class
 *   automatically and full dark/light theming will activate on next run.
 *
 * ── Without the JAR ──────────────────────────────────────────────────────
 *   The app still runs cleanly — it simply uses the system default L&F.
 *   The warning message below will no longer appear once the JAR is added.
 */
public final class ThemeManager {

    private static boolean dark = true;

    private static final String FLAT_DARK_LAF  = "com.formdev.flatlaf.FlatDarkLaf";
    private static final String FLAT_LIGHT_LAF = "com.formdev.flatlaf.FlatLightLaf";

    private ThemeManager() {}

    // ── Public API ────────────────────────────────────────────────────────

    /** Call once before any Swing window is created. Installs dark theme. */
    public static void installDarkTheme() {
        if (tryInstall(FLAT_DARK_LAF)) {
            applyGlobalDefaults(true);
            dark = true;
        }
    }

    /** Call once before any Swing window is created. Installs light theme. */
    public static void installLightTheme() {
        if (tryInstall(FLAT_LIGHT_LAF)) {
            applyGlobalDefaults(false);
            dark = false;
        }
    }

    /**
     * Switch theme at runtime. Call from the Settings toggle.
     * Automatically repaints all open windows.
     */
    public static void setDark(boolean wantDark) {
        if (wantDark == dark) return;
        String lafClass = wantDark ? FLAT_DARK_LAF : FLAT_LIGHT_LAF;
        if (tryInstall(lafClass)) {
            applyGlobalDefaults(wantDark);
            dark = wantDark;
        } else {
            // FlatLaf not available — just flip the flag so the toggle
            // reflects the user's choice if they add the JAR later.
            dark = wantDark;
        }
        updateAllWindows();
    }

    /** Force-repaint every open Swing window. */
    public static void updateAllWindows() {
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
            w.revalidate();
            w.repaint();
        }
    }

    public static boolean isDark() { return dark; }

    // ── Private helpers ───────────────────────────────────────────────────

    /**
     * Load and install a L&F class by name reflectively.
     * Returns true on success; false + one-line console notice if JAR missing.
     */
    private static boolean tryInstall(String className) {
        try {
            Class<?> cls  = Class.forName(className);
            LookAndFeel lf = (LookAndFeel) cls.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel(lf);
            return true;
        } catch (ClassNotFoundException ex) {
            System.out.println(
                "[ThemeManager] FlatLaf JAR not on classpath — using system L&F.\n" +
                "  → To enable beautiful theming, add flatlaf-3.4.1.jar:\n" +
                "      Maven:  add com.formdev:flatlaf:3.4.1 to pom.xml\n" +
                "      Manual: download from github.com/JFormDesigner/FlatLaf/releases\n" +
                "               then add to Project Structure → Libraries (IntelliJ)\n" +
                "               or Build Path → Add External Archives (Eclipse)");
        } catch (Exception ex) {
            System.err.println("[ThemeManager] Failed to install L&F: " + ex.getMessage());
        }
        return false;
    }

    /**
     * UIManager overrides applied after the L&F is set.
     * These give rounded corners, compact rows, and accent colours.
     */
    private static void applyGlobalDefaults(boolean isDark) {
        // Rounded components
        UIManager.put("Button.arc",         999);
        UIManager.put("TextComponent.arc",  8);
        UIManager.put("Component.arc",      8);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width",    8);

        // Compact row heights
        UIManager.put("Table.rowHeight",      32);
        UIManager.put("Tree.rowHeight",       26);
        UIManager.put("List.cellHeight",      26);
        UIManager.put("TabbedPane.tabHeight", 34);

        // Focus ring
        UIManager.put("Component.focusWidth", 1);

        // Accent colour
        Color accent = isDark ? new Color(82, 130, 255) : new Color(52, 110, 235);
        UIManager.put("Component.accentColor",     accent);
        UIManager.put("Button.default.background", accent);
        UIManager.put("ProgressBar.foreground",    accent);
    }
}