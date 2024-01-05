package com.bladecoder.tll;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class Tetramino {
    public static final int I = 1;
    public static final int J = 2;
    public static final int L = 3;
    public static final int O = 4;
    public static final int S = 5;
    public static final int T = 6;
    public static final int Z = 7;

    // 4 rotations, 4 rows, 4 columns
    public static final int[][][] SHAPE_I = {
            {
                { 0, 0, 0, 0 },
                { I, I, I, I },
                { 0, 0, 0, 0 },
                { 0, 0, 0, 0 }
            },
            {
                { 0, I, 0, 0 },
                { 0, I, 0, 0 },
                { 0, I, 0, 0 },
                { 0, I, 0, 0 }
            },
    };

    public static final int[][][] SHAPE_J = {
            {
                { J, 0, 0},
                { J, J, J},
                { 0, 0, 0},
            },
            {
                { 0, J, 0},
                { 0, J, 0},
                { J, J, 0},
            },
            {
                { 0, 0, 0},
                { J, J, J},
                { 0, 0, J},
            },
            {
                { 0, J, J},
                { 0, J, 0},
                { 0, J, 0},
            },
    };

    public static final int[][][] SHAPE_L = {
            {
                { 0, 0, L},
                { L, L, L},
                { 0, 0, 0},
            },
            {
                { L, L, 0},
                { 0, L, 0},
                { 0, L, 0},
            },
            {
                { 0, 0, 0},
                { L, L, L},
                { L, 0, 0},
            },
            {
                { 0, L, 0},
                { 0, L, 0},
                { 0, L, L},
            },
    };

    public static final int[][][] SHAPE_O = {
            {
                { O, O},
                { O, O},
            },
    };

    public static final int[][][] SHAPE_S = {
            {
                { 0, 0, 0},
                { 0, S, S},
                { S, S, 0},
            },
            {
                { 0, S, 0},
                { 0, S, S},
                { 0, 0, S},
            },
    };

    public static final int[][][] SHAPE_T = {
            {
                { 0, 0, 0},
                { T, T, T},
                { 0, T, 0},
            },
            {
                { 0, T, 0},
                { 0, T, T},
                { 0, T, 0},
            },
            {
                { 0, T, 0},
                { T, T, T},
                { 0, 0, 0},
            },
            {
                { 0, T, 0},
                { T, T, 0},
                { 0, T, 0},
            },
    };

    public static final int[][][] SHAPE_Z = {
            {
                { 0, 0, 0},
                { Z, Z, 0},
                { 0, Z, Z},
            },
            {
                { 0, 0, Z},
                { 0, Z, Z},
                { 0, Z, 0},
            },
            {
                { Z, Z, 0},
                { 0, Z, Z},
                { 0, 0, 0},
            },
            {
                { 0, Z, 0},
                { Z, Z, 0},
                { Z, 0, 0},
            }
    };

    public static final int[][][][] SHAPES = {
            SHAPE_I, SHAPE_J, SHAPE_L, SHAPE_O, SHAPE_S, SHAPE_T, SHAPE_Z
    };

    private int[][][] current;
    private int[][][] next;
    private int rotation = 0;

    private Vector2 pos = new Vector2();

    public Tetramino() {
        next = SHAPES[(int)(Math.random() * SHAPES.length)];

        next();
    }

    public void next() {
        current = next;

        next = SHAPES[(int)(Math.random() * SHAPES.length)];

        rotation = 0;
        // set position
        int y = 17;

        // if last line is empty, move up
        boolean empty = true;
        int l = current[0].length - 1;
        for(int x = 0; x< current[0][0].length; x++) {
            if(current[0][l][x] != 0) {
                empty = false;
                break;
            }
        }

        if(empty || current[0].length == 2) y++;

        pos.set(5, y);
    }

    public void moveLeft() {
        pos.x -= 1;
    }

    public void moveRight() {
        pos.x += 1;
    }

    public void moveDown() {
        pos.y -= 1;
    }

    public void rotateLeft() {
        rotation = (rotation + 1) % current.length;
    }

    public void rotateRight() {
        rotation = (rotation - 1) % current.length;
        if(rotation < 0) rotation = current.length - 1;
    }

    public boolean isFree(int x, int y) {
        return get(x, y) == 0;
    }

    public int getWidth() {
        return current[rotation][0].length;
    }

    public int getHeight() {
        return current[rotation].length;
    }

    public int get(int x, int y) {
        return current[rotation][y][x];
    }

    public Vector2 getPos() {
        return pos;
    }

    public void render(float delta, SpriteBatch batch, TextureAtlas.AtlasRegion tile, Vector2 org) {
        float width = tile.getRegionWidth();
        float height = tile.getRegionHeight();

        for(int y = 0; y< current[rotation].length ; y++) {
            for(int x = 0; x< current[rotation][y].length; x++) {
                if(!isFree(x, y)) {
                    batch.draw(tile, org.x + (pos.x + x) * width, org.y + (pos.y + y) * height);
                }
            }
        }

        // Render the next tetramino
        int leftAlign = next[0][0].length + 1;
        for(int y = 0; y< next[0].length ; y++) {
            for(int x = 0; x< next[0][y].length; x++) {
                if(next[0][y][x] != 0) {
                    batch.draw(tile, org.x + (x - leftAlign) * width, org.y + (y + 17) * height);
                }
            }
        }
    }
}
