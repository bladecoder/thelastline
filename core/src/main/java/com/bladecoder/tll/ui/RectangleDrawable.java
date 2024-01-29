package com.bladecoder.tll.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.bladecoder.tll.util.RectangleRenderer;

public class RectangleDrawable extends BaseDrawable {

    private final Color color;
    private final int borderWidth;
    private final Color borderColor;

    public RectangleDrawable(Color color, int borderWidth, Color borderColor) {
        this.color = color;
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        RectangleRenderer.draw(batch, x, y, width, height, color, borderWidth, borderColor);
    }
}
