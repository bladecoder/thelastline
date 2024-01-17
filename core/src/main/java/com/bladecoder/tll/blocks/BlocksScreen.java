package com.bladecoder.tll.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bladecoder.tll.TLLGame;
import com.bladecoder.tll.util.Config;

public class BlocksScreen implements Screen {

    private final TLLGame game;
    private final Viewport viewport = new ScreenViewport();
    private final SpriteBatch batch = new SpriteBatch();

    private TextureAtlas atlas;

    private final SoundManager soundManager = new SoundManager();
    private Theme theme = Theme.DEFAULT;

    private final GameState gameState = new GameState();
    private final BlocksLogic blocksLogic = new BlocksLogic(gameState, soundManager);
    private BlocksRenderer blocksRenderer;
    private final BlocksInputProcessor inputProcessor;

    public BlocksScreen(TLLGame game) {
        this.game = game;
        inputProcessor = new BlocksInputProcessor(game, blocksLogic);
    }

    @Override
    public void show() {
        theme = Theme.THEMES[Config.getInstance().getPref("theme", 0)];

        if(theme.atlas != null) {
            atlas = new TextureAtlas(theme.atlas);
        }

        soundManager.load();

        blocksRenderer = new BlocksRenderer(atlas, game.getSkin(), gameState, theme);
        Gdx.input.setInputProcessor(inputProcessor);

        if(gameState.paused)
            blocksLogic.resume();
        else {
            blocksLogic.setGameMode(GameState.GameMode.values()[Config.getInstance().getPref("gameMode", 0)]);
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
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        blocksRenderer.resize((int)viewport.getWorldWidth(), (int)viewport.getWorldHeight());
    }

    @Override
    public void pause() {
        blocksLogic.pause();
    }

    @Override
    public void resume() {
        blocksLogic.resume();
    }

    @Override
    public void hide() {
        if(atlas != null) {
            atlas.dispose();
            atlas = null;
        }
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
