package com.bladecoder.tll;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.bladecoder.tll.blocks.BlocksLogic;
import com.bladecoder.tll.blocks.BlocksScreen;
import com.bladecoder.tll.blocks.GameState;
import com.bladecoder.tll.blocks.Theme;
import com.bladecoder.tll.ui.BladeSkin;
import com.bladecoder.tll.util.Config;
import com.bladecoder.tll.util.EngineLogger;

public class TLLGame extends Game {
    private static final String SKIN_FILENAME = "ui/ui.json";
    private BlocksScreen blocksScreen;

    private MenuScreen menuScreen;

    private BladeSkin skin;

    public BladeSkin getSkin() {
        return skin;
    }

    @Override
    public void create() {
        loadAssets();

        blocksScreen = new BlocksScreen(this);
        menuScreen = new MenuScreen(this);

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

    public boolean isPaused() {
        return blocksScreen.isPaused();
    }

    public void setPaused(boolean paused) {
        blocksScreen.setPaused(paused);
    }

    @Override
    public void dispose() {
        skin.dispose();
    }

    private void loadAssets() {
        FileHandle skinFile = Gdx.files.internal(SKIN_FILENAME);
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(SKIN_FILENAME.substring(0, SKIN_FILENAME.lastIndexOf('.')) + ".atlas"));
        skin = new BladeSkin(skinFile, atlas);
    }
}
