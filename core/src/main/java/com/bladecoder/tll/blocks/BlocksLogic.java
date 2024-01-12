package com.bladecoder.tll.blocks;

import com.bladecoder.tll.util.Config;

public class BlocksLogic {
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

    public enum GameMode {
        MARATHON
    }

    private GameMode gameMode = GameMode.MARATHON;

    private float moveDownTime = 0;

    private float stateTimer = 0; // time used for ARE and line clear

    private float speed; // seconds per line

    private boolean softdrop = false;

    private int startLevel = 1;

    private GameState gameState;

    public BlocksLogic(GameState gameState) {
        this.gameState = gameState;
        this.gameState.highScore = Config.getInstance().getPref("highscore", 0);
    }

    public void update(float delta) {
        switch (gameState.state) {
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
                stateTimer += delta; // new game delay
                break;
        }
    }

    private void updateARE(float delta) {
        stateTimer += delta;

        if (stateTimer > INITIAL_DELAY_TIME) {
            stateTimer = 0;
            gameState.state = GameState.State.FALLING;
        }
    }

    private void updateLineClear(float delta) {
        stateTimer += delta;

        if (stateTimer < LINE_CLEAR_TIME) return;

        gameState.tetramino.next();
        stateTimer = 0;
        gameState.state = GameState.State.FALLING;

        // check if we have to remove a row
        int removedRows = 0;
        for (int y = 0; y < gameState.playfield.getHeight(); y++) {
            if (gameState.playfield.isRowFull(y)) {
                gameState.playfield.removeRow(y);
                removedRows++;
                y--;
            }
        }

        if (removedRows > 0) {
            gameState.lines += removedRows;

            if (removedRows == 1) gameState.points += 100 * gameState.level;
            else if (removedRows == 2) gameState.points += 300 * gameState.level;
            else if (removedRows == 3) gameState.points += 500 * gameState.level;
            else gameState.points += 800 * gameState.level;

            // level up every 10 lines
            if (gameState.lines % 10 == 0) {
                levelUp();

                if (gameState.level > MAX_LEVEL) {
                    gameState.state = GameState.State.WIN;
                }
            }
        }
    }

    private void updateFalling(float delta) {
        if (isGameOver()) {
            gameState.state = GameState.State.GAME_OVER;
            return;
        }

        if (gameState.highScore < gameState.points) {
            gameState.highScore = gameState.points;
            Config.getInstance().setPref("highscore", gameState.highScore);
            Config.getInstance().savePrefs();
        }

        moveDownTime += delta;

        float s = speed;

        if (softdrop) s = speed / SOFT_DROP_SPEED;

        if (moveDownTime > s) {
            moveDownTime = 0;

            if (gameState.playfield.canMoveDown(gameState.tetramino)) {
                gameState.tetramino.moveDown();
                if (softdrop) gameState.points += 1;
            } else {
                lockDown();
            }
        }
    }

    private void lockDown() {
        gameState.playfield.lockDown(gameState.tetramino);

        if (gameState.playfield.isSomeRowFull()) {
            gameState.state = GameState.State.LINE_CLEAR;
        } else {
            gameState.tetramino.next();
            gameState.state = GameState.State.ARE;
        }
    }

    public void moveLeft() {
        if (gameState.state != GameState.State.FALLING && gameState.state != GameState.State.ARE) return;

        for (int y = 0; y < gameState.tetramino.getHeight(); y++) {
            for (int x = 0; x < gameState.tetramino.getWidth(); x++) {
                if (!gameState.tetramino.isFree(x, y)) {
                    if (!gameState.playfield.isFree((int) gameState.tetramino.getPos().x + x - 1, (int) gameState.tetramino.getPos().y + y)) return;
                }
            }
        }

        gameState.tetramino.moveLeft();
    }

    public void moveRight() {
        if (gameState.state != GameState.State.FALLING && gameState.state != GameState.State.ARE) return;

        for (int y = 0; y < gameState.tetramino.getHeight(); y++) {
            for (int x = 0; x < gameState.tetramino.getWidth(); x++) {
                if (!gameState.tetramino.isFree(x, y)) {
                    if (!gameState.playfield.isFree((int) gameState.tetramino.getPos().x + x + 1, (int) gameState.tetramino.getPos().y + y)) return;
                }
            }
        }

        gameState.tetramino.moveRight();
    }

    public void rotateLeft() {
        if (gameState.state != GameState.State.FALLING && gameState.state != GameState.State.ARE) return;

        gameState.tetramino.rotateLeft();
        if (isGameOver()) gameState.tetramino.rotateRight();
    }

    public void rotateRight() {
        if (gameState.state != GameState.State.FALLING && gameState.state != GameState.State.ARE) return;

        gameState.tetramino.rotateRight();
        if (isGameOver()) gameState.tetramino.rotateLeft();
    }

    public void drop() {
        if (gameState.state != GameState.State.FALLING && gameState.state != GameState.State.ARE) return;

        while (gameState.playfield.canMoveDown(gameState.tetramino)) {
            gameState.tetramino.moveDown();
            gameState.points += 2;
        }

        lockDown();
    }

    public void newGame() {
        // wait 1 second before starting a new game to avoid accidental key press
        if ((gameState.state == GameState.State.GAME_OVER || gameState.state == GameState.State.WIN) && stateTimer < 1.0f) return;

        gameState.points = 0;
        gameState.lines = 0;
        setLevel(startLevel);

        gameState.playfield.clear();
        moveDownTime = 0;
        softdrop = false;
        stateTimer = 0;
        gameState.state = GameState.State.ARE;

        gameState.tetramino.next();
    }

    boolean isGameOver() {
        for (int y = 0; y < gameState.tetramino.getHeight(); y++) {
            for (int x = 0; x < gameState.tetramino.getWidth(); x++) {
                if (!gameState.tetramino.isFree(x, y)) {
                    if (!gameState.playfield.isFree((int) gameState.tetramino.getPos().x + x, (int) gameState.tetramino.getPos().y + y)) return true;
                }
            }
        }

        return false;
    }

    public void setSoftDrop(boolean value) {
        softdrop = value;
    }

    private void levelUp() {
        setLevel(gameState.level + 1);
    }

    private void setLevel(int level) {
        gameState.level = level;
        // speed = (float) Math.pow(0.8 - (level - 1) * 0.007f, level - 1);
        speed = SPEEDS[level - 1];
    }

    public void setStartLevel(int level) {
        this.startLevel = level;
    }

    GameState.State getState() {
        return gameState.state;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
}
