package com.bladecoder.tll.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bladecoder.tll.TLLGame;

public class BlocksScreen implements Screen {

    private final TLLGame game;
    private Viewport viewport;
    private BlocksGame blocksGame;
    private SpriteBatch batch;
    private TextureAtlas atlas;

    private TextureAtlas.AtlasRegion bg1;
    private TextureAtlas.AtlasRegion tile;

    private BlocksInputProcessor inputProcessor;

    private int startLevel = 1;

    public BlocksScreen(TLLGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        atlas = new TextureAtlas("bg1.atlas");
        bg1 = atlas.findRegion("bg1");
        tile = atlas.findRegion("tile");
        viewport = new FitViewport(1920, 1080);
        blocksGame = new BlocksGame(batch, tile, game.getSkin());
        inputProcessor = new BlocksInputProcessor(game, blocksGame);
        Gdx.input.setInputProcessor(inputProcessor);

        blocksGame.setLevel(startLevel);
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(bg1, 0, 0);
        // for long processing frames, limit delta to 1/30f to avoid skipping animations
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f);
        inputProcessor.update(delta);
        blocksGame.update(delta);
        blocksGame.render();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
    }

    public void setStartLevel(int startLevel) {
        this.startLevel = startLevel;
    }
}
