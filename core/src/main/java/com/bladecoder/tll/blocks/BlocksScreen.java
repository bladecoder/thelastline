package com.bladecoder.tll.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bladecoder.tll.TLLGame;

public class BlocksScreen implements Screen {

    private final TLLGame game;
    private final Viewport viewport = new ScreenViewport();
    private final SpriteBatch batch = new SpriteBatch();

    private TextureAtlas atlas;
    private TextureAtlas.AtlasRegion background;
    private TextureAtlas.AtlasRegion tile;

    private int startLevel = 1;

    private Theme theme = Theme.SUNSET;

    private final GameState gameState = new GameState();
    private final BlocksLogic blocksLogic = new BlocksLogic(gameState);
    private BlocksRenderer blocksRenderer;
    private final BlocksInputProcessor inputProcessor;

    public BlocksScreen(TLLGame game) {
        this.game = game;
        inputProcessor = new BlocksInputProcessor(game, blocksLogic);
    }

    @Override
    public void show() {
        if(theme.atlas != null) {
            atlas = new TextureAtlas("theme1.atlas");
        }

        blocksRenderer = new BlocksRenderer(atlas, game.getSkin(), gameState, theme);
        Gdx.input.setInputProcessor(inputProcessor);

        blocksLogic.setStartLevel(startLevel);
        blocksLogic.newGame();
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(theme.bgColor.r, theme.bgColor.g, theme.bgColor.b, theme.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        // batch.draw(bg1, 0, 0);
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

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        if(atlas != null)
            atlas.dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public void setStartLevel(int startLevel) {
        this.startLevel = startLevel;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public void setGameMode(BlocksLogic.GameMode gameMode) {
        blocksLogic.setGameMode(gameMode);
    }
}
