package com.travelplanner.ui.gui.theme;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

/**
 * Nimbus-based theme manager — zero external dependencies.
 *
 * CRITICAL ORDERING RULE:
 *   Palette tokens MUST be written to UIManager BEFORE setLookAndFeel() is
 *   called. Nimbus reads UIManager at install time to seed its derived colours.
 *   Installing first then setting tokens causes the first paint to use default
 *   Nimbus colours (the "wrong theme on startup" bug).
 *
 *   Correct order:
 *     1. set all palette tokens into UIManager
 *     2. call UIManager.setLookAndFeel(new NimbusLookAndFeel())  ← one call
 *     3. write the same tokens into getLookAndFeelDefaults()      ← for runtime toggles
 *
 * CALL SITE:
 *   ThemeManager.installDarkTheme() must be called in Main.java BEFORE
 *   SwingUtilities.invokeLater so that no Swing component is ever constructed
 *   without the correct palette in place.
 *
 * Public API:
 *   ThemeManager.installDarkTheme()   — call once at startup (before invokeLater)
 *   ThemeManager.installLightTheme()  — call once at startup (before invokeLater)
 *   ThemeManager.setDark(boolean)     — toggle at runtime from Settings
 *   ThemeManager.isDark()             — query current mode
 *   ThemeManager.updateAllWindows()   — force-repaint all windows
 */
public final class ThemeManager {

    private static boolean dark = true;

    private ThemeManager() {}

    // ── Public API ────────────────────────────────────────────────────────

    /**
     * Call ONCE in Main.java before SwingUtilities.invokeLater().
     * Installs Nimbus with the dark palette baked in from the first pixel.
     */
    public static void installDarkTheme() {
        applyNimbus(true);
    }

    /**
     * Call ONCE in Main.java before SwingUtilities.invokeLater().
     * Installs Nimbus with the light palette baked in from the first pixel.
     */
    public static void installLightTheme() {
        applyNimbus(false);
    }

    /**
     * Toggle at runtime (called from the Settings panel toggle).
     * Re-applies the full palette and repaints every open window.
     */
    public static void setDark(boolean wantDark) {
        if (wantDark == dark) return;
        applyNimbus(wantDark);
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

    // ── Core: correct install order ───────────────────────────────────────

    private static void applyNimbus(boolean wantDark) {
        dark = wantDark;

        // STEP 1 — write palette into UIManager BEFORE installing Nimbus.
        //          Nimbus seeds all its derived colours from these at install time.
        if (wantDark) writePalette(DARK);
        else          writePalette(LIGHT);

        // STEP 2 — install Nimbus exactly once. It now reads the palette we just set.
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("[ThemeManager] Nimbus unavailable: " + e.getMessage());
            return;
        }

        // STEP 3 — mirror palette into getLookAndFeelDefaults() so that runtime
        //          toggles (which happen after LAF is installed) also take effect.
        UIDefaults laf = UIManager.getLookAndFeelDefaults();
        for (String[] entry : (wantDark ? DARK : LIGHT)) {
            laf.put(entry[0], new ColorUIResource(new Color((int) Long.parseLong(entry[1], 16))));
        }

        // STEP 4 — non-colour tweaks that work on any L&F
        applyCommonDefaults(wantDark);
    }

    // ── Palette tables  [key, rrggbb hex] ─────────────────────────────────

    private static final String[][] DARK = {
        // Surfaces
        { "control",               "1C1E2E" },  // panel / window background
        { "info",                  "252738" },  // tooltip background
        { "nimbusBase",            "2A2D44" },  // base tint — drives many derived colours
        { "nimbusBlueGrey",        "3A3E58" },  // secondary surfaces, borders
        { "nimbusLightBackground", "1C1E2E" },  // text-field / list background
        // Text
        { "text",                  "DDE1EE" },  // default foreground
        { "nimbusSelectedText",    "FFFFFF" },
        { "nimbusDisabledText",    "666880" },
        { "infoText",              "DDE1EE" },
        { "menuText",              "DDE1EE" },
        { "textHighlight",         "3D5299" },  // text-selection background
        // Interactive / accent
        { "nimbusSelectionBackground", "3D5299" },
        { "nimbusFocus",               "5282FF" },
        { "nimbusOrange",              "5282FF" },
        // Borders
        { "nimbusBorder",          "3C4260" },
        // Scrollbar
        { "scrollbar",             "252738" },
        { "nimbusScrollBar",       "3A3E58" },
        // Menu
        { "menu",                  "252738" },
        { "menuHighlight",         "3D5299" },
    };

    private static final String[][] LIGHT = {
        // Surfaces
        { "control",               "F0F2F8" },
        { "info",                  "FFFBE6" },
        { "nimbusBase",            "5C7AB5" },
        { "nimbusBlueGrey",        "8A9CC0" },
        { "nimbusLightBackground", "FFFFFF" },
        // Text
        { "text",                  "1A1C2E" },
        { "nimbusSelectedText",    "FFFFFF" },
        { "nimbusDisabledText",    "9099B0" },
        { "infoText",              "1A1C2E" },
        { "menuText",              "1A1C2E" },
        { "textHighlight",         "C5D3F5" },
        // Interactive / accent
        { "nimbusSelectionBackground", "3464EB" },
        { "nimbusFocus",               "3464EB" },
        { "nimbusOrange",              "3464EB" },
        // Borders
        { "nimbusBorder",          "C4CAD9" },
        // Scrollbar
        { "scrollbar",             "E4E8F0" },
        { "nimbusScrollBar",       "BEC6D8" },
        // Menu
        { "menu",                  "FFFFFF" },
        { "menuHighlight",         "3464EB" },
    };

    // ── Helpers ───────────────────────────────────────────────────────────

    /** Write a palette table into UIManager (pre-install path). */
    private static void writePalette(String[][] palette) {
        for (String[] entry : palette) {
            UIManager.put(entry[0],
                new ColorUIResource(new Color((int) Long.parseLong(entry[1], 16))));
        }
    }

    /** Non-colour defaults applied after the LAF is installed. */
    private static void applyCommonDefaults(boolean isDark) {
        Color accent = isDark ? new Color(82, 130, 255) : new Color(52, 100, 235);
        Color bg     = isDark ? new Color(0x1C1E2E)    : Color.WHITE;

        UIManager.put("Table.rowHeight",        28);
        UIManager.put("List.cellHeight",        26);
        UIManager.put("Tree.rowHeight",         24);
        UIManager.put("Button.background",      new ColorUIResource(accent));
        UIManager.put("TextArea.background",    new ColorUIResource(bg));
        UIManager.put("TextPane.background",    new ColorUIResource(bg));
        UIManager.put("EditorPane.background",  new ColorUIResource(bg));

        // Mirror into LAF defaults as well
        UIDefaults laf = UIManager.getLookAndFeelDefaults();
        laf.put("Button.background",     new ColorUIResource(accent));
        laf.put("TextArea.background",   new ColorUIResource(bg));
        laf.put("TextPane.background",   new ColorUIResource(bg));
        laf.put("EditorPane.background", new ColorUIResource(bg));
    }
}