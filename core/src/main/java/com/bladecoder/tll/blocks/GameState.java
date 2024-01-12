package com.bladecoder.tll.blocks;

public class GameState {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 20;

    enum State {
        FALLING,
        ARE,
        LINE_CLEAR,
        GAME_OVER,
        WIN
    }

    State state = State.ARE;

    final PlayField playfield;
    final Tetramino tetramino;

    int points = 0;
    int lines = 0;
    int level = 1;
    int highScore = 0;

    public GameState() {
        this(WIDTH, HEIGHT);
    }

    public GameState(int width, int height) {
        playfield = new PlayField(width, height);
        tetramino = new Tetramino();
    }
}