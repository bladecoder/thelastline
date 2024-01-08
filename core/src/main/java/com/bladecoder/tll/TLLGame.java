package com.bladecoder.tll;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.bladecoder.tll.blocks.BlocksScreen;
import com.bladecoder.tll.ui.BladeSkin;
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
        setScreen(new MenuScreen(this));
    }

    public void setBlocksScreen(int startLevel) {
        blocksScreen.setStartLevel(startLevel);
        setScreen(blocksScreen);
    }

    @Override
    public void dispose() {
        skin.dispose();
    }

    private void loadAssets() {
//        BladeSkin.addStyleTag(ChoicesUI.ChoicesUIStyle.class);
//        BladeSkin.addStyleTag(CreditsScreen.CreditScreenStyle.class);
//        BladeSkin.addStyleTag(MenuScreen.MenuScreenStyle.class);
//
//        BladeSkin.addStyleTag(StoryScreen.StoryScreenStyle.class);
//        BladeSkin.addStyleTag(TextPanel.TextPanelStyle.class);

        //loadI18NBundle();

        FileHandle skinFile = Gdx.files.internal(SKIN_FILENAME);
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(SKIN_FILENAME.substring(0, SKIN_FILENAME.lastIndexOf('.')) + ".atlas"));
        skin = new BladeSkin(skinFile, atlas);
    }
}
