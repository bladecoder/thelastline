package com.bladecoder.tll;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.bladecoder.tll.ui.MenuInputListener;

public class MainMenuInputListener extends MenuInputListener {
    private final TLLGame game;

    public MainMenuInputListener(TLLGame game, ButtonGroup<Button> menu) {
        super(menu);
        this.game = game;
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {

        switch (keycode) {
            case Input.Keys.ESCAPE:
            case Input.Keys.BACK:
            case Input.Keys.MENU:
                Gdx.app.exit();
                return true;
        }

        return super.keyDown(event, keycode);
    }

    protected void buttonUp(Controller controller, int buttonCode) {
        super.buttonUp(controller, buttonCode);

        if (buttonCode == controller.getMapping().buttonStart) {
            game.setBlocksScreen();
        }
    }
}
