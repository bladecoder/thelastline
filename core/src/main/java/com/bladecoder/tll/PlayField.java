package com.bladecoder.tll;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class PlayField {
    public static final int WIDTH= 10;
    public static final int HEIGHT= 22;

    private int[][] field;

    private TextureAtlas.AtlasRegion tile;
    private SpriteBatch batch;

    private Vector2 org = new Vector2();

    private Tetramino tetramino;

    private float speed = 1 / 1f; // seconds per line
    private float moveDownTime = 0;

    private int points = 0;
    private int lines = 0;

    private int level = 1;

    public PlayField(SpriteBatch batch, TextureAtlas.AtlasRegion tile) {
        field = new int[WIDTH][HEIGHT];
        this.batch = batch;
        this.tile = tile;

        org.set(723, 60);

        tetramino = new Tetramino();
    }

    public void clear() {
        for(int x=0; x<WIDTH; x++) {
            for(int y=0; y<HEIGHT; y++) {
                field[x][y] = 0;
            }
        }

        points = 0;
        lines = 0;
        level = 1;
    }

    public boolean isFree(int x, int y) {
        if(x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
            return false;

        return field[x][y] == 0;
    }

    public void set(int x, int y, int value) {
        field[x][y] = value;
    }

    public int get(int x, int y) {
        return field[x][y];
    }

    public void removeRow(int row) {
        for(int y=row; y<HEIGHT-1; y++) {
            for(int x=0; x<WIDTH; x++) {
                field[x][y] = field[x][y+1];
            }
        }
    }

    public boolean isRowFull(int row) {
        for(int x=0; x<WIDTH; x++) {
            if(field[x][row] == 0)
                return false;
        }

        return true;
    }

    public int getRows() {
        return HEIGHT;
    }

    public int getCols() {
        return WIDTH;
    }

    public void dump() {
        for(int y=0; y<HEIGHT; y++) {
            for(int x=0; x<WIDTH; x++) {
                System.out.print(field[x][y]);
            }
            System.out.println();
        }
    }

    public void render(float delta) {
        float width = tile.getRegionWidth();
        float height = tile.getRegionHeight();

        // update and render tetramino
        moveDownTime += delta;
        if (moveDownTime > speed) {
            moveDownTime = 0;

            if(canMoveDown())
                tetramino.moveDown();
            else {
                nextTetramino();
            }

        }

        tetramino.render(delta, batch, tile, org);

        // render field
        for(int y=0; y<HEIGHT; y++) {
            for(int x=0; x<WIDTH; x++) {
                if(field[x][y] != 0) {
                    batch.draw(tile, org.x + x * width, org.y + y * height);
                }
            }
        }

        // check if we have to remove a row
        int removedRows = 0;
        for(int y=0; y<HEIGHT; y++) {
            if (isRowFull(y)) {
                removeRow(y);
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
        }

        if(isGameOver()) {
            clear();
            tetramino.next();
        }
    }

    private void nextTetramino() {
        for(int y=0; y<tetramino.getHeight(); y++) {
            for(int x=0; x<tetramino.getWidth(); x++) {
                if(tetramino.get(x, y) != 0) {
                    set((int)tetramino.getPos().x + x, (int)tetramino.getPos().y + y, 1);
                }
            }
        }

        tetramino.next();
    }

    private boolean canMoveDown() {
        for(int y=0; y<tetramino.getHeight(); y++) {
            for(int x=0; x<tetramino.getWidth(); x++) {
                if(!tetramino.isFree(x, y)) {
                    if(!isFree((int)tetramino.getPos().x + x, (int)tetramino.getPos().y + y - 1))
                        return false;
                }
            }
        }

        return true;
    }

    private boolean isGameOver() {
        for(int y=0; y<tetramino.getHeight(); y++) {
            for(int x=0; x<tetramino.getWidth(); x++) {
                if(!tetramino.isFree(x, y)) {
                    if(!isFree((int)tetramino.getPos().x + x, (int)tetramino.getPos().y + y))
                        return true;
                }
            }
        }

        return false;
    }

    public void moveLeft() {
        for(int y=0; y<tetramino.getHeight(); y++) {
            for(int x=0; x<tetramino.getWidth(); x++) {
                if(!tetramino.isFree(x, y)) {
                    if(!isFree((int)tetramino.getPos().x + x - 1, (int)tetramino.getPos().y + y))
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
                    if(!isFree((int)tetramino.getPos().x + x + 1, (int)tetramino.getPos().y + y))
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

    public void moveDown() {
        if(canMoveDown())
            tetramino.moveDown();
    }

    public void drop() {
        while(canMoveDown())
            tetramino.moveDown();
    }
}
