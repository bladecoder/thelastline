package com.bladecoder.tll;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class TLLInputProcessor implements InputProcessor {

    private PlayField playField;

    private float moveTime = 0;

    public TLLInputProcessor(PlayField playField) {
        this.playField = playField;
    }

    @Override
    public boolean keyDown(int i) {
        moveTime = 0;

        switch (i) {
            case Input.Keys.LEFT:
                playField.moveLeft();
                break;
            case Input.Keys.RIGHT:
                playField.moveRight();
                break;
            case Input.Keys.UP:
                playField.rotateRight();
                break;
            case Input.Keys.Z:
                playField.rotateLeft();
                break;
            case Input.Keys.X:
                playField.rotateRight();
                break;
            case Input.Keys.DOWN:
                playField.moveDown();
                break;
            case Input.Keys.SPACE:
                playField.drop();
                break;
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                break;

            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }

    // move left/right while keys pressed and time passed
    public void update(float delta) {
        moveTime += delta;

        if(moveTime < 0.15f)
            return;

        moveTime = 0;

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playField.moveLeft();
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playField.moveRight();
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playField.moveDown();
        }
    }
}
