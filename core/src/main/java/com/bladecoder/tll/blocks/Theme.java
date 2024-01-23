package com.bladecoder.tll.blocks;

import com.badlogic.gdx.graphics.Color;

public class Theme {
    public static final Theme DEFAULT = new Theme();
    public static final Theme SUNSET = createSunsetTheme();

    public static final Theme MONO = createMonoTheme();

    public static final Theme[] THEMES = {DEFAULT, MONO, SUNSET};

    public Color bgColor = Color.BLACK;
    public Color gridColor = new Color(0, 0.2f, 0, 1f);
    public float gridWidth = 2;
    public Color playfieldColor;
    public Color playfieldBorderColor = Color.GREEN;
    public Color scoresTextColor = Color.GREEN;
    public Color scoresBgColor = Color.BLACK;
    public Color scoresBorderColor = Color.GREEN;

    public Color[] tileColors = {Color.GREEN};
    public Color tileBorderColor = Color.BLACK;
    public float tileBorderWidth = 4;

    public float playfieldBorderWidth = 2;

    public float scoresBorderWidth = 2;

    public Color clearLineColor = Color.GREEN;
    public String tileName = null;

    private static Theme createSunsetTheme() {
        Color bg = new Color(0x243145ff);
        Color lightBg = new Color(0x2b516fff);
        Color accent = new Color(0xff6b78ff);

        Theme theme = new Theme();
        theme.bgColor = bg;
        theme.gridColor = new Color(0x67506dff);
        theme.gridWidth = 0;
        theme.playfieldColor = lightBg;
        theme.playfieldBorderColor = accent;
        theme.scoresTextColor = Color.WHITE;
        theme.scoresBgColor = lightBg;
        theme.scoresBorderColor = accent;
        theme.scoresBorderWidth = 6;
        theme.tileColors = new Color[] {
            new Color(0x9dfffaff),   // I
            new Color(0xff90ecff),   // J
            new Color(0xffac4bff),   // L
            new Color(0xfff97aff),   // O
            accent,                           // S
            new Color(0x8283ffff),  // T
            new Color(0x32ff91ff)   // Z
        };
        theme.tileBorderColor = new Color(0x142532ff);
        theme.tileBorderWidth = 3;

        theme.playfieldBorderWidth = 6;

        theme.clearLineColor = accent;
        theme.tileName = "tile2";
        return theme;
    }

    private static Theme createMonoTheme() {
        Color bg = new Color(0xdddbdeff);
        Color accent = Color.BLACK;

        Theme theme = new Theme();
        theme.bgColor = bg;
        theme.gridColor = null;
        theme.gridWidth = 2;
        theme.playfieldColor = null;
        theme.playfieldBorderColor = accent;
        theme.scoresTextColor = accent;
        theme.scoresBgColor = null;
        theme.scoresBorderColor = null;
        theme.scoresBorderWidth = 0;
        theme.tileColors = new Color[] {bg};
        theme.tileBorderColor = new Color(0x142532ff);
        theme.tileBorderWidth = 4;

        theme.playfieldBorderWidth = 30;

        theme.clearLineColor = accent;

        theme.tileName = "tile";
        return theme;
    }
}
