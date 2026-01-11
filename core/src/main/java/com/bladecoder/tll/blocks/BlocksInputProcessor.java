package com.bladecoder.tll.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.IntSet;
import com.bladecoder.tll.util.DPIUtils;
import com.bladecoder.tll.util.EngineLogger;

/** Handles for keyboard and gamepad input. */
public class BlocksInputProcessor implements InputProcessor {

  private static final float DAS_INITIAL_DELAY = 0.26f;
  private static final float DAS_REPEAT_DELAY = 0.05f;
  private static final float TOUCH_SCREEN_MOVE_DIST = 0.15f;

  private static final float DROP_DELAY = 0.5f;

  private final BlocksLogic blocksGame;

  private float moveTime;

  private final IntSet pressedButtons = new IntSet();

  private boolean das;

  private int movedPointer;

  private int touchDownX;
  private int touchDownY;

  private boolean dragging;

  private float dropTime;

  // for touch screen, if true when touch up, drop the block
  private boolean drop;

  // soft drop set by axis
  private boolean axisSoftDrop;

  public BlocksInputProcessor(BlocksLogic blocksGame) {
    this.blocksGame = blocksGame;

    movedPointer = -1;
  }

  @Override
  public boolean keyDown(int i) {
    switch (i) {
      case Input.Keys.ESCAPE:
      case Input.Keys.BACK:
      case Input.Keys.MENU:
      case Input.Keys.P:
        blocksGame.pause();
        break;
      case Input.Keys.LEFT:
        moveTime = DAS_INITIAL_DELAY;
        das = blocksGame.moveLeft();
        break;
      case Input.Keys.RIGHT:
        moveTime = DAS_INITIAL_DELAY;
        das = blocksGame.moveRight();
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
    if (blocksGame.getState() == GameState.State.GAME_OVER
        || blocksGame.getState() == GameState.State.WIN
        || blocksGame.isPaused()) {
      return false;
    }

    if (i == Input.Keys.DOWN) {
      blocksGame.setSoftDrop(false);
    }

    return false;
  }

  @Override
  public boolean keyTyped(char c) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {

    if (!dragging) {
      touchDownX = screenX;
      touchDownY = screenY;
    }

    drop = false;

    return true;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    if (blocksGame.getState() == GameState.State.GAME_OVER
        || blocksGame.getState() == GameState.State.WIN
        || blocksGame.isPaused()) {
      return true;
    }

    if (movedPointer == pointer) {
      if (drop) {
        blocksGame.drop();
        drop = false;
      }

      blocksGame.setSoftDrop(false);

      movedPointer = -1;
      moveTime = 0;
      dragging = false;
      return true;
    }

    if (screenX < Gdx.graphics.getWidth() / 2) blocksGame.rotateLeft();
    else blocksGame.rotateRight();

    return true;
  }

  @Override
  public boolean touchCancelled(int i, int i1, int i2, int i3) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    dragging = true;

    System.out.println("touchDragged: " + screenX + ", " + screenY + ", " + pointer);

    if (DPIUtils.pixelsToInches(touchDownX - screenX) > TOUCH_SCREEN_MOVE_DIST) {
      if (blocksGame.isSoftDrop()) {
        blocksGame.setSoftDrop(false);
      }

      blocksGame.moveLeft();
      touchDownX = screenX;
      touchDownY = screenY;
      movedPointer = pointer;
    } else if (DPIUtils.pixelsToInches(touchDownX - screenX) < -TOUCH_SCREEN_MOVE_DIST) {
      if (blocksGame.isSoftDrop()) {
        blocksGame.setSoftDrop(false);
      }

      blocksGame.moveRight();
      touchDownX = screenX;
      touchDownY = screenY;
      movedPointer = pointer;
    } else if (DPIUtils.pixelsToInches(touchDownY - screenY) < -TOUCH_SCREEN_MOVE_DIST * 2) {
      blocksGame.setSoftDrop(true);
      touchDownX = screenX;
      touchDownY = screenY;
      movedPointer = pointer;
    } else if (DPIUtils.pixelsToInches(touchDownY - screenY) > TOUCH_SCREEN_MOVE_DIST * 2) {
      if (blocksGame.isSoftDrop()) {
        blocksGame.setSoftDrop(false);
      } else {
        drop = true;
      }

      touchDownX = screenX;
      touchDownY = screenY;
      movedPointer = pointer;
    }

    // To avoid the block to drop/softdrop in the next tetramino
    if (blocksGame.getState() != GameState.State.FALLING) {
      drop = false;
      blocksGame.setSoftDrop(false);
    }

    return true;
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

    if (dragging) {
      return;
    }

    updateButtons();
    updateAxis(delta);

    // if the block could not move, we don't wait for the DAS to allow the kick of the block
    // while the button is pressed. If not, the block will be locked while waiting for the DAS.
    if (das && moveTime > 0) return;

    if (!das) moveTime = DAS_INITIAL_DELAY;
    else moveTime = DAS_REPEAT_DELAY;

    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
      blocksGame.moveLeft();
    } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
      blocksGame.moveRight();
    }

    updateDPad();
  }

  private void updateAxis(float delta) {
    for (Controller controller : Controllers.getControllers()) {

      // handle drop
      dropTime -= delta;

      float axisYValue = controller.getAxis(controller.getMapping().axisLeftY);

      if (axisSoftDrop && axisYValue < 0.5) {
        blocksGame.setSoftDrop(false);
        axisSoftDrop = false;
      }

      if (dropTime <= 0 && axisYValue > 0.6) {
        blocksGame.setSoftDrop(true);
        axisSoftDrop = true;
        dropTime = DROP_DELAY;
      } else if (dropTime <= 0 && axisYValue < -0.6f) {
        blocksGame.drop();
        dropTime = DROP_DELAY;
      }

      if (das && moveTime > 0) return;

      float axisXValue = controller.getAxis(controller.getMapping().axisLeftX);

      if (axisXValue < -0.2f && axisXValue >= -0.7f) {
        moveTime = DAS_INITIAL_DELAY;
        das = blocksGame.moveLeft();
      } else if (axisXValue > 0.2f && axisXValue <= 0.7) {
        moveTime = DAS_INITIAL_DELAY;
        das = blocksGame.moveRight();
      } else if (axisXValue < -0.7f) {
        moveTime = DAS_REPEAT_DELAY;
        das = blocksGame.moveLeft();
      } else if (axisXValue > 0.7f) {
        moveTime = DAS_REPEAT_DELAY;
        das = blocksGame.moveRight();
      }
    }
  }

  private void updateButtons() {

    for (Controller controller : Controllers.getControllers()) {

      for (int buttonCode = controller.getMinButtonIndex();
          buttonCode <= controller.getMaxButtonIndex();
          buttonCode++) {
        boolean p = controller.getButton(buttonCode);

        if (p) {
          if (!pressedButtons.contains(buttonCode)) {
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
    } else if (buttonCode == controller.getMapping().buttonDpadRight) {
      moveTime = DAS_INITIAL_DELAY;
      das = blocksGame.moveRight();
    } else if (buttonCode == controller.getMapping().buttonDpadLeft) {
      moveTime = DAS_INITIAL_DELAY;
      das = blocksGame.moveLeft();
    } else if (buttonCode == controller.getMapping().buttonDpadDown) {
      blocksGame.setSoftDrop(true);
    }
  }

  private void updateDPad() {
    for (Controller controller : Controllers.getControllers()) {
      if (controller.getButton(controller.getMapping().buttonDpadRight)) {
        das = blocksGame.moveRight();
      } else if (controller.getButton(controller.getMapping().buttonDpadLeft)) {
        das = blocksGame.moveLeft();
      }
    }
  }
}
