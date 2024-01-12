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

    private final BitmapFont font;
    private final GlyphLayout textLayout = new GlyphLayout();
    private final TextureAtlas.AtlasRegion tile;
    private final Vector2 org = new Vector2();

    private final GameState gameState;

    private int playfieldHeight;
    private int playfieldWidth;

    private float scale = 1.0f;

    private int tileSize;

    private float borderWidth;

    private final Theme theme;


    public BlocksRenderer(TextureAtlas.AtlasRegion tile, BladeSkin skin, GameState gameState, Theme theme) {
        this.font = skin.getFont("big-font");
        this.tile = tile;
        this.gameState = gameState;
        this.theme = theme;

        borderWidth = theme.borderWidth;
    }

    public void resize(int width, int height) {
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
    }

    public void render(SpriteBatch batch) {

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
                    RectangleRenderer.draw(batch, org.x, org.y + y * tileSize, tileSize * gameState.playfield.getWidth(), tileSize, Color.GREEN);
                }
            }
        }

        float size = 4 * tileSize + DPIUtils.getSpacing() * 2;
        float posx = org.x - size;
        float posy = org.y + gameState.playfield.getHeight() * tileSize - size + borderWidth;

        renderNextTetramino(batch, posx, posy, size);

        // draw num. lines
        posy -= size - borderWidth;
        RectangleRenderer.draw(batch, posx, posy, size, size, theme.scoresBgColor, borderWidth, theme.scoresBorderColor);
        String str = "Lines\n" + gameState.lines;
        textLayout.setText(font, str, theme.scoresTextColor, 0f, Align.center, false);
        font.draw(batch, textLayout, posx + size / 2, posy + (size + textLayout.height) / 2);

        // draw level
        posy -= size - borderWidth;
        RectangleRenderer.draw(batch, posx, posy, size, size, theme.scoresBgColor, borderWidth, theme.scoresBorderColor);
        str = "Level\n" + gameState.level;
        textLayout.setText(font, str, theme.scoresTextColor, 0.0f, Align.center, false);
        font.draw(batch, textLayout, posx + size / 2, posy + (size + textLayout.height) / 2);

        // draw score
        posx = org.x + playfieldWidth + borderWidth;
        posy = org.y + gameState.playfield.getHeight() * tileSize - size + borderWidth;
        RectangleRenderer.draw(batch, posx, posy, size, size, theme.scoresBgColor, borderWidth, theme.scoresBorderColor);
        str = "Score\n" + gameState.points;
        textLayout.setText(font, str, theme.scoresTextColor, 0.0f, Align.center, false);
        font.draw(batch, textLayout, posx + size / 2, posy + (size + textLayout.height) / 2);

        // draw high score
        posy -= size - borderWidth;
        Color highScoreColor = theme.scoresTextColor;
        if (gameState.highScore == gameState.points) highScoreColor = Color.RED;

        RectangleRenderer.draw(batch, posx, posy, size, size, theme.scoresBgColor, borderWidth, theme.scoresBorderColor);
        str = "High\n" + gameState.highScore;
        textLayout.setText(font, str, highScoreColor, 0.0f, Align.center, false);
        font.draw(batch, textLayout, posx + size / 2, posy + (size + textLayout.height) / 2);

        // draw game over or win text
        if (gameState.state == GameState.State.GAME_OVER || gameState.state == GameState.State.WIN) {
            String s = "GAME OVER";

            if (gameState.state == GameState.State.WIN) s = "YOU WIN!";

            textLayout.setText(font, s, theme.scoresTextColor, 0.0f, Align.center, false);
            RectangleRenderer.draw(
                    batch,
                    org.x + (playfieldWidth  - textLayout.width) / 2 - DPIUtils.getMarginSize() * 2,
                    org.y + (playfieldHeight - textLayout.height) / 2 - DPIUtils.getMarginSize() * 2,
                    textLayout.width + DPIUtils.getMarginSize() * 4,
                    textLayout.height + DPIUtils.getMarginSize() * 4,
                    Color.BLACK);
            font.draw(batch, textLayout,  org.x  + (float)playfieldWidth / 2, org.y + (float) playfieldHeight / 2 + textLayout.height / 2);
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

        posx = posx + (size - tileSize * next.length) / 2f;
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

    private void renderTile(SpriteBatch batch, float x, float y) {
        if(tile != null)
            batch.draw(tile, x, y, tileSize, tileSize);
        else
            RectangleRenderer.draw(batch, x, y, tileSize, tileSize, theme.tileColor, theme.tileBorderWidth * scale, theme.tileBorderColor);
    }

    private void renderGrid(SpriteBatch batch) {
        // Draw horizontal lines
        for (int y = 1; y < gameState.playfield.getHeight(); y++) {
            RectangleRenderer.draw(batch, org.x, org.y + y * tileSize, playfieldWidth, borderWidth, theme.gridColor);
        }

        // Draw vertical lines
        for (int x = 1; x < gameState.playfield.getWidth(); x++) {
            RectangleRenderer.draw(batch, org.x + x * tileSize, org.y, borderWidth, playfieldHeight, theme.gridColor);
        }
    }
}
