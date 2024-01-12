package com.bladecoder.tll.blocks;

import com.badlogic.gdx.graphics.Color;

public class Theme {
    public static final Theme DEFAULT = new Theme();

    public Color bgColor = Color.BLACK;
    public Color gridColor = new Color(0, 0.2f, 0, 1f);
    public Color playfieldColor;
    public Color playfieldBorderColor = Color.GREEN;
    public Color scoresTextColor = Color.GREEN;
    public Color scoresBgColor;
    public Color scoresBorderColor = Color.GREEN;

    public Color tileColor = Color.GREEN;
    public Color tileBorderColor = Color.BLACK;
    public float tileBorderWidth = 4;

    public float borderWidth = 2;
    public String atlas; // = "theme1.atlas";
}
