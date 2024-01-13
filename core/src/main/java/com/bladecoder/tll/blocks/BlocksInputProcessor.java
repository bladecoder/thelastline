package com.bladecoder.tll.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.IntSet;
import com.bladecoder.tll.TLLGame;
import com.bladecoder.tll.util.EngineLogger;

/**
 * Handles for keyboard and gamepad input.
 */
public class BlocksInputProcessor implements InputProcessor {

    private static final float DAS_INITIAL_DELAY = 0.26f;
    private static final float DAS_REPEAT_DELAY = 0.05f;

    private final BlocksLogic blocksGame;

    private float moveTime;

    private final IntSet pressedButtons = new IntSet();

    private final TLLGame game;

    public BlocksInputProcessor(TLLGame game, BlocksLogic blocksGame) {
        this.blocksGame = blocksGame;
        this.game = game;
    }

    @Override
    public boolean keyDown(int i) {

        if(i ==Input.Keys.ESCAPE) {
            blocksGame.pause();
            game.setMenuScreen();
            return true;
        }

        switch (i) {
            case Input.Keys.LEFT:
                moveTime = DAS_INITIAL_DELAY;
                blocksGame.moveLeft();
                break;
            case Input.Keys.RIGHT:
                moveTime = DAS_INITIAL_DELAY;
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
        if(blocksGame.getState() == GameState.State.GAME_OVER || blocksGame.getState() == GameState.State.WIN) {
            blocksGame.newGame();
            return false;
        }

        if(i == Input.Keys.DOWN) {
            blocksGame.setSoftDrop(false);
        } else if(i == Input.Keys.P) {
            if(blocksGame.isPaused())
                blocksGame.resume();
            else
                blocksGame.pause();
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

        moveTime -= delta;

        updateButtons();

        if(moveTime > 0)
            return;

        moveTime = DAS_REPEAT_DELAY;

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
                    if(!pressedButtons.contains(buttonCode)) {
                        pressedButtons.add(buttonCode);
                        buttonDown(controller, buttonCode);
                    }
                } else if (pressedButtons.contains(buttonCode)) {
                    pressedButtons.remove(buttonCode);
                    buttonUp(controller, buttonCode);
                }
            }
        }
    }

    private void buttonUp(Controller controller, int buttonCode) {
        EngineLogger.debug(buttonCode + " gamepad button up.");

        if (buttonCode == controller.getMapping().buttonStart) {
            blocksGame.pause();
            game.setMenuScreen();

            return;
        }

        if(blocksGame.getState() == GameState.State.GAME_OVER || blocksGame.getState() == GameState.State.WIN) {
            blocksGame.newGame();
            return;
        }

        if (buttonCode == controller.getMapping().buttonDpadUp) {
            blocksGame.drop();
        } else if (buttonCode == controller.getMapping().buttonR1
                || buttonCode == controller.getMapping().buttonR2
                || buttonCode == controller.getMapping().buttonL1) {
            blocksGame.drop();
        } else if (buttonCode == controller.getMapping().buttonDpadDown) {
            blocksGame.setSoftDrop(false);
        }
    }

    private void buttonDown(Controller controller, int buttonCode) {
        EngineLogger.debug(buttonCode + " gamepad button down.");

        if (buttonCode == controller.getMapping().buttonA) {
            blocksGame.rotateRight();
        } else if (buttonCode == controller.getMapping().buttonB) {
            blocksGame.rotateLeft();
        }else if (buttonCode == controller.getMapping().buttonDpadRight) {
            moveTime = DAS_INITIAL_DELAY;
            blocksGame.moveRight();
        } else if (buttonCode == controller.getMapping().buttonDpadLeft) {
            moveTime = DAS_INITIAL_DELAY;
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
