package com.bladecoder.tll.blocks;

class PlayField {

    private final int[][] field;

    private final int width;
    private final int height;

    PlayField(int width, int height) {
        this.field = new int[width][height];

        this.width = width;
        this.height = height;
    }

    void clear() {
        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                field[x][y] = 0;
            }
        }
    }

    boolean isFree(int x, int y) {
        if(x < 0 || x >= width || y < 0 || y >= height)
            return false;

        return field[x][y] == 0;
    }

    void set(int x, int y, int value) {
        field[x][y] = value;
    }

    int get(int x, int y) {
        return field[x][y];
    }

    void removeRow(int row) {
        for(int y=row; y<height-1; y++) {
            for(int x=0; x<width; x++) {
                field[x][y] = field[x][y+1];
            }
        }
    }

    boolean isRowFull(int row) {
        for(int x=0; x<width; x++) {
            if(field[x][row] == 0)
                return false;
        }

        return true;
    }

    int getHeight() {
        return height;
    }

    int getWidth() {
        return width;
    }

    void nextTetramino(Tetramino tetramino) {
        // copy tetramino to field
        for(int y=0; y< tetramino.getHeight(); y++) {
            for(int x=0; x< tetramino.getWidth(); x++) {
                int v = tetramino.get(x, y);
                if(v != 0) {
                    set((int)tetramino.getPos().x + x, (int)tetramino.getPos().y + y, v);
                }
            }
        }

        tetramino.next();
    }

    boolean canMoveDown(Tetramino tetramino) {
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
}
