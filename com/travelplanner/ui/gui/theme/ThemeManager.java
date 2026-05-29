package com.travelplanner.ui.gui.theme;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

/**
 * Nimbus-based theme manager — zero external dependencies.
 * Public API:
 *   ThemeManager.installDarkTheme()   — call once at startup
 *   ThemeManager.installLightTheme()  — call once at startup
 *   ThemeManager.setDark(boolean)     — toggle at runtime
 *   ThemeManager.isDark()             — query current mode
 *   ThemeManager.updateAllWindows()   — force-repaint all windows
 */
public final class ThemeManager {

    private static boolean dark = true;

    private ThemeManager() {}

    // ── Public API ────────────────────────────────────────────────────────

    /** Call once before any Swing window is created. Installs dark theme. */
    public static void installDarkTheme() {
        applyNimbus(true);
        dark = true;
    }

    /** Call once before any Swing window is created. Installs light theme. */
    public static void installLightTheme() {
        applyNimbus(false);
        dark = false;
    }

    /**
     * Switch theme at runtime (called from the Settings toggle).
     * Automatically repaints all open windows.
     */
    public static void setDark(boolean wantDark) {
        if (wantDark == dark) return;
        applyNimbus(wantDark);
        dark = wantDark;
        updateAllWindows();
    }

    /** Force-repaint every open Swing window after a theme change. */
    public static void updateAllWindows() {
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
            w.revalidate();
            w.repaint();
        }
    }

    public static boolean isDark() { return dark; }

    // ── Core installer ────────────────────────────────────────────────────

    /**
     * Installs Nimbus and immediately overrides its named colour tokens
     * with either the dark or light palette.
     *
     * Nimbus reads these keys from UIManager.getLookAndFeelDefaults() after
     * the LAF is installed, so we set them before AND after installation to
     * guarantee they are picked up in both code paths (initial install vs
     * runtime toggle).
     */
    private static void applyNimbus(boolean wantDark) {
        try {
            // 1. Install Nimbus
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("[ThemeManager] Nimbus not available: " + e.getMessage());
            return;
        }

        // 2. Push colour tokens into BOTH UIManager maps so Nimbus picks them up
        if (wantDark) {
            applyDarkPalette();
        } else {
            applyLightPalette();
        }

        // 3. Re-install so Nimbus rebuilds its derived colours from our tokens
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException ignored) {}

        // 4. General compact/aesthetic tweaks (work on any L&F)
        applyCommonDefaults(wantDark);
    }

    // ── Dark palette ──────────────────────────────────────────────────────

    private static void applyDarkPalette() {
        // Primary surfaces
        set("control",              0x1C1E2E);   // panel / window background
        set("info",                 0x252738);   // tooltip background
        set("nimbusBase",           0x2A2D44);   // base tint for derived colours
        set("nimbusBlueGrey",       0x3A3E58);   // secondary surfaces, borders
        set("nimbusLightBackground",0x1C1E2E);   // text field / list background

        // Text
        set("text",                 0xDDE1EE);   // default foreground
        set("nimbusSelectedText",   0xFFFFFF);   // selected text foreground
        set("nimbusDisabledText",   0x666880);   // disabled foreground
        set("infoText",             0xDDE1EE);   // tooltip text
        set("menuText",             0xDDE1EE);   // menu item text
        set("textHighlight",        0x3D5299);   // text selection background

        // Accent / interactive
        set("nimbusSelectionBackground", 0x3D5299);  // list/table row selection
        set("nimbusFocus",               0x5282FF);  // focus ring colour
        set("nimbusOrange",              0x5282FF);  // progress bar / slider fill

        // Borders & separators
        set("nimbusBorder",         0x3C4260);

        // Scroll bar
        set("scrollbar",            0x252738);
        set("nimbusScrollBar",      0x3A3E58);

        // Menu / popup
        set("menu",                 0x252738);
        set("menuHighlight",        0x3D5299);
    }

    // ── Light palette ─────────────────────────────────────────────────────

    private static void applyLightPalette() {
        // Primary surfaces
        set("control",              0xF0F2F8);
        set("info",                 0xFFFBE6);
        set("nimbusBase",           0x5C7AB5);
        set("nimbusBlueGrey",       0x8A9CC0);
        set("nimbusLightBackground",0xFFFFFF);

        // Text
        set("text",                 0x1A1C2E);
        set("nimbusSelectedText",   0xFFFFFF);
        set("nimbusDisabledText",   0x9099B0);
        set("infoText",             0x1A1C2E);
        set("menuText",             0x1A1C2E);
        set("textHighlight",        0xC5D3F5);

        // Accent / interactive
        set("nimbusSelectionBackground", 0x3464EB);
        set("nimbusFocus",               0x3464EB);
        set("nimbusOrange",              0x3464EB);

        // Borders & separators
        set("nimbusBorder",         0xC4CAD9);

        // Scroll bar
        set("scrollbar",            0xE4E8F0);
        set("nimbusScrollBar",      0xBEC6D8);

        // Menu / popup
        set("menu",                 0xFFFFFF);
        set("menuHighlight",        0x3464EB);
    }

    // ── Common defaults (applied after palette) ───────────────────────────

    private static void applyCommonDefaults(boolean isDark) {
        Color accent = isDark ? new Color(82, 130, 255) : new Color(52, 100, 235);

        // Table / list row heights
        UIManager.put("Table.rowHeight",  28);
        UIManager.put("List.cellHeight",  26);
        UIManager.put("Tree.rowHeight",   24);

        // Button default background mirrors accent
        UIManager.put("Button.background", new ColorUIResource(accent));

        // Make sure opaque text areas use the right background
        Color bg = isDark ? new Color(0x1C1E2E) : Color.WHITE;
        UIManager.put("TextArea.background",    new ColorUIResource(bg));
        UIManager.put("TextPane.background",    new ColorUIResource(bg));
        UIManager.put("EditorPane.background",  new ColorUIResource(bg));
    }

    // ── Helper ────────────────────────────────────────────────────────────

    /**
     * Write a colour token into both UIManager maps that Nimbus reads:
     *   1. UIManager defaults  — used before any window opens
     *   2. LAF defaults        — used by already-running Nimbus instance
     */
    private static void set(String key, int rgb) {
        ColorUIResource color = new ColorUIResource(new Color(rgb));
        UIManager.put(key, color);
        UIManager.getLookAndFeelDefaults().put(key, color);
    }
}