package com.bladecoder.tll.blocks;

import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.async.AsyncTask;
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

    private float moveDownTime = 0;

    private float stateTimer = 0; // time used for ARE and line clear

    private float speed; // seconds per line

    private boolean softdrop = false;

    private int startLevel = 1;

    private final GameState gameState;

    private final SoundManager soundManager;

    public BlocksLogic(GameState gameState, SoundManager soundManager) {
        this.gameState = gameState;
        this.soundManager = soundManager;
    }

    public void update(float delta) {
        if (gameState.paused) return;

        if (gameState.state != GameState.State.GAME_OVER && gameState.state != GameState.State.WIN) {
            if (gameState.gameMode == GameState.GameMode.ULTRA) gameState.gameTime -= delta;
            else gameState.gameTime += delta;
        }

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
        stateTimer -= delta;

        if (stateTimer < 0) {
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

            if (removedRows == 4) soundManager.tetris();
            else {
                    new Timer().scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            soundManager.lineClear();
                        }
                    }, 0f, 0.2f, removedRows - 1);
            }

            if (removedRows == 1) gameState.points += 100 * gameState.level;
            else if (removedRows == 2) gameState.points += 300 * gameState.level;
            else if (removedRows == 3) gameState.points += 500 * gameState.level;
            else gameState.points += 800 * gameState.level;

            if (gameState.gameMode == GameState.GameMode.MARATHON) {
                // level up every 10 lines
                if (gameState.lines % 10 < removedRows) {
                    levelUp();

                    if (gameState.level > MAX_LEVEL) {
                        gameState.state = GameState.State.WIN;
                    }
                }
            } else if (gameState.gameMode == GameState.GameMode.SPRINT) {
                if (gameState.lines >= 40) {
                    gameState.state = GameState.State.WIN;
                }
            } else if (gameState.gameMode == GameState.GameMode.ULTRA) {
                if (gameState.lines >= 150) {
                    gameState.state = GameState.State.WIN;
                }
            }
        }
    }

    private void updateFalling(float delta) {
        if (isGameOver()) {
            gameState.state = GameState.State.GAME_OVER;
            soundManager.youLose();
            soundManager.musicStop();
            return;
        }

        if (gameState.highScore < gameState.points) {
            updateHighScore();
        }

        if (gameState.gameMode == GameState.GameMode.ULTRA && gameState.gameTime <= 0) {
            gameState.state = GameState.State.WIN;
            soundManager.youWin();
            soundManager.musicStop();
            return;
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
        soundManager.lockdown();
        gameState.playfield.lockDown(gameState.tetramino);

        if (gameState.playfield.isSomeRowFull()) {
            gameState.state = GameState.State.LINE_CLEAR;
        } else {
            stateTimer = INITIAL_DELAY_TIME;

            if (gameState.tetramino.getPos().y > 2) {
                // Pieces that lock in the bottom two rows are followed by 10 frames of entry delay, and each group of 4
                // rows above that has an entry delay 2 frames longer than the last
                stateTimer += INITIAL_DELAY_TIME_EACH_FOUR * (gameState.tetramino.getPos().y - 2) / 4f;
            }

            gameState.tetramino.next();
            gameState.state = GameState.State.ARE;
        }
    }

    // returns true if the move has been done
    public boolean moveLeft() {
        if ((gameState.state != GameState.State.FALLING && gameState.state != GameState.State.ARE) || isPaused()) return true;

        for (int y = 0; y < gameState.tetramino.getHeight(); y++) {
            for (int x = 0; x < gameState.tetramino.getWidth(); x++) {
                if (!gameState.tetramino.isFree(x, y)) {
                    if (!gameState.playfield.isFree(
                            (int) gameState.tetramino.getPos().x + x - 1, (int) gameState.tetramino.getPos().y + y))
                        return false;
                }
            }
        }

        soundManager.move();
        gameState.tetramino.moveLeft();

        return true;
    }

    // returns true if the move has been done

    public boolean moveRight() {
        if ((gameState.state != GameState.State.FALLING && gameState.state != GameState.State.ARE) || isPaused()) return true;

        for (int y = 0; y < gameState.tetramino.getHeight(); y++) {
            for (int x = 0; x < gameState.tetramino.getWidth(); x++) {
                if (!gameState.tetramino.isFree(x, y)) {
                    if (!gameState.playfield.isFree(
                            (int) gameState.tetramino.getPos().x + x + 1, (int) gameState.tetramino.getPos().y + y))
                        return false;
                }
            }
        }

        soundManager.move();
        gameState.tetramino.moveRight();

        return true;
    }

    public void rotateLeft() {
        if ((gameState.state != GameState.State.FALLING && gameState.state != GameState.State.ARE) || isPaused()) return;

        soundManager.rotate();
        gameState.tetramino.rotateLeft();
        if (isGameOver()) gameState.tetramino.rotateRight();
    }

    public void rotateRight() {
        if ((gameState.state != GameState.State.FALLING && gameState.state != GameState.State.ARE) || isPaused()) return;

        soundManager.rotate();
        gameState.tetramino.rotateRight();
        if (isGameOver()) gameState.tetramino.rotateLeft();
    }

    public void drop() {
        if ((gameState.state != GameState.State.FALLING && gameState.state != GameState.State.ARE) || isPaused()) return;

        while (gameState.playfield.canMoveDown(gameState.tetramino)) {
            gameState.tetramino.moveDown();
            gameState.points += 2;
        }

        lockDown();
    }

    public void newGame() {
        // wait 1 second before starting a new game to avoid accidental key press
        if ((gameState.state == GameState.State.GAME_OVER || gameState.state == GameState.State.WIN)
                && stateTimer < 1.0f) return;

        if (gameState.gameMode == GameState.GameMode.ULTRA) gameState.gameTime = 3 * 60; // 3 minutes
        else gameState.gameTime = 0;

        gameState.points = 0;
        gameState.lines = 0;
        setLevel(startLevel);

        gameState.playfield.clear();
        moveDownTime = 0;
        softdrop = false;
        stateTimer = 0;
        gameState.state = GameState.State.ARE;

        gameState.tetramino.next();

        soundManager.musicPlay();
    }

    boolean isGameOver() {
        for (int y = 0; y < gameState.tetramino.getHeight(); y++) {
            for (int x = 0; x < gameState.tetramino.getWidth(); x++) {
                if (!gameState.tetramino.isFree(x, y)) {
                    if (!gameState.playfield.isFree(
                            (int) gameState.tetramino.getPos().x + x, (int) gameState.tetramino.getPos().y + y))
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
        soundManager.levelUp();
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

    public void setGameMode(GameState.GameMode gameMode) {
        gameState.gameMode = gameMode;

        switch (gameState.gameMode) {
            case MARATHON:
                gameState.highScore = Config.getInstance().getPref("highscore", 0);
                break;
            case SPRINT:
                gameState.highScore = Config.getInstance().getPref("highscore_sprint", 0);
                break;
            case ULTRA:
                gameState.highScore = Config.getInstance().getPref("highscore_ultra", 0);
                break;
        }
    }

    private void updateHighScore() {
        gameState.highScore = gameState.points;

        switch (gameState.gameMode) {
            case MARATHON:
                Config.getInstance().setPref("highscore", gameState.highScore);
                break;
            case SPRINT:
                Config.getInstance().setPref("highscore_sprint", gameState.highScore);
                break;
            case ULTRA:
                Config.getInstance().setPref("highscore_ultra", gameState.highScore);
                break;
        }
        Config.getInstance().savePrefs();
    }

    public void pause() {
        if (gameState.state != GameState.State.GAME_OVER && gameState.state != GameState.State.WIN) {
            gameState.paused = true;
            soundManager.musicPause();
        }
    }

    public void resume() {
        if(gameState.paused) {
            gameState.paused = false;
            soundManager.musicPlay();
        }

        gameState.paused = false;
    }

    public boolean isPaused() {
        return gameState.paused;
    }
}
