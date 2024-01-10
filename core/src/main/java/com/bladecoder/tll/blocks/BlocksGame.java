package com.bladecoder.tll.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.bladecoder.tll.ui.BladeSkin;
import com.bladecoder.tll.util.Config;
import com.bladecoder.tll.util.DPIUtils;
import com.bladecoder.tll.util.RectangleRenderer;

public class BlocksGame {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 22;

    private static final int SOFT_DROP_SPEED = 20; // 20x faster
    private static final float[] SPEEDS = {
        0.800f, 0.717f, 0.633f, 0.550f, 0.467f, 0.383f, 0.300f, 0.217f, 0.133f, 0.100f,
        0.083f, 0.083f, 0.083f, 0.067f, 0.067f, 0.067f, 0.050f, 0.050f, 0.050f, 0.033f,
        0.033f, 0.033f, 0.033f, 0.033f, 0.033f, 0.033f, 0.033f, 0.033f, 0.033f, 0.017f
    };

    private static final float INITIAL_DELAY_TIME = 1f / 60f * 10f; // 10 frames
    private static final float INITIAL_DELAY_TIME_EACH_FOUR = 1f / 60f * 2f; // 2 frames

    private static final float LINE_CLEAR_TIME = 1f / 60f * 20f; // 20 frames

    public static final int MAX_LEVEL = 30;

    private final BitmapFont font;

    private final GlyphLayout textLayout = new GlyphLayout();

    private float moveDownTime = 0;

    private float time = 0; // time used for ARE and line clear

    private final Vector2 org = new Vector2();

    private final TextureAtlas.AtlasRegion tile;
    private final SpriteBatch batch;

    private final PlayField playfield;
    private final Tetramino tetramino = new Tetramino();

    private float speed; // seconds per line

    private int points = 0;
    private int lines = 0;
    private int level = 1;
    private int highScore = 0;
    private boolean softdrop = false;

    private int startLevel = 1;

    private State state = State.ARE;

    enum State {
        FALLING,
        ARE,
        LINE_CLEAR,
        GAME_OVER,
        WIN
    }

    public BlocksGame(SpriteBatch batch, TextureAtlas.AtlasRegion tile, BladeSkin skin) {
        this.font = skin.getFont("big-font");
        this.org.set(723, 60);

        this.batch = batch;
        this.tile = tile;

        this.playfield = new PlayField(WIDTH, HEIGHT);

        highScore = Config.getInstance().getPref("highscore", 0);
    }

    public void update(float delta) {
        switch (state) {
            case ARE:
                updateARE(delta);
                break;
            case LINE_CLEAR:
                updateLineClear(delta);
                break;
            case FALLING:
                updateFalling(delta);
                break;
            default:
                time += delta; // new game delay
                break;
        }
    }

    private void updateARE(float delta) {
        time += delta;

        if (time > INITIAL_DELAY_TIME) {
            time = 0;
            state = State.FALLING;
        }
    }

    private void updateLineClear(float delta) {
        time += delta;

        if (time < LINE_CLEAR_TIME) return;

        tetramino.next();
        time = 0;
        state = State.FALLING;

        // check if we have to remove a row
        int removedRows = 0;
        for (int y = 0; y < playfield.getHeight(); y++) {
            if (playfield.isRowFull(y)) {
                playfield.removeRow(y);
                removedRows++;
                y--;
            }
        }

        if (removedRows > 0) {
            lines += removedRows;

            if (removedRows == 1) points += 100 * level;
            else if (removedRows == 2) points += 300 * level;
            else if (removedRows == 3) points += 500 * level;
            else points += 800 * level;

            // level up every 10 lines
            if (lines % 10 == 0) {
                levelUp();

                if (level > MAX_LEVEL) {
                    state = State.WIN;
                }
            }
        }
    }

    private void updateFalling(float delta) {
        if (isGameOver()) {
            state = State.GAME_OVER;
            return;
        }

        if (highScore < points) {
            highScore = points;
            Config.getInstance().setPref("highscore", highScore);
            Config.getInstance().savePrefs();
        }

        // update tetramino
        moveDownTime += delta;

        float s = speed;

        if (softdrop) s = speed / SOFT_DROP_SPEED;

        if (moveDownTime > s) {
            moveDownTime = 0;

            if (playfield.canMoveDown(tetramino)) {
                tetramino.moveDown();
                if (softdrop) points += 1;
            } else {
                lockDown();
            }
        }
    }

    private void lockDown() {
        playfield.lockDown(tetramino);

        if (playfield.isSomeRowFull()) {
            state = State.LINE_CLEAR;
        } else {
            tetramino.next();
            state = State.ARE;
        }
    }

    public void render() {
        float width = tile.getRegionWidth();
        float height = tile.getRegionHeight();

        // render tetramino
        renderTetramino();

        // render field
        for (int y = 0; y < playfield.getHeight(); y++) {
            for (int x = 0; x < playfield.getWidth(); x++) {
                if (playfield.get(x, y) != 0) {
                    batch.draw(tile, org.x + x * width, org.y + y * height);
                }
            }
        }

        if(state == State.LINE_CLEAR) {
            // draw rectangle in the cleared lines
            for(int y=0; y<playfield.getHeight(); y++) {
                if(playfield.isRowFull(y)) {
                    RectangleRenderer.draw(batch, org.x, org.y + y * height, width * WIDTH, height, Color.GREEN);
                }
            }
        }

        // draw score
        String scoreStr = "Score\n" + points + "\n\nLines\n" + lines + "\n\nLevel\n" + level;
        textLayout.setText(font, scoreStr, Color.GREEN, 0.0f, Align.center, false);
        font.draw(batch, textLayout, org.x - textLayout.width / 2 - DPIUtils.getMarginSize() * 2, 800);

        // draw high score
        Color highScoreColor = Color.GREEN;
        if (highScore == points) highScoreColor = Color.RED;

        textLayout.setText(font, "High Score\n" + highScore, highScoreColor, 0.0f, Align.center, false);
        font.draw(batch, textLayout, org.x + width * WIDTH + textLayout.width / 2 + DPIUtils.getMarginSize() * 2, 800);

        // draw game over or win text
        if (state == State.GAME_OVER || state == State.WIN) {
            String s = "GAME OVER";

            if (state == State.WIN) s = "YOU WIN!";

            textLayout.setText(font, s, Color.GREEN, 0.0f, Align.center, false);
            RectangleRenderer.draw(
                    batch,
                    (float) 1920 / 2 - textLayout.width / 2 - DPIUtils.getMarginSize() * 2,
                    (float) 1080 / 2 - textLayout.height / 2 - DPIUtils.getMarginSize() * 2,
                    textLayout.width + DPIUtils.getMarginSize() * 4,
                    textLayout.height + DPIUtils.getMarginSize() * 4,
                    Color.BLACK);
            font.draw(batch, textLayout, (float) 1920 / 2, (float) 1080 / 2 + textLayout.height / 2);
        }
    }

    public void renderTetramino() {
        float width = tile.getRegionWidth();
        float height = tile.getRegionHeight();
        float posX = org.x + tetramino.getPos().x * width;
        float posY = org.y + tetramino.getPos().y * height;

        for (int y = 0; y < tetramino.getCurrent().length; y++) {
            for (int x = 0; x < tetramino.getCurrent()[y].length; x++) {
                if (!tetramino.isFree(x, y)) {
                    batch.draw(tile, posX + x * width, posY + y * height);
                }
            }
        }

        // Render the next tetramino
        int[][] next = tetramino.getNext();
        int leftAlign = next[0].length + 1;
        for (int y = 0; y < next.length; y++) {
            for (int x = 0; x < next[y].length; x++) {
                if (next[y][x] != 0) {
                    batch.draw(tile, org.x + (x - leftAlign) * width, org.y + (y + 17) * height);
                }
            }
        }
    }

    public void moveLeft() {
        if (state != State.FALLING && state != State.ARE) return;

        for (int y = 0; y < tetramino.getHeight(); y++) {
            for (int x = 0; x < tetramino.getWidth(); x++) {
                if (!tetramino.isFree(x, y)) {
                    if (!playfield.isFree((int) tetramino.getPos().x + x - 1, (int) tetramino.getPos().y + y)) return;
                }
            }
        }

        tetramino.moveLeft();
    }

    public void moveRight() {
        if (state != State.FALLING && state != State.ARE) return;

        for (int y = 0; y < tetramino.getHeight(); y++) {
            for (int x = 0; x < tetramino.getWidth(); x++) {
                if (!tetramino.isFree(x, y)) {
                    if (!playfield.isFree((int) tetramino.getPos().x + x + 1, (int) tetramino.getPos().y + y)) return;
                }
            }
        }

        tetramino.moveRight();
    }

    public void rotateLeft() {
        if (state != State.FALLING && state != State.ARE) return;

        tetramino.rotateLeft();
        if (isGameOver()) tetramino.rotateRight();
    }

    public void rotateRight() {
        if (state != State.FALLING && state != State.ARE) return;

        tetramino.rotateRight();
        if (isGameOver()) tetramino.rotateLeft();
    }

    public void drop() {
        if (state != State.FALLING && state != State.ARE) return;

        while (playfield.canMoveDown(tetramino)) {
            tetramino.moveDown();
            points += 2;
        }

        lockDown();
    }

    public void newGame() {
        // wait 1 second before starting a new game to avoid accidental key press
        if ((state == State.GAME_OVER || state == State.WIN) && time < 1.0f) return;

        points = 0;
        lines = 0;
        setLevel(startLevel);

        playfield.clear();
        moveDownTime = 0;
        softdrop = false;
        time = 0;
        state = State.ARE;

        tetramino.next();
    }

    boolean isGameOver() {
        for (int y = 0; y < tetramino.getHeight(); y++) {
            for (int x = 0; x < tetramino.getWidth(); x++) {
                if (!tetramino.isFree(x, y)) {
                    if (!playfield.isFree((int) tetramino.getPos().x + x, (int) tetramino.getPos().y + y)) return true;
                }
            }
        }

        return false;
    }

    public void setSoftDrop(boolean value) {
        softdrop = value;
    }

    private void levelUp() {
        setLevel(level + 1);
    }

    private void setLevel(int level) {
        this.level = level;
        // speed = (float) Math.pow(0.8 - (level - 1) * 0.007f, level - 1);
        speed = SPEEDS[level - 1];
    }

    public void setStartLevel(int level) {
        this.startLevel = level;
    }

    public int getLevel() {
        return level;
    }

    public State getState() {
        return state;
    }
}
