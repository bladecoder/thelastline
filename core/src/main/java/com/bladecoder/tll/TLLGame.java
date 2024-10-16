package com.bladecoder.tll;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.postprocessing.effects.CrtMonitor;
import com.bitfire.postprocessing.effects.Curvature;
import com.bitfire.postprocessing.filters.Combine;
import com.bitfire.postprocessing.filters.CrtScreen;
import com.bitfire.utils.ShaderLoader;
import com.bladecoder.tll.blocks.BlocksScreen;
import com.bladecoder.tll.ui.BladeSkin;
import com.bladecoder.tll.util.EngineLogger;
import com.bladecoder.tll.util.RectangleRenderer;

public class TLLGame extends Game {
    private static final String SKIN_FILENAME = "ui.json";
    private BlocksScreen blocksScreen;

    private MainMenuScreen menuScreen;

    private BladeSkin skin;

    private PostProcessor postProcessor;
    private CrtMonitor crt;

    public BladeSkin getSkin() {
        return skin;
    }

    @Override
    public void create() {
        loadAssets();

        blocksScreen = new BlocksScreen(this);
        menuScreen = new MainMenuScreen(this);

        Gdx.input.setCatchKey(Input.Keys.MENU, true);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        EngineLogger.setDebug();
        EngineLogger.debug("TLLGame created");
        EngineLogger.debug("Controllers: " + Controllers.getControllers().size);

        setMenuScreen();
    }

    public void setMenuScreen() {
        setScreen(menuScreen);
    }

    public void setBlocksScreen() {
        setScreen(blocksScreen);
    }

    public void setPaused(boolean paused) {
        blocksScreen.setPaused(paused);
    }

    @Override
    public void render () {
        float delta = Gdx.graphics.getDeltaTime();
        crt.setTime(delta);
        postProcessor.capture();
        super.render();
        postProcessor.render();
    }

    @Override
    public void resume() {
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            loadAssets();
        }

        postProcessor.rebind();

        super.resume();
    }

    @Override
    public void dispose() {
        skin.dispose();
        RectangleRenderer.dispose();
        postProcessor.dispose();
    }

    private void loadAssets() {
        FileHandle skinFile = Gdx.files.internal(SKIN_FILENAME);
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(SKIN_FILENAME.substring(0, SKIN_FILENAME.lastIndexOf('.')) + ".atlas"));
        skin = new BladeSkin(skinFile, atlas);
        setupPostprocessor();
    }

    private void setupPostprocessor() {
        ShaderLoader.BasePath = "shaders/";
        //boolean isDesktop = Gdx.app.getType() == Application.ApplicationType.Desktop;
        postProcessor = new PostProcessor( false, false, true );

        Curvature curvature = new Curvature();
        curvature.setDistortion(0.1f);
        postProcessor.addEffect( curvature );

//        Vignette vignette = new Vignette( Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false );
//        vignette.setIntensity(0.5f);
//        postProcessor.addEffect( vignette );

        int effects = CrtScreen.Effect.TweakContrast.v | CrtScreen.Effect.PhosphorVibrance.v | CrtScreen.Effect.Scanlines.v | CrtScreen.Effect.Tint.v | CrtScreen.Effect.Vignette.v ;
        crt = new CrtMonitor( Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, true, CrtScreen.RgbMode.ChromaticAberrations, effects );
        crt.setDistortion(.1f);
        Combine combine = crt.getCombinePass();
        combine.setSource1Intensity( 0f );
        combine.setSource2Intensity( 1f );
        combine.setSource1Saturation( 0f );
        combine.setSource2Saturation( 1f );

        postProcessor.addEffect( crt );

        Bloom bloom = new Bloom( (int)(Gdx.graphics.getWidth() * 0.25f), (int)(Gdx.graphics.getHeight() * 0.25f) );
        postProcessor.addEffect( bloom );
    }
}
