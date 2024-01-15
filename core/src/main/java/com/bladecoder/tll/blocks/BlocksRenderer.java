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

    public static final int MAX_BIG_SCORE = 9999;
    private BitmapFont smallFont;
    private BitmapFont bigFont;

    private final GlyphLayout textLayoutSmall = new GlyphLayout();
    private final GlyphLayout textLayoutBig = new GlyphLayout();
    private TextureAtlas.AtlasRegion tile;
    private TextureAtlas.AtlasRegion background;
    private final Vector2 org = new Vector2();

    private final GameState gameState;

    private float playfieldHeight;
    private float playfieldWidth;

    private float scale = 1.0f;

    private float tileSize;

    private float playfieldBorderWidth;

    private float scoresBorderWidth;

    private final Theme theme;

    private float screenWidth;
    private float screenHeight;

    private boolean vertical = false;

    private final BladeSkin skin;


    public BlocksRenderer(TextureAtlas atlas, BladeSkin skin, GameState gameState, Theme theme) {
        this.gameState = gameState;
        this.theme = theme;
        this.skin = skin;

        if(atlas != null) {
            this.background = atlas.findRegion("background");
            this.tile = atlas.findRegion("tile");
        }
    }

    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        // set vertical mode if screen is taller than wide
        vertical = height * .9f > width;

        scale = height / 1080f;

        playfieldBorderWidth = (int)(theme.playfieldBorderWidth * scale);

        if(playfieldBorderWidth < 1)
            playfieldBorderWidth = 1;

        scoresBorderWidth = (int)(theme.scoresBorderWidth * scale);

        if(scoresBorderWidth < 1)
            scoresBorderWidth = 1;

        float playfieldAspect = (float)gameState.playfield.getWidth() / (float)gameState.playfield.getHeight();


        if(vertical) {
            this.bigFont = skin.getFont("small-font");
            this.smallFont = skin.getFont("tiny-font");

            // score size = 2 tiles + 2 spacings
            playfieldHeight = height - (int)DPIUtils.getMarginSize() * 2 - playfieldBorderWidth * 2 - scoresBorderWidth * 2;
            tileSize = playfieldHeight / (gameState.playfield.getHeight() + 4);

            playfieldHeight = tileSize * gameState.playfield.getHeight(); // for perfect fit
            playfieldWidth = (int)(playfieldHeight * playfieldAspect);

            this.org.set((float) ((width - playfieldWidth) / 2.0), DPIUtils.getMarginSize() + playfieldBorderWidth);
        } else {
            this.bigFont = skin.getFont("big-font");
            this.smallFont = skin.getFont("small-font");

            playfieldHeight = height - (int)DPIUtils.getMarginSize() * 2 - playfieldBorderWidth * 2;
            tileSize = playfieldHeight / gameState.playfield.getHeight();
            playfieldHeight = tileSize * gameState.playfield.getHeight(); // for perfect fit
            playfieldWidth = (int)(playfieldHeight * playfieldAspect);

            this.org.set((float) ((width - playfieldWidth) / 2.0), DPIUtils.getMarginSize() + playfieldBorderWidth);
        }
    }

    public void render(SpriteBatch batch) {

        // draw background
        if(background != null) {
            batch.draw(background, 0, 0, screenWidth, screenHeight);
        }

        // draw playfield border
        RectangleRenderer.draw(batch, org.x - playfieldBorderWidth, org.y - playfieldBorderWidth, playfieldWidth + playfieldBorderWidth * 2, playfieldHeight + playfieldBorderWidth * 2, theme.playfieldColor, playfieldBorderWidth, theme.playfieldBorderColor);

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

        // render scores
        if(vertical)
            renderScoresV(batch);
        else
            renderScoresH(batch);

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
                    org.x + (playfieldWidth  - textLayoutBig.width) / 2 - DPIUtils.getMarginSize() * 1.5f,
                    org.y + (playfieldHeight - textLayoutBig.height) / 2 - DPIUtils.getMarginSize() * 1.5f,
                    textLayoutBig.width + DPIUtils.getMarginSize() * 3,
                    textLayoutBig.height + DPIUtils.getMarginSize() * 3,
                    c, scoresBorderWidth, theme.scoresBorderColor);
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

    private void renderScoresH(SpriteBatch batch) {
        float scoresWidth = 4 * tileSize + DPIUtils.getSpacing() * 2;
        float scoresHeight = scoresWidth;
        float posx = org.x - scoresWidth - playfieldBorderWidth + scoresBorderWidth;
        float posy = org.y + gameState.playfield.getHeight() * tileSize - scoresWidth + playfieldBorderWidth;

        renderNextTetramino(batch, posx, posy, scoresWidth, scoresHeight);

        // draw num. lines
        posy -= scoresHeight - scoresBorderWidth;

        // No margin needed if border is not drawn
        if(scoresBorderWidth < 1 || theme.scoresBorderColor == null)
            posx += DPIUtils.getSpacing() * 2;

        renderScoreText(batch, posx, posy, scoresWidth, scoresHeight, "LINES", "" + gameState.lines, theme.scoresTextColor);

        // draw level
        posy -= scoresHeight - scoresBorderWidth;
        renderScoreText(batch, posx, posy, scoresWidth, scoresHeight,"LEVEL", "" + gameState.level, theme.scoresTextColor);

        // draw score
        posx = org.x + playfieldWidth + playfieldBorderWidth - scoresBorderWidth;
        posy = org.y + gameState.playfield.getHeight() * tileSize - scoresWidth + playfieldBorderWidth;

        String titleStr = "SCORE";
        String valueStr = "" + gameState.points;

        // if score is too big, show it with small font
        if(gameState.points > MAX_BIG_SCORE) {
            titleStr += "\n" + gameState.points;
            valueStr = null;
        }

        renderScoreText(batch, posx, posy, scoresWidth, scoresHeight,titleStr, valueStr, theme.scoresTextColor);

        // draw high score
        posy -= scoresHeight - scoresBorderWidth;

        titleStr = "BEST";
        valueStr = "" + gameState.highScore;

        // if score is too big, show it with small font
        if(gameState.highScore > MAX_BIG_SCORE) {
            titleStr += "\n" + gameState.highScore;
            valueStr = null;
        }

        Color highScoreColor = theme.scoresTextColor;
        if (gameState.highScore <= gameState.points) highScoreColor = Color.RED;
        renderScoreText(batch, posx, posy, scoresWidth, scoresHeight,titleStr, valueStr, highScoreColor);

        // draw game mode and time
        posy -= scoresHeight - scoresBorderWidth;
        renderScoreText(batch, posx, posy, scoresWidth, scoresHeight,gameState.gameMode.toString() + "\n" + getTimeString(), null, theme.scoresTextColor);
    }

    private void renderScoresV(SpriteBatch batch) {
        float centerScoresWidth = 4 * tileSize + DPIUtils.getSpacing();
        float scoresWidth = (playfieldWidth - centerScoresWidth) / 2 + scoresBorderWidth * 2;
        float scoresHeight = 2 * tileSize + DPIUtils.getSpacing();
        float posx = org.x - playfieldBorderWidth;
        float posy = org.y + gameState.playfield.getHeight() * tileSize + playfieldBorderWidth + scoresHeight - scoresBorderWidth * 2;

        String titleStr = "SCORE";
        String valueStr = "" + gameState.points;

        // if score is too big, show it with small font
        if(gameState.points > MAX_BIG_SCORE) {
            titleStr += "\n" + gameState.points;
            valueStr = null;
        }

        renderScoreText(batch, posx, posy, scoresWidth, scoresHeight, titleStr, valueStr, theme.scoresTextColor);

        // draw high score
        posx += scoresWidth - scoresBorderWidth;

        titleStr = "BEST";
        valueStr = "" + gameState.highScore;

        // if score is too big, show it with small font
        if(gameState.highScore > MAX_BIG_SCORE) {
            titleStr += "\n" + gameState.highScore;
            valueStr = null;
        }

        Color highScoreColor = theme.scoresTextColor;
        if (gameState.highScore <= gameState.points) highScoreColor = Color.RED;
        renderScoreText(batch, posx, posy, centerScoresWidth, scoresHeight,titleStr, valueStr, highScoreColor);

        // draw game mode and time
        posx += centerScoresWidth - scoresBorderWidth;
        renderScoreText(batch, posx, posy, scoresWidth, scoresHeight,gameState.gameMode.toString() + "\n" + getTimeString(), null, theme.scoresTextColor);

        // Next row
        posx = org.x - playfieldBorderWidth;
        posy = org.y + gameState.playfield.getHeight() * tileSize + playfieldBorderWidth - scoresBorderWidth;

        // draw level
        renderScoreText(batch, posx, posy, scoresWidth, scoresHeight,"LEVEL", "" + gameState.level, theme.scoresTextColor);

        // next tetramino
        posx += scoresWidth - scoresBorderWidth;
        renderNextTetramino(batch, posx, posy, centerScoresWidth, scoresHeight);

        // draw num. lines
        posx += centerScoresWidth - scoresBorderWidth;

        // No margin needed if border is not drawn
        if(scoresBorderWidth < 1 || theme.scoresBorderColor == null)
            posx += DPIUtils.getMarginSize();

        renderScoreText(batch, posx, posy, scoresWidth, scoresHeight,"LINES", "" + gameState.lines, theme.scoresTextColor);
    }

    private void renderNextTetramino(SpriteBatch batch, float posx, float posy, float width, float height) {
        int[][] next = gameState.tetramino.getNext();

        RectangleRenderer.draw(batch, posx, posy, width, height, theme.scoresBgColor, scoresBorderWidth, theme.scoresBorderColor);

        posx = posx + (width - tileSize * next.length) / 2f; // - borderWidth;
        posy = posy + (height - tileSize * (next.length == 4? 1:2)) / 2f;

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

    private void renderScoreText(SpriteBatch batch, float posx, float posy, float width, float height, String title, String value, Color textColor) {
        RectangleRenderer.draw(batch, posx, posy, width, height, theme.scoresBgColor, scoresBorderWidth, theme.scoresBorderColor);

        if(value == null) {
            textLayoutSmall.setText(smallFont, title, textColor, 0f, Align.center, false);

            if(textLayoutSmall.width > width) {
                width = textLayoutSmall.width + DPIUtils.getMarginSize() * 2;
            }

            smallFont.draw(batch, textLayoutSmall, posx + width / 2, posy + (height + textLayoutSmall.height) / 2);
            return;
        }

        textLayoutSmall.setText(smallFont, title, textColor, 0f, Align.center, false);

        if(textLayoutSmall.width > width) {
            width = textLayoutSmall.width + DPIUtils.getMarginSize() * 2;
        }

        textLayoutBig.setText(bigFont, value, textColor, 0f, Align.center, false);

        if(textLayoutBig.width > width) {
            width = textLayoutBig.width + DPIUtils.getMarginSize() * 2;
        }

        smallFont.draw(batch, textLayoutSmall, posx + width / 2, posy + (height + textLayoutSmall.height  + textLayoutBig.height  + DPIUtils.getMarginSize()) / 2);
        bigFont.draw(batch, textLayoutBig, posx + width / 2, posy + (height + textLayoutBig.height - textLayoutSmall.height - DPIUtils.getMarginSize()) / 2);
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
