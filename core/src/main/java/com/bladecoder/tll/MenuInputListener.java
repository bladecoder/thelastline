package com.bladecoder.tll;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.IntSet;
import com.bladecoder.tll.util.EngineLogger;

public class MenuInputListener extends InputListener  {
    private final TLLGame game;

    private final IntSet pressedButtons = new IntSet();

    private final ButtonGroup<Button> menu;

    private final static float AXIS_DELAY = 0.4f;
    private float axisTime;

    public MenuInputListener(TLLGame game, ButtonGroup<Button> menu) {
        this.game = game;
        this.menu = menu;

        selectButton(0);
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {

        switch (keycode) {
            case Input.Keys.ESCAPE:
            case Input.Keys.BACK:
            case Input.Keys.MENU:
                if(!game.isPaused())
                    Gdx.app.exit();
                else
                    game.setBlocksScreen();
                return true;
            case  Input.Keys.UP:
                up();
                return true;
            case Input.Keys.DOWN:
                down();
                return true;
            case Input.Keys.ENTER:
            case Input.Keys.RIGHT:
                click(false);
                return true;
            case Input.Keys.SPACE:
            case Input.Keys.LEFT:
                click(true);
                return true;
        }

        return false;
    }

    public void update(float delta) {
        updateAxis(delta);
        updateButtons();
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
            game.setBlocksScreen();
        } else if (buttonCode == controller.getMapping().buttonA || buttonCode == controller.getMapping().buttonDpadRight) {
            click(false);
        } else if (buttonCode == controller.getMapping().buttonB || buttonCode == controller.getMapping().buttonDpadLeft) {
            click(true);
        } else if (buttonCode == controller.getMapping().buttonDpadUp) {
            up();
        } else if (buttonCode == controller.getMapping().buttonDpadDown) {
            down();
        }
    }

    private void buttonDown(Controller controller, int buttonCode) {
        EngineLogger.debug(buttonCode + " gamepad button down.");
    }

    private void updateAxis(float delta) {
        axisTime -= delta;

        if(axisTime > 0)
            return;

        for (Controller controller : Controllers.getControllers()) {
            // move up and down
            if (controller.getAxis(controller.getMapping().axisLeftY) < -0.5f) {
                axisTime = AXIS_DELAY;
                up();
            } else if (controller.getAxis(controller.getMapping().axisLeftY) > 0.5f) {
                axisTime = AXIS_DELAY;
                down();
            } else if (controller.getAxis(controller.getMapping().axisLeftX) < -0.5f) {
                axisTime = AXIS_DELAY;
                click(true);
            } else if (controller.getAxis(controller.getMapping().axisLeftX) > 0.5f) {
                axisTime = AXIS_DELAY;
                click(false);
            }
        }
    }

    private Button getButton(int index) {
        return menu.getButtons().get(index);
    }

    private void up() {
        if(menu.getCheckedIndex() == 0) return;
        selectButton(menu.getCheckedIndex() - 1);
    }

    private void down() {
        if(menu.getCheckedIndex() == getNumButtons() - 1) return;
        selectButton(menu.getCheckedIndex() + 1);
    }

    private void selectButton(int buttonIndex) {
        getButton(buttonIndex).setChecked(true);
    }

    private int getNumButtons() {
        return menu.getButtons().size;
    }

    private void click(boolean secondaryButton) {
       Button b = menu.getChecked();

       for(EventListener l: b.getListeners()) {
           if(l instanceof ClickListener) {
               InputEvent event = new InputEvent();
               event.setButton(secondaryButton ? Input.Buttons.RIGHT : Input.Buttons.LEFT);

               ((ClickListener)l).clicked(event, 0, 0);
           }
       }
    }
}
