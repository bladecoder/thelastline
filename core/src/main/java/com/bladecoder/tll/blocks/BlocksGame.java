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

public class BlocksGame {
    public static final int WIDTH= 10;
    public static final int HEIGHT= 22;

    private static final int SOFT_DROP_SPEED = 20;
    private static final float INIT_SPEED = 1f;

    private final BitmapFont font;

    private final GlyphLayout textLayout = new GlyphLayout();

    private float moveDownTime = 0;

    private final Vector2 org = new Vector2();

    private final TextureAtlas.AtlasRegion tile;
    private final SpriteBatch batch;

    private final PlayField playfield;
    private final Tetramino tetramino = new Tetramino();

    private float speed = INIT_SPEED; // seconds per line

    private int points = 0;
    private int lines = 0;
    private int level = 1;
    private boolean softdrop = false;

    private boolean win = false;

    public BlocksGame(SpriteBatch batch, TextureAtlas.AtlasRegion tile, BladeSkin skin) {
        this.font = skin.getFont("big-font");
        this.org.set(723, 60);

        this.batch = batch;
        this.tile = tile;

        this.playfield = new PlayField(WIDTH, HEIGHT);
    }

    public void update(float delta) {
        if(isGameOver() || win) {
            return;
        }

        // update tetramino
        moveDownTime += delta;

        float s = speed;

        if(softdrop)
            s = speed / SOFT_DROP_SPEED;

        if (moveDownTime > s) {
            moveDownTime = 0;

            if(playfield.canMoveDown(tetramino)) {
                tetramino.moveDown();
                if(softdrop)
                    points += 1;
            } else {
                playfield.nextTetramino(tetramino);
            }

        }

        // check if we have to remove a row
        int removedRows = 0;
        for(int y=0; y<playfield.getHeight(); y++) {
            if (playfield.isRowFull(y)) {
                playfield.removeRow(y);
                removedRows++;
            }
        }

        if(removedRows > 0) {
            lines += removedRows;

            if(removedRows == 1)
                points += 100 * level;
            else if(removedRows == 2)
                points += 300 * level;
            else if(removedRows == 3)
                points += 500 * level;
            else
                points += 800 * level;

            // level up every 10 lines
            if(lines % 10 == 0) {
                levelUp();

                if(level == 16) {
                    win = true;
                }
            }
        }
    }

    public void render() {
        float width = tile.getRegionWidth();
        float height = tile.getRegionHeight();

        // render tetramino
        tetramino.render(batch, tile, org);

        // render field
        for(int y=0; y < playfield.getHeight(); y++) {
            for(int x=0; x < playfield.getWidth(); x++) {
                if(playfield.get(x, y) != 0) {
                    batch.draw(tile, org.x + x * width, org.y + y * height);
                }
            }
        }

        // draw score
        String scoreStr = "Score\n" + points + "\n\nLines\n" + lines + "\n\nLevel\n" + level;
        textLayout.setText(font, scoreStr, Color.GREEN, 0.0f, Align.center, false);
        font.draw(batch, textLayout, org.x - textLayout.width / 2 - DPIUtils.getMarginSize() * 2, 800);

        // draw game over or win text
        if (isGameOver() || win ) {
            String s = "GAME OVER";

            if(win)
                s = "YOU WIN!";

            textLayout.setText(font, s, Color.GREEN, 0.0f, Align.center, false);
            RectangleRenderer.draw(batch, (float) 1920 / 2 - textLayout.width / 2 - DPIUtils.getMarginSize() * 2, (float) 1080 / 2 - textLayout.height / 2 - DPIUtils.getMarginSize() * 2, textLayout.width + DPIUtils.getMarginSize() * 4, textLayout.height + DPIUtils.getMarginSize() * 4, Color.BLACK);
            font.draw(batch, textLayout, (float) 1920 / 2, (float) 1080 / 2 + textLayout.height / 2);
        }
    }

    public void moveLeft() {
        for(int y=0; y< tetramino.getHeight(); y++) {
            for(int x=0; x< tetramino.getWidth(); x++) {
                if(! tetramino.isFree(x, y)) {
                    if(!playfield.isFree((int)tetramino.getPos().x + x - 1, (int)tetramino.getPos().y + y))
                        return;
                }
            }
        }

        tetramino.moveLeft();
    }

    public void moveRight() {
        for(int y=0; y<tetramino.getHeight(); y++) {
            for(int x=0; x<tetramino.getWidth(); x++) {
                if(!tetramino.isFree(x, y)) {
                    if(!playfield.isFree((int)tetramino.getPos().x + x + 1, (int)tetramino.getPos().y + y))
                        return;
                }
            }
        }

        tetramino.moveRight();
    }

    public void rotateLeft() {
        tetramino.rotateLeft();
        if(isGameOver())
            tetramino.rotateRight();
    }

    public void rotateRight() {
        tetramino.rotateRight();
        if(isGameOver())
            tetramino.rotateLeft();
    }

    public void drop() {
        while(playfield.canMoveDown(tetramino)) {
            tetramino.moveDown();
            points += 2;
        }

        playfield.nextTetramino(tetramino);
    }

    public void newGame() {
        points = 0;
        lines = 0;
        level = 1;
        speed = INIT_SPEED;

        playfield.clear();
        moveDownTime = 0;
        softdrop = false;
        win = false;

        tetramino.next();
    }

    boolean isGameOver() {
        for(int y=0; y<tetramino.getHeight(); y++) {
            for(int x=0; x<tetramino.getWidth(); x++) {
                if(!tetramino.isFree(x, y)) {
                    if(!playfield.isFree((int)tetramino.getPos().x + x, (int)tetramino.getPos().y + y))
                        return true;
                }
            }
        }

        return false;
    }

    public void setSoftDrop(boolean value) {
        softdrop = value;
    }

    private void levelUp() {
        level++;
        speed = (float) Math.pow(0.8 - (level - 1) * 0.007f, level - 1);
    }

    public boolean hasWin() {
        return win;
    }
}
