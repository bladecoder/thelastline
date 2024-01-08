package com.bladecoder.tll.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

import com.bladecoder.tll.util.EngineLogger;

public class BlocksInputProcessor implements InputProcessor {

    private final BlocksGame blocksGame;

    private float moveTime = 0;

    private int pressed = -1;

    public BlocksInputProcessor(BlocksGame blocksGame) {
        this.blocksGame = blocksGame;
    }

    @Override
    public boolean keyDown(int i) {

        if(i ==Input.Keys.ESCAPE)
            Gdx.app.exit();

        if(blocksGame.isGameOver() || blocksGame.hasWin()) {
            blocksGame.newGame();
            return false;
        }

        moveTime = 0;

        switch (i) {
            case Input.Keys.LEFT:
                blocksGame.moveLeft();
                break;
            case Input.Keys.RIGHT:
                blocksGame.moveRight();
                break;
            case Input.Keys.UP:
            case Input.Keys.X:
                blocksGame.rotateRight();
                break;
            case Input.Keys.Z:
                blocksGame.rotateLeft();
                break;
            case Input.Keys.DOWN:
                blocksGame.setSoftDrop(true);
                break;
            case Input.Keys.SPACE:
                blocksGame.drop();
                break;

            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean keyUp(int i) {
        if(i == Input.Keys.DOWN) {
            blocksGame.setSoftDrop(false);
        }

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

        updateButtons();

        if(moveTime < 0.15f)
            return;

        moveTime = 0;

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            blocksGame.moveLeft();
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            blocksGame.moveRight();
        }

        updateDPad();
    }

    private void updateButtons() {

        for (Controller controller : Controllers.getControllers()) {

            for (int buttonCode = controller.getMinButtonIndex(); buttonCode <= controller
                    .getMaxButtonIndex(); buttonCode++) {
                boolean p = controller.getButton(buttonCode);

                if (p) {
                    if (pressed != -1 && buttonCode != pressed) {
                        buttonUp(controller, pressed);
                        pressed = -1;
                    }

                    if(pressed == -1) {
                        pressed = buttonCode;
                        buttonDown(controller, pressed);
                    }
                } else if (buttonCode == pressed) {
                    buttonUp(controller, pressed);
                    pressed = -1;
                }
            }
        }
    }

    private void buttonUp(Controller controller, int buttonCode) {
        EngineLogger.debug(buttonCode + " gamepad button up.");

        if (buttonCode == controller.getMapping().buttonStart) {
            Gdx.app.exit();

            return;
        }

        if (buttonCode == controller.getMapping().buttonA) {
            blocksGame.rotateRight();
        } else if (buttonCode == controller.getMapping().buttonB) {
            blocksGame.rotateLeft();
        } else if (buttonCode == controller.getMapping().buttonDpadUp) {
            blocksGame.drop();
        } else if (buttonCode == controller.getMapping().buttonR1
            || buttonCode == controller.getMapping().buttonR2) {
            blocksGame.drop();
        } else if (buttonCode == controller.getMapping().buttonDpadDown) {
            blocksGame.setSoftDrop(false);
        }
    }

    private void buttonDown(Controller controller, int buttonCode) {
        EngineLogger.debug(buttonCode + " gamepad button down.");

        moveTime = 0;

        if(blocksGame.isGameOver() || blocksGame.hasWin()) {
            blocksGame.newGame();
            return;
        }

        if (buttonCode == controller.getMapping().buttonDpadRight) {
            blocksGame.moveRight();
        } else if (buttonCode == controller.getMapping().buttonDpadLeft) {
            blocksGame.moveLeft();
        } else if (buttonCode == controller.getMapping().buttonDpadDown) {
            blocksGame.setSoftDrop(true);
        }
    }

    private void updateDPad() {
        for (Controller controller : Controllers.getControllers()) {
            if (controller.getButton(controller.getMapping().buttonDpadRight)) {
                blocksGame.moveRight();
            } else if (controller.getButton(controller.getMapping().buttonDpadLeft)) {
                blocksGame.moveLeft();
            }
        }
    }
}
