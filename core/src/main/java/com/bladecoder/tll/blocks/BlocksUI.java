package com.bladecoder.tll.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bladecoder.tll.TLLGame;
import com.bladecoder.tll.ui.BladeSkin;
import com.bladecoder.tll.ui.MenuInputListener;
import com.bladecoder.tll.ui.RectangleDrawable;
import com.bladecoder.tll.util.DPIUtils;

public class BlocksUI {
  private final Stage stage;
  private final TLLGame game;

  private final BlocksLogic blocksLogic;

  private final Button pauseButton;

  private Table pauseTable;

  private final Skin skin;

  private final Theme theme;

  private MenuInputListener inputListener;

  public BlocksUI(
      BladeSkin skin,
      TLLGame game,
      BlocksLogic blocksLogic,
      Theme theme,
      Viewport viewport,
      SpriteBatch batch) {
    this.stage = new Stage(viewport, batch);
    this.blocksLogic = blocksLogic;
    this.game = game;
    this.skin = skin;
    this.theme = theme;

    // PAUSE BUTTON
    Color buttonColor = theme.scoresTextColor.cpy();
    buttonColor.a = 0.35f;

    Drawable pauseDrawable = ((SpriteDrawable) skin.getDrawable("pause")).tint(buttonColor);
    Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle();
    pauseButtonStyle.up = pauseDrawable;
    pauseButtonStyle.down = pauseDrawable;
    pauseButtonStyle.over = pauseDrawable;

    pauseButton = new Button(pauseButtonStyle);
    pauseButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
            blocksLogic.pause();
            showMenu();
          }
        });

    pauseButton.setSize(DPIUtils.getPrefButtonSize(), DPIUtils.getPrefButtonSize());

    if (Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen))
      stage.addActor(pauseButton);
  }

  private Table createMenu(String titleText, boolean addResume, ButtonGroup<Button> group) {
    Table table = new Table();

    table.pad(DPIUtils.getMarginSize() * 2);
    table.align(Align.center);

    Color bgColor = theme.scoresBgColor;

    if (bgColor == null) {
      bgColor = theme.bgColor;
    }

    Drawable tableBg =
        new RectangleDrawable(bgColor, (int) theme.scoresBorderWidth, theme.scoresBorderColor);
    table.setBackground(tableBg);

    Label title = new Label(titleText, skin, "title");

    table.add(title).pad(DPIUtils.getMarginSize() * 2);
    table.row();

    if (addResume) {
      TextButton resumeGame = new TextButton("Resume", skin, "menu");

      resumeGame.getLabel().setAlignment(Align.center);
      resumeGame.addListener(
          new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
              hideMenu();
              blocksLogic.resume();
            }
          });

      group.add(resumeGame);
      table.add(resumeGame);
      table.row();
    }

    TextButton newGame = new TextButton("New Game", skin, "menu");
    newGame.getLabel().setAlignment(Align.center);
    newGame.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            blocksLogic.newGame();
            hideMenu();
          }
        });

    group.add(newGame);
    table.add(newGame);
    table.row();

    TextButton mainMenu = new TextButton("Main Menu", skin, "menu");
    mainMenu.getLabel().setAlignment(Align.center);
    mainMenu.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            game.setMenuScreen();
          }
        });

    group.add(mainMenu);
    table.add(mainMenu);
    table.row();

    return table;
  }

  private void showMenu() {

    String titleText = "PAUSED";
    boolean addResume = true;

    if (blocksLogic.getState() == GameState.State.WIN) {
      titleText = "YOU WIN!";
      addResume = false;
    } else if (blocksLogic.getState() == GameState.State.GAME_OVER) {
      titleText = "GAME OVER";
      addResume = false;
    }

    // PAUSE WINDOW
    ButtonGroup<Button> group = new ButtonGroup<>();
    group.setMinCheckCount(1);
    pauseTable = createMenu(titleText, addResume, group);

    inputListener = new MenuInputListener(group);
    pauseTable.addListener(inputListener);

    stage.addActor(pauseTable);

    pauseButton.setVisible(false);
    pauseTable.pack();

    pauseTable.setPosition(
        (stage.getWidth() - pauseTable.getWidth()) / 2,
        (stage.getHeight() - pauseTable.getHeight()) / 2);
    stage.setKeyboardFocus(pauseTable);
  }

  public void hideMenu() {
    if (pauseTable == null) return;

    stage.getRoot().removeActor(pauseTable);
    pauseTable = null;
    pauseButton.setVisible(true);
    stage.setKeyboardFocus(null);
  }

  public void render(float delta) {

    if (pauseTable == null
        && (blocksLogic.isPaused()
            || blocksLogic.getState() == GameState.State.WIN
            || blocksLogic.getState() == GameState.State.GAME_OVER)) {
      showMenu();
    }

    if (pauseTable != null) {
      inputListener.update(delta);
    }

    stage.act(delta);
    stage.draw();
  }

  public void resize(int width, int height) {
    pauseButton.setPosition(
        width - pauseButton.getWidth() - DPIUtils.getMarginSize() / 2,
        height - pauseButton.getHeight() - DPIUtils.getMarginSize() / 2);

    if (pauseTable != null) {
      pauseTable.setPosition(
          (stage.getWidth() - pauseTable.getWidth()) / 2,
          (stage.getHeight() - pauseTable.getHeight()) / 2);
    }
  }

  public InputProcessor getStage() {
    return stage;
  }
}
