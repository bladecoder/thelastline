package com.bladecoder.tll;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bladecoder.tll.blocks.BlocksLogic;
import com.bladecoder.tll.util.Config;
import com.bladecoder.tll.util.DPIUtils;

public class MenuScreen implements Screen {

    private static final String[] THEMES = {"DEFAULT", "SUNSET"};

    private static final String[] GAME_MODES = {"MARATHON", "SPRINT", "ULTRA"};

    private Stage stage;
    private final Table menuButtonTable = new Table();

    private MenuInputListener inputListener;

    private final TLLGame game;

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

        TextButton resumeGame = new TextButton("Resume", skin, "menu");

        if(game.isPaused()) {
            resumeGame.getLabel().setAlignment(Align.center);
            resumeGame.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setBlocksScreen();
                }
            });

            menuButtonTable.add(resumeGame);
            menuButtonTable.row();
        }

        TextButton newGame = new TextButton("New Game", skin, "menu");
        newGame.getLabel().setAlignment(Align.center);
        newGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setPaused(false);
                game.setBlocksScreen();
            }
        });

        menuButtonTable.add(newGame);
        menuButtonTable.row();

        TextButton gameModeButton = new TextButton("Mode  " + GAME_MODES[Config.getInstance().getPref("gameMode", 0)], skin, "menu");
        gameModeButton.getLabel().setAlignment(Align.center);
        gameModeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int gameMode = Config.getInstance().getPref("gameMode", 0);

                if(event.getButton() == Input.Buttons.RIGHT) {
                    if(gameMode > 0)
                        gameMode-=1;
                } else {
                    gameMode = (gameMode + 1) % GAME_MODES.length;
                }

                gameModeButton.setText("Mode  " + GAME_MODES[gameMode]);
                Config.getInstance().setPref("gameMode", gameMode);
                Config.getInstance().savePrefs();
            }
        });

        menuButtonTable.add(gameModeButton);
        menuButtonTable.row();

        TextButton level = new TextButton("Level " + Config.getInstance().getPref("startLevel", 1), skin, "menu");
        level.getLabel().setAlignment(Align.center);
        level.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int startLevel = Config.getInstance().getPref("startLevel", 1);

                if(event.getButton() == Input.Buttons.RIGHT) {
                    if(startLevel > 1)
                        startLevel-=1;
                    else startLevel = BlocksLogic.MAX_LEVEL;
                } else {
                    if (startLevel < BlocksLogic.MAX_LEVEL)
                        startLevel += 1;
                    else startLevel = 1;
                }

                level.setText("Level " + startLevel);
                Config.getInstance().setPref("startLevel", startLevel);
                Config.getInstance().savePrefs();
            }
        });

        menuButtonTable.add(level);
        menuButtonTable.row();

        TextButton themeButton = new TextButton("Theme " + THEMES[Config.getInstance().getPref("theme", 0)], skin, "menu");
        themeButton.getLabel().setAlignment(Align.center);
        themeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int theme = Config.getInstance().getPref("theme", 0);

                if(event.getButton() == Input.Buttons.RIGHT) {
                    if(theme > 0)
                        theme-=1;
                } else {
                    theme = (theme + 1) % THEMES.length;
                }

                themeButton.setText("Theme " + THEMES[theme]);
                Config.getInstance().setPref("theme", theme);
                Config.getInstance().savePrefs();
            }
        });

        menuButtonTable.add(themeButton);
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

        ButtonGroup<Button> menuGroup = game.isPaused() ? new ButtonGroup<>( resumeGame, newGame, gameModeButton, level, themeButton, quit) :
                new ButtonGroup<>( newGame, gameModeButton, level, themeButton, quit);
        menuGroup.setMinCheckCount(1);

        inputListener = new MenuInputListener(game, menuGroup);
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
        stage.dispose();
    }

    @Override
    public void dispose() {

    }
}
