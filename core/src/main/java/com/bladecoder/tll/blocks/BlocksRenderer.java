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

    public static final int MAX_BIG_SCORE = 99999;
    private BitmapFont smallFont;
    private BitmapFont bigFont;

    private final GlyphLayout textLayoutSmall = new GlyphLayout();
    private final GlyphLayout textLayoutBig = new GlyphLayout();
    private TextureAtlas.AtlasRegion tile;
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

    private float scoresWidth1; // center for vertical mode, left for horizontal mode
    private float scoresWidth2; // sides for vertical mode, right for horizontal mode


    public BlocksRenderer(BladeSkin skin, GameState gameState, Theme theme) {
        this.gameState = gameState;
        this.theme = theme;
        this.skin = skin;
        this.tile = null;

        if(theme.tileName != null) {
            this.tile = skin.getAtlas().findRegion(theme.tileName);
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
            playfieldHeight = height - (int)DPIUtils.getMarginSize() * 4 - playfieldBorderWidth * 2;
            tileSize = playfieldHeight / (gameState.playfield.getHeight() + 4);

            playfieldHeight = tileSize * gameState.playfield.getHeight(); // for perfect fit
            playfieldWidth = (int)(playfieldHeight * playfieldAspect);

            this.org.set((float) ((width - playfieldWidth) / 2.0), DPIUtils.getMarginSize() + playfieldBorderWidth);

            scoresWidth1 = 4 * tileSize + DPIUtils.getSpacing() + scoresBorderWidth * 2;
            scoresWidth2 = (playfieldWidth - scoresWidth1) / 2 + scoresBorderWidth * 2;

        } else {
            this.bigFont = skin.getFont("big-font");
            this.smallFont = skin.getFont("small-font");

            playfieldHeight = height - (int)DPIUtils.getMarginSize() * 2 - playfieldBorderWidth * 2;
            tileSize = playfieldHeight / gameState.playfield.getHeight();
            playfieldHeight = tileSize * gameState.playfield.getHeight(); // for perfect fit
            playfieldWidth = (int)(playfieldHeight * playfieldAspect);

            this.org.set((float) ((width - playfieldWidth) / 2.0), DPIUtils.getMarginSize() + playfieldBorderWidth);

            scoresWidth1 = 4 * tileSize + DPIUtils.getSpacing() * 2;
            scoresWidth2 = scoresWidth1;

            textLayoutSmall.setText(smallFont, "XXXXX", Color.BLACK, 0f, Align.center, false);
            if(textLayoutSmall.width + DPIUtils.getSpacing() * 2 > scoresWidth1) {
                scoresWidth1 = textLayoutSmall.width + DPIUtils.getSpacing() * 2;
            }

        }
        textLayoutSmall.setText(smallFont, "MARATHON\n00:00:00", Color.BLACK, 0f, Align.center, false);
        if(textLayoutSmall.width + DPIUtils.getSpacing() * 2 > scoresWidth2) {
            scoresWidth2 = textLayoutSmall.width + DPIUtils.getSpacing() * 2;
        }
    }

    public void render(SpriteBatch batch) {

        // draw playfield border
        RectangleRenderer.draw(batch, org.x - playfieldBorderWidth, org.y - playfieldBorderWidth, playfieldWidth + playfieldBorderWidth * 2, playfieldHeight + playfieldBorderWidth * 2, theme.playfieldColor, playfieldBorderWidth, theme.playfieldBorderColor);

        // render grid
        if (theme.gridColor != null)
            renderGrid(batch);

        // level up effect: draw a full playfield rectangle
        if(gameState.state == GameState.State.LEVEL_UP) {
            RectangleRenderer.draw(batch, org.x, org.y, tileSize * gameState.playfield.getWidth(), tileSize * gameState.playfield.getHeight(), theme.clearLineColor);
        }

        // render gameState.tetramino
        renderTetramino(batch);

        // render field
        for (int y = 0; y < gameState.playfield.getHeight(); y++) {
            for (int x = 0; x < gameState.playfield.getWidth(); x++) {
                if (gameState.playfield.get(x, y) != 0) {
                    renderTile(batch, org.x + x * tileSize, org.y + y * tileSize, gameState.playfield.get(x, y));
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
                    renderTile(batch, posX + x * tileSize, posY + y * tileSize, gameState.tetramino.get(x, y));
                }
            }
        }
    }

    private void renderScoresH(SpriteBatch batch) {
        float scoresHeight = 4 * tileSize + DPIUtils.getSpacing() * 2;
        float posx = org.x - scoresWidth1 - playfieldBorderWidth + scoresBorderWidth;
        float posy = org.y + gameState.playfield.getHeight() * tileSize - scoresHeight + playfieldBorderWidth;

        renderNextTetramino(batch, posx, posy, scoresWidth1, scoresHeight);

        // draw num. lines
        posy -= scoresHeight - scoresBorderWidth;

        // No margin needed if border is not drawn
        if(scoresBorderWidth < 1 || theme.scoresBorderColor == null) {
            posx += DPIUtils.getSpacing();
        }

        scoresWidth1 = renderScoreText(batch, posx, posy, scoresWidth1, scoresHeight, "LINES", "" + gameState.lines, theme.scoresTextColor);

        // draw level
        posy -= scoresHeight - scoresBorderWidth;
        scoresWidth1 = renderScoreText(batch, posx, posy, scoresWidth1, scoresHeight,"LEVEL", "" + gameState.level, theme.scoresTextColor);

        // draw score
        posx = org.x + playfieldWidth + playfieldBorderWidth - scoresBorderWidth;
        posy = org.y + gameState.playfield.getHeight() * tileSize - scoresHeight + playfieldBorderWidth;

        String titleStr = "SCORE";
        String valueStr = "" + gameState.points;

        // if score is too big, show it with small font
        if(gameState.points > MAX_BIG_SCORE) {
            titleStr += "\n" + gameState.points;
            valueStr = null;
        }

        scoresWidth2 = renderScoreText(batch, posx, posy, scoresWidth2, scoresHeight,titleStr, valueStr, theme.scoresTextColor);

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
        scoresWidth2 = renderScoreText(batch, posx, posy, scoresWidth2, scoresHeight,titleStr, valueStr, highScoreColor);

        // draw game mode and time
        posy -= scoresHeight - scoresBorderWidth;
        scoresWidth2 = renderScoreText(batch, posx, posy, scoresWidth2, scoresHeight,gameState.gameMode.toString() + "\n" + getTimeString(), null, theme.scoresTextColor);
    }

    private void renderScoresV(SpriteBatch batch) {

        float scoresHeight = 2 * tileSize + DPIUtils.getSpacing() * 2 + scoresBorderWidth;
        float posx = (screenWidth - scoresWidth1 - scoresWidth2 * 2)/2 + scoresBorderWidth;
        float posy = org.y + gameState.playfield.getHeight() * tileSize + playfieldBorderWidth + scoresHeight - scoresBorderWidth * 2;

        String titleStr = "SCORE";
        String valueStr = "" + gameState.points;

        // if score is too big, show it with small font
        if(gameState.points > MAX_BIG_SCORE) {
            titleStr += "\n" + gameState.points;
            valueStr = null;
        }

        scoresWidth2 = renderScoreText(batch, posx, posy, scoresWidth2, scoresHeight, titleStr, valueStr, theme.scoresTextColor);

        // draw high score
        posx += scoresWidth2 - scoresBorderWidth;

        titleStr = "BEST";
        valueStr = "" + gameState.highScore;

        // if score is too big, show it with small font
        if(gameState.highScore > MAX_BIG_SCORE) {
            titleStr += "\n" + gameState.highScore;
            valueStr = null;
        }

        Color highScoreColor = theme.scoresTextColor;
        if (gameState.highScore <= gameState.points) highScoreColor = Color.RED;
        scoresWidth1 = renderScoreText(batch, posx, posy, scoresWidth1, scoresHeight,titleStr, valueStr, highScoreColor);

        // draw game mode and time
        posx += scoresWidth1 - scoresBorderWidth;
        scoresWidth2 = renderScoreText(batch, posx, posy, scoresWidth2, scoresHeight,gameState.gameMode.toString() + "\n" + getTimeString(), null, theme.scoresTextColor);

        // Next row
        posx = (screenWidth - scoresWidth1 - scoresWidth2 * 2)/2 + scoresBorderWidth;
        posy = org.y + gameState.playfield.getHeight() * tileSize + playfieldBorderWidth - scoresBorderWidth;

        // draw level
        scoresWidth2 = renderScoreText(batch, posx, posy, scoresWidth2, scoresHeight,"LEVEL", "" + gameState.level, theme.scoresTextColor);

        // next tetramino
        posx += scoresWidth2 - scoresBorderWidth;
        renderNextTetramino(batch, posx, posy, scoresWidth1, scoresHeight);

        // draw num. lines
        posx += scoresWidth1 - scoresBorderWidth;

        // No margin needed if border is not drawn
        if(scoresBorderWidth < 1 || theme.scoresBorderColor == null)
            posx += DPIUtils.getMarginSize();

        scoresWidth2 = renderScoreText(batch, posx, posy, scoresWidth2, scoresHeight,"LINES", "" + gameState.lines, theme.scoresTextColor);
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
                    renderTile(batch, posx + x * tileSize, posy + y * tileSize, next[y][x]);
                    empty = false;
                }
            }

            if(empty) {
                posy -= tileSize;
            }
        }
    }

    // returns the width of the score text. The width can grow if the text is too long.
    private float renderScoreText(SpriteBatch batch, float posx, float posy, float width, float height, String title, String value, Color textColor) {
        float finalWidth = width;

        RectangleRenderer.draw(batch, posx, posy, width, height, theme.scoresBgColor, scoresBorderWidth, theme.scoresBorderColor);

        if(value == null) {
            textLayoutSmall.setText(smallFont, title, textColor, 0f, Align.center, false);

            if(textLayoutSmall.width > finalWidth) {
                finalWidth = textLayoutSmall.width + DPIUtils.getMarginSize() * 2;
            }

            smallFont.draw(batch, textLayoutSmall, posx + width / 2, posy + (height + textLayoutSmall.height) / 2);
            return finalWidth;
        }

        textLayoutSmall.setText(smallFont, title, textColor, 0f, Align.center, false);

        if(textLayoutSmall.width > finalWidth) {
            finalWidth = textLayoutSmall.width + DPIUtils.getMarginSize() * 2;
        }

        textLayoutBig.setText(bigFont, value, textColor, 0f, Align.center, false);

        if(textLayoutBig.width > finalWidth) {
            finalWidth = textLayoutBig.width + DPIUtils.getMarginSize() * 2;
        }

        smallFont.draw(batch, textLayoutSmall, posx + width / 2, posy + (height + textLayoutSmall.height  + textLayoutBig.height  + DPIUtils.getMarginSize()) / 2);
        bigFont.draw(batch, textLayoutBig, posx + width / 2, posy + (height + textLayoutBig.height - textLayoutSmall.height - DPIUtils.getMarginSize()) / 2);

        return finalWidth;
    }

    private void renderTile(SpriteBatch batch, float x, float y, int type) {
        if(tile != null) {
            batch.setColor(theme.tileColors[(type - 1) % theme.tileColors.length]);
            batch.draw(tile, x, y, tileSize, tileSize);
            batch.setColor(Color.WHITE);
        } else
            RectangleRenderer.draw(batch, x, y, tileSize, tileSize, theme.tileColors[(type - 1) % theme.tileColors.length], theme.tileBorderWidth * scale, theme.tileBorderColor);
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
