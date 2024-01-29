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
import com.bladecoder.tll.blocks.Theme;
import com.bladecoder.tll.util.Config;
import com.bladecoder.tll.util.DPIUtils;

public class MainMenuScreen implements Screen {

    private static final String[] THEMES = {"DEFAULT", "MONO", "SUNSET"};

    private static final String[] GAME_MODES = {"MARATHON", "SPRINT", "ULTRA"};

    private Stage stage;
    private final Table menuButtonTable = new Table();

    private MainMenuInputListener inputListener;

    private final TLLGame game;

    private Theme theme;

    private ButtonGroup<Button> menuGroup;

    private Label title;

    private Label version;

    public MainMenuScreen(TLLGame game) {
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

        title = new Label("THE LAST LINE", skin,
                "title");

        title.setAlignment(Align.center);

        menuButtonTable.add(title).padBottom(DPIUtils.getMarginSize());
        menuButtonTable.row();

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


        Table levelTable = new Table();
        levelTable.defaults().pad(DPIUtils.getSpacing()).align(Align.center);
        TextButton level = new TextButton("Level " + Config.getInstance().getPref("startLevel", 1), skin, "menu");

        TextButton levelDown = new TextButton(" <", skin, "menu");
        levelDown.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int startLevel = Config.getInstance().getPref("startLevel", 1);

                if(startLevel > 1)
                    startLevel-=1;
                else startLevel = BlocksLogic.MAX_LEVEL;

                level.setText("Level " + startLevel);
                Config.getInstance().setPref("startLevel", startLevel);
                Config.getInstance().savePrefs();
            }
        });
        levelTable.add(levelDown);

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
        levelTable.add(level);

        TextButton levelUp = new TextButton("> ", skin, "menu");
        levelUp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int startLevel = Config.getInstance().getPref("startLevel", 1);

                if (startLevel < BlocksLogic.MAX_LEVEL)
                    startLevel += 1;
                else startLevel = 1;

                level.setText("Level " + startLevel);
                Config.getInstance().setPref("startLevel", startLevel);
                Config.getInstance().savePrefs();
            }
        });
        levelTable.add(levelUp);

        menuButtonTable.add(levelTable);
        menuButtonTable.row();

        TextButton themeButton = new TextButton("Theme " + THEMES[Config.getInstance().getPref("theme", 0)], skin, "menu");
        themeButton.getLabel().setAlignment(Align.center);
        themeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int themeIdx = Config.getInstance().getPref("theme", 0);

                if(event.getButton() == Input.Buttons.RIGHT) {
                    if(themeIdx > 0)
                        themeIdx-=1;
                } else {
                    themeIdx = (themeIdx + 1) % THEMES.length;
                }

                themeButton.setText("Theme " + THEMES[themeIdx]);
                Config.getInstance().setPref("theme", themeIdx);
                Config.getInstance().savePrefs();

                updateTheme();
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

        menuGroup = new ButtonGroup<>( newGame, gameModeButton, level, themeButton, quit);
        menuGroup.setMinCheckCount(1);

        inputListener = new MainMenuInputListener(game, menuGroup);
        menuButtonTable.addListener(inputListener);

        version = new Label("v" + Config.getInstance().getProperty("version", "0") + " by Rafael García", skin, "default");
        version.setPosition(DPIUtils.getSpacing(),DPIUtils.getSpacing());
        stage.addActor(version);

        updateTheme();
    }

    private void updateTheme() {
        theme = Theme.THEMES[Config.getInstance().getPref("theme", 0)];

        TextButton.TextButtonStyle style = (TextButton.TextButtonStyle) menuGroup.getButtons().get(0).getStyle();

        style.checkedFontColor = theme.scoresTextColor;
        style.checkedFocusedFontColor = theme.scoresTextColor;
        style.fontColor = theme.scoresTextColor.cpy().mul(0.6f);

        title.getStyle().fontColor = theme.scoresTextColor;
        version.getStyle().fontColor = theme.scoresTextColor;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(theme.bgColor.r, theme.bgColor.g, theme.bgColor.b, theme.bgColor.a);
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
