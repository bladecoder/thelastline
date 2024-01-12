package com.bladecoder.tll.blocks;

import com.badlogic.gdx.graphics.Color;

public class Theme {
    public static final Theme DEFAULT = new Theme();
    public static final Theme SUNSET = createSunsetTheme();

    public static final Theme[] THEMES = {DEFAULT, SUNSET};


    public Color bgColor = Color.BLACK;
    public Color gridColor = new Color(0, 0.2f, 0, 1f);
    public float gridWidth = 2;
    public Color playfieldColor;
    public Color playfieldBorderColor = Color.GREEN;
    public Color scoresTextColor = Color.GREEN;
    public Color scoresBgColor = Color.BLACK;
    public Color scoresBorderColor = Color.GREEN;

    public Color tileColor = Color.GREEN;
    public Color tileBorderColor = Color.BLACK;
    public float tileBorderWidth = 4;

    public float borderWidth = 2;

    public Color clearLineColor = Color.GREEN;
    public String atlas; // = "theme1.atlas";

    private static Theme createSunsetTheme() {
        Color bg = new Color(0x243145ff);
        Color lightBg = new Color(0x2b516fff);
        Color accent = new Color(0xff6b78ff);

        Theme theme = new Theme();
        theme.bgColor = bg;
        theme.gridColor = new Color(0x67506dff);
        theme.gridWidth = 2;
        theme.playfieldColor = lightBg;
        theme.playfieldBorderColor = accent;
        theme.scoresTextColor = Color.WHITE;
        theme.scoresBgColor = lightBg;
        theme.scoresBorderColor = accent;
        theme.tileColor = accent;
        theme.tileBorderColor = new Color(0x142532ff);
        theme.tileBorderWidth = 4;

        theme.borderWidth = 6;

        theme.clearLineColor = accent;
        return theme;
    }
}
