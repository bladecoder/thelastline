package com.bladecoder.tll.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.bladecoder.tll.ui.BladeSkin;
import com.bladecoder.tll.util.DPIUtils;
import com.bladecoder.tll.util.RectangleRenderer;

public class BlocksRenderer {

    private final BitmapFont smallFont;
    private final BitmapFont bigFont;

    private final GlyphLayout textLayoutSmall = new GlyphLayout();
    private final GlyphLayout textLayoutBig = new GlyphLayout();
    private TextureAtlas.AtlasRegion tile;
    private TextureAtlas.AtlasRegion background;
    private final Vector2 org = new Vector2();

    private final GameState gameState;

    private int playfieldHeight;
    private int playfieldWidth;

    private float scale = 1.0f;

    private int tileSize;

    private float borderWidth;

    private final Theme theme;

    private float screenWidth;
    private float screenHeight;

    private float scoreSquareSize;


    public BlocksRenderer(TextureAtlas atlas, BladeSkin skin, GameState gameState, Theme theme) {
        this.bigFont = skin.getFont("big-font");
        this.smallFont = skin.getFont("small-font");
        this.gameState = gameState;
        this.theme = theme;

        if(atlas != null) {
            this.background = atlas.findRegion("background");
            this.tile = atlas.findRegion("tile");
        }

        borderWidth = theme.borderWidth;
    }

    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        playfieldHeight = height - (int)DPIUtils.getMarginSize() * 2;

        tileSize = playfieldHeight / gameState.playfield.getHeight();
        playfieldHeight = tileSize * gameState.playfield.getHeight(); // for perfect fit


        float aspect = (float)gameState.playfield.getWidth() / (float)gameState.playfield.getHeight();

        playfieldWidth = (int)(playfieldHeight * aspect);

        scale = height / 1080f;

        this.org.set((float) ((width - playfieldWidth) / 2.0), DPIUtils.getMarginSize());

        borderWidth = (int)(theme.borderWidth * scale);

        if(borderWidth < 1)
            borderWidth = 1;

        scoreSquareSize = 4 * tileSize + DPIUtils.getSpacing() * 2;
    }

    public void render(SpriteBatch batch) {

        // draw background
        if(background != null) {
            batch.draw(background, 0, 0, screenWidth, screenHeight);
        }

        // draw playfield border
        RectangleRenderer.draw(batch, org.x - borderWidth, org.y - borderWidth, playfieldWidth + borderWidth * 2, playfieldHeight + borderWidth * 2, theme.playfieldColor, borderWidth, theme.playfieldBorderColor);

        // render grid
        if (theme.gridColor != null)
            renderGrid(batch);

        // render gameState.tetramino
        renderTetramino(batch);

        // render field
        for (int y = 0; y < gameState.playfield.getHeight(); y++) {
            for (int x = 0; x < gameState.playfield.getWidth(); x++) {
                if (gameState.playfield.get(x, y) != 0) {
                    renderTile(batch, org.x + x * tileSize, org.y + y * tileSize);
                }
            }
        }

        // line clear effect
        if(gameState.state == GameState.State.LINE_CLEAR) {
            // draw rectangle in the cleared gameState.lines
            for(int y=0; y<gameState.playfield.getHeight(); y++) {
                if(gameState.playfield.isRowFull(y)) {
                    RectangleRenderer.draw(batch, org.x, org.y + y * tileSize, tileSize * gameState.playfield.getWidth(), tileSize, theme.clearLineColor);
                }
            }
        }

        float posx = org.x - scoreSquareSize;
        float posy = org.y + gameState.playfield.getHeight() * tileSize - scoreSquareSize + borderWidth;

        renderNextTetramino(batch, posx, posy, scoreSquareSize);

        // draw num. lines
        posy -= scoreSquareSize - borderWidth;
        renderSquareText(batch, posx, posy,"LINES", "" + gameState.lines, theme.scoresTextColor);

        // draw level
        posy -= scoreSquareSize - borderWidth;
        renderSquareText(batch, posx, posy,"LEVEL", "" + gameState.level, theme.scoresTextColor);

        // draw score
        posx = org.x + playfieldWidth + (theme.scoresBorderColor != null ? 0 : borderWidth);
        posy = org.y + gameState.playfield.getHeight() * tileSize - scoreSquareSize + borderWidth;

        String titleStr = "SCORE";
        String valueStr = "" + gameState.points;

        // if score is too big, show it with small font
        if(gameState.points > 9999) {
            titleStr = "SCORE\n" + gameState.points;
            valueStr = null;
        }

        renderSquareText(batch, posx, posy,titleStr, valueStr, theme.scoresTextColor);

        // draw high score
        posy -= scoreSquareSize - borderWidth;

        titleStr = "BEST";
        valueStr = "" + gameState.highScore;

        // if score is too big, show it with small font
        if(gameState.highScore > 9999) {
            titleStr = "HIGH SCORE\n" + gameState.highScore;
            valueStr = null;
        }

        Color highScoreColor = theme.scoresTextColor;
        if (gameState.highScore <= gameState.points) highScoreColor = Color.RED;
        renderSquareText(batch, posx, posy,titleStr, valueStr, highScoreColor);

        // draw game mode and time
        posy -= scoreSquareSize - borderWidth;
        renderSquareText(batch, posx, posy,gameState.gameMode.toString() + "\n" + getTimeString(), null, theme.scoresTextColor);

        // draw game over or win text
        if (gameState.state == GameState.State.GAME_OVER || gameState.state == GameState.State.WIN) {
            String s = "GAME OVER";

            if (gameState.state == GameState.State.WIN) s = "YOU WIN!";

            textLayoutBig.setText(bigFont, s, theme.scoresTextColor, 0.0f, Align.center, false);

            Color c = theme.scoresBgColor;

            if(c == null)
            	c = theme.bgColor;

            RectangleRenderer.draw(
                    batch,
                    org.x + (playfieldWidth  - textLayoutBig.width) / 2 - DPIUtils.getMarginSize() * 2,
                    org.y + (playfieldHeight - textLayoutBig.height) / 2 - DPIUtils.getMarginSize() * 2,
                    textLayoutBig.width + DPIUtils.getMarginSize() * 4,
                    textLayoutBig.height + DPIUtils.getMarginSize() * 4,
                    c, borderWidth, theme.scoresBorderColor);
            bigFont.draw(batch, textLayoutBig,  org.x  + (float)playfieldWidth / 2, org.y + (float) playfieldHeight / 2 + textLayoutBig.height / 2);
        }
    }

    private void renderTetramino(SpriteBatch batch) {
        float posX = org.x + gameState.tetramino.getPos().x * tileSize;
        float posY = org.y + gameState.tetramino.getPos().y * tileSize;

        for (int y = 0; y < gameState.tetramino.getCurrent().length; y++) {
            for (int x = 0; x < gameState.tetramino.getCurrent()[y].length; x++) {
                if (!gameState.tetramino.isFree(x, y) && gameState.tetramino.getPos().y + y < gameState.playfield.getHeight()) {
                    renderTile(batch, posX + x * tileSize, posY + y * tileSize);
                }
            }
        }
    }

    private void renderNextTetramino(SpriteBatch batch, float posx, float posy, float size) {
        int[][] next = gameState.tetramino.getNext();

        RectangleRenderer.draw(batch, posx, posy, size, size, theme.scoresBgColor, borderWidth, theme.scoresBorderColor);

        posx = posx + (size - tileSize * next.length) / 2f; // - borderWidth;
        posy = posy + (size - tileSize * (next.length == 4? 1:2)) / 2f;

        for (int y = 0; y < next.length; y++) {
            boolean empty = true;
            for (int x = 0; x < next[y].length; x++) {
                if (next[y][x] != 0) {
                    renderTile(batch, posx + x * tileSize, posy + y * tileSize);
                    empty = false;
                }
            }

            if(empty) {
                posy -= tileSize;
            }
        }
    }

    private void renderSquareText(SpriteBatch batch, float posx, float posy, String title, String value, Color textColor) {
        RectangleRenderer.draw(batch, posx, posy, scoreSquareSize, scoreSquareSize, theme.scoresBgColor, borderWidth, theme.scoresBorderColor);

        if(value == null) {
            textLayoutSmall.setText(smallFont, title, textColor, 0f, Align.center, false);

            if(textLayoutSmall.width > scoreSquareSize) {
            	scoreSquareSize = textLayoutSmall.width + DPIUtils.getMarginSize() * 2;
            }

            smallFont.draw(batch, textLayoutSmall, posx + scoreSquareSize / 2, posy + (scoreSquareSize + textLayoutSmall.height) / 2);
            return;
        }

        textLayoutSmall.setText(smallFont, title, textColor, 0f, Align.center, false);

        if(textLayoutSmall.width > scoreSquareSize) {
            scoreSquareSize = textLayoutSmall.width + DPIUtils.getMarginSize() * 2;
        }

        textLayoutBig.setText(bigFont, value, textColor, 0f, Align.center, false);

        if(textLayoutBig.width > scoreSquareSize) {
            scoreSquareSize = textLayoutBig.width + DPIUtils.getMarginSize() * 2;
        }

        smallFont.draw(batch, textLayoutSmall, posx + scoreSquareSize / 2, posy + (scoreSquareSize + textLayoutSmall.height  + textLayoutBig.height  + DPIUtils.getMarginSize()) / 2);
        bigFont.draw(batch, textLayoutBig, posx + scoreSquareSize / 2, posy + (scoreSquareSize + textLayoutBig.height - textLayoutSmall.height - DPIUtils.getMarginSize()) / 2);
    }

    private void renderTile(SpriteBatch batch, float x, float y) {
        if(tile != null)
            batch.draw(tile, x, y, tileSize, tileSize);
        else
            RectangleRenderer.draw(batch, x, y, tileSize, tileSize, theme.tileColor, theme.tileBorderWidth * scale, theme.tileBorderColor);
    }

    private void renderGrid(SpriteBatch batch) {
        // Draw horizontal lines
        for (int y = 1; y < gameState.playfield.getHeight(); y++) {
            RectangleRenderer.draw(batch, org.x, org.y + y * tileSize, playfieldWidth, theme.gridWidth * scale, theme.gridColor);
        }

        // Draw vertical lines
        for (int x = 1; x < gameState.playfield.getWidth(); x++) {
            RectangleRenderer.draw(batch, org.x + x * tileSize, org.y, theme.gridWidth * scale, playfieldHeight, theme.gridColor);
        }
    }

    private String getTimeString() {
        int hours = (int) (gameState.gameTime / 3600);
        int minutes = (int) ((gameState.gameTime % 3600) / 60);
        int seconds = (int) (gameState.gameTime % 60);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
