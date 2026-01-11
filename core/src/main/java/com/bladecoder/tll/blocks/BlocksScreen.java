package com.bladecoder.tll.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bladecoder.tll.TLLGame;
import com.bladecoder.tll.util.Config;

public class BlocksScreen implements Screen {

  private final TLLGame game;
  private final Viewport viewport = new ScreenViewport();
  private final SpriteBatch batch = new SpriteBatch();

  private final SoundManager soundManager = new SoundManager();
  private Theme theme = Theme.DEFAULT;

  private final GameState gameState = new GameState();
  private final BlocksLogic blocksLogic = new BlocksLogic(gameState, soundManager);
  private BlocksRenderer blocksRenderer;
  private final BlocksInputProcessor inputProcessor;

  private BlocksUI blocksUI;

  public BlocksScreen(TLLGame game) {
    this.game = game;
    inputProcessor = new BlocksInputProcessor(blocksLogic);
  }

  @Override
  public void show() {
    theme = Theme.THEMES[Config.getInstance().getPref("theme", 0)];

    int musicVolume = Config.getInstance().getPref("music_volume", 4);

    // Set the music volume in logaritmic scale
    float v = (float) ((Math.exp(musicVolume * 0.25f) - 1) / (Math.E - 1));

    soundManager.setGlobalMusicVolume(v);

    soundManager.load();

    blocksUI = new BlocksUI(game.getSkin(), game, blocksLogic, theme, viewport, batch);

    blocksRenderer = new BlocksRenderer(game.getSkin(), gameState, theme);

    final InputMultiplexer multiplexer = new InputMultiplexer();
    multiplexer.addProcessor(blocksUI.getStage());
    multiplexer.addProcessor(inputProcessor);
    Gdx.input.setInputProcessor(multiplexer);

    if (gameState.paused) blocksLogic.resume();
    else {
      blocksLogic.setGameMode(
          GameState.GameMode.values()[Config.getInstance().getPref("gameMode", 0)]);
      blocksLogic.setStartLevel(Config.getInstance().getPref("startLevel", 1));
      blocksLogic.newGame();
    }
  }

  @Override
  public void render(float v) {
    Gdx.gl.glClearColor(theme.bgColor.r, theme.bgColor.g, theme.bgColor.b, theme.bgColor.a);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    batch.setProjectionMatrix(viewport.getCamera().combined);
    batch.begin();
    // for long processing frames, limit delta to 1/30f to avoid skipping animations
    float delta = Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f);
    inputProcessor.update(delta);
    blocksLogic.update(delta);
    blocksRenderer.render(batch);
    batch.end();

    blocksUI.render(delta);
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
    blocksRenderer.resize((int) viewport.getWorldWidth(), (int) viewport.getWorldHeight());
    blocksUI.resize((int) viewport.getWorldWidth(), (int) viewport.getWorldHeight());
  }

  @Override
  public void pause() {
    blocksLogic.pause();
  }

  @Override
  public void resume() {
    blocksUI.hideMenu();
    blocksLogic.resume();
  }

  @Override
  public void hide() {
    soundManager.dispose();
  }

  @Override
  public void dispose() {
    batch.dispose();
  }

  public boolean isPaused() {
    return gameState.paused;
  }

  public void setPaused(boolean paused) {
    gameState.paused = paused;
  }
}
