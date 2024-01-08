package com.bladecoder.tll;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bladecoder.tll.util.DPIUtils;

public class MenuScreen implements Screen {

    private Stage stage;
    private final Table menuButtonTable = new Table();

    private MenuInputListener inputListener;

    private final TLLGame game;

    private int startLevel = 1;

    public MenuScreen(TLLGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        final Skin skin = game.getSkin();

        menuButtonTable.clear();

        menuButtonTable.setFillParent(true);

        menuButtonTable.align(Align.center);
        menuButtonTable.pad(DPIUtils.getMarginSize() * 2);
        final BitmapFont f = skin.get("menu", TextButton.TextButtonStyle.class).font;
        float buttonWidth = f.getCapHeight() * 15f;
        menuButtonTable.defaults().pad(DPIUtils.getSpacing()).width(buttonWidth).align(Align.center);

        Label title = new Label("The Last Line", skin,
                "title");

        title.setAlignment(Align.center);

        menuButtonTable.add(title).padBottom(DPIUtils.getMarginSize() * 2);
        menuButtonTable.row();

        TextButton newGame = new TextButton("New Game", skin, "menu");
        newGame.getLabel().setAlignment(Align.center);
        newGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setBlocksScreen(startLevel);
            }
        });

        menuButtonTable.add(newGame);
        menuButtonTable.row();

        TextButton level = new TextButton("Level " + startLevel, skin, "menu");
        level.getLabel().setAlignment(Align.center);
        level.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startLevel+=1;
                level.setText("Level " + startLevel);
            }
        });

        menuButtonTable.add(level);
        menuButtonTable.row();

        TextButton quit = new TextButton("Exit", skin, "menu");
        quit.getLabel().setAlignment(Align.center);
        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        menuButtonTable.add(quit);
        menuButtonTable.row();

        menuButtonTable.pack();
        stage.addActor(menuButtonTable);
        stage.setKeyboardFocus(menuButtonTable);
        inputListener = new MenuInputListener(game, menuButtonTable);
        menuButtonTable.addListener(inputListener);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
        inputListener.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }
}
