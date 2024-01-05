package com.bladecoder.tll;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Game extends ApplicationAdapter {
    private SpriteBatch batch;
    private TextureAtlas image;

    private TextureAtlas.AtlasRegion bg1;
    private TextureAtlas.AtlasRegion tile;

    private Viewport viewport;
    private OrthographicCamera camera;

    private PlayField playfield;

    private TLLInputProcessor inputProcessor;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new TextureAtlas("bg1.atlas");
        bg1 = image.findRegion("bg1");
        tile = image.findRegion("tile");

        camera = new OrthographicCamera();
        viewport = new FitViewport(1920, 1080, camera);
        playfield = new PlayField(batch, tile);
        inputProcessor = new TLLInputProcessor(playfield);
        Gdx.input.setInputProcessor(inputProcessor);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(bg1, 0, 0);
        // for long processing frames, limit delta to 1/30f to avoid skipping animations
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f);
        inputProcessor.update(delta);
        playfield.render(delta);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
