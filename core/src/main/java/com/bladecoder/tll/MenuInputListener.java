/*******************************************************************************
 * Copyright 2014 Rafael Garcia Moreno.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.bladecoder.tll;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.Pools;
import com.bladecoder.tll.util.EngineLogger;

public class MenuInputListener extends InputListener  {
    public static final float THUMBSTICKVELOCITY = 12f * 60f;

    private final TLLGame game;

    private final IntSet pressedButtons = new IntSet();

    private int currentButtonIndex = -1;

    private final Table menu;

    public MenuInputListener(TLLGame game, Table menu) {
        this.game = game;
        this.menu = menu;

        // set setProgrammaticChangeEvents to all buttons
        for(Actor a : menu.getChildren()) {
            if(a instanceof Button) {
                ((Button)a).setProgrammaticChangeEvents(true);
            }
        }
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if(keycode == Input.Keys.UP) {
            up();
            return true;
        } else if(keycode == Input.Keys.DOWN) {
            down();
            return true;
        } else if(keycode == Input.Keys.ENTER) {
            click();
            return true;
        }  else if (keycode == Input.Keys.ESCAPE) {
            game.setBlocksScreen(1);
            return true;
        } else if (keycode == Input.Keys.BACK) {
            Gdx.app.exit();
            return true;
        }

        return false;
    }

    public void update(float delta) {
        if(currentButtonIndex == -1) {
            currentButtonIndex = 0;
            //selectButton(getButton(currentButtonIndex));
        }

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
            game.setMenuScreen();
        } else if (buttonCode == controller.getMapping().buttonA) {
            releaseButton(getButton(currentButtonIndex));
        } else if (buttonCode == controller.getMapping().buttonStart) {
            game.setBlocksScreen(1);
        } else if (buttonCode == controller.getMapping().buttonDpadUp) {
            up();
        } else if (buttonCode == controller.getMapping().buttonDpadDown) {
            down();
        }
    }

    private void click() {
        clickButton(getButton(currentButtonIndex));
        releaseButton(getButton(currentButtonIndex));
    }

    private void buttonDown(Controller controller, int buttonCode) {
        EngineLogger.debug(buttonCode + " gamepad button down.");
        if (buttonCode == controller.getMapping().buttonA) {
            clickButton(getButton(currentButtonIndex));
        }
    }

    /**
     * Simulate mousing over a button.
     */
    private void selectButton(Actor button) {
        InputEvent event = Pools.obtain(InputEvent.class);
        event.setType(InputEvent.Type.enter);

        button.fire(event);
        event.isHandled();
        Pools.free(event);
    }

    /**
     * Simulate mousing off of a button.
     */
    private void unselectButton(Actor button) {
        InputEvent event = Pools.obtain(InputEvent.class);
        event.setType(InputEvent.Type.exit);

        button.fire(event);
        event.isHandled();
        Pools.free(event);
    }

    /**
     * Simulate button click down.
     */
    private void clickButton(Actor button) {
        InputEvent event = Pools.obtain(InputEvent.class);
        event.setType(InputEvent.Type.touchDown);
        event.setButton(Input.Buttons.LEFT);

        button.fire(event);
        event.isHandled();
        Pools.free(event);
    }

    private Actor getButton(int index) {
        for(Actor a : menu.getChildren()) {
            if(a instanceof Button) {
                if(index == 0)
                    return a;

                index--;
            }
        }

        return null;
    }

    private void updateAxis(float delta) {
//        for (Controller controller : Controllers.getControllers()) {
//            vx += controller.getAxis(controller.getMapping().axisLeftX) * v;
//            vy += controller.getAxis(controller.getMapping().axisLeftY) * v;
//            vx += controller.getAxis(controller.getMapping().axisRightX) * v / 2f;
//            vy += controller.getAxis(controller.getMapping().axisRightY) * v / 2f;
//        }
    }

    private void up() {
        if(currentButtonIndex == 0) return;
        selectButton(currentButtonIndex - 1);
    }

    private void down() {
        if(currentButtonIndex == getNumButtons() - 1) return;
        selectButton(currentButtonIndex + 1);
    }

    private void selectButton(int buttonIndex) {
        unselectButton(getButton(currentButtonIndex));
        currentButtonIndex = buttonIndex;
        selectButton(getButton(currentButtonIndex));
    }

    private int getNumButtons() {
        int numButtons = 0;

        for(Actor a : menu.getChildren()) {
            if(a instanceof Button)
                numButtons++;
        }

        return numButtons;
    }

    /**
     * Simulate button click release.
     */
    private void releaseButton(Actor button) {
        InputEvent event = Pools.obtain(InputEvent.class);
        event.setType(InputEvent.Type.touchUp);
        event.setButton(Input.Buttons.LEFT);

        button.fire(event);
        event.isHandled();
        Pools.free(event);
    }
}
