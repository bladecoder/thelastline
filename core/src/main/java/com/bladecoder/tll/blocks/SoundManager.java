package com.bladecoder.tll.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    private final static String[] MUSIC_FILES = {"8bitmelody.mp3", "hauntedcastle.mp3", "newbattle.mp3"};
    private final static float[] MUSIC_VOLUMES = {0.2f, 0.2f, 0.9f};

    private float globalMusicVolume = 1.0f;

    private Sound lockdownSound;
    private Sound rotateSound;

    private Sound moveSound;

    private Sound lineClearSound;

    private Sound youLoseSound;
    private Sound youWinSound;

    private Sound levelUpSound;

    private Sound tetrisSound;

    private Music music;

    public void load() {
        lockdownSound = Gdx.audio.newSound(Gdx.files.internal("lockdown.wav"));
        rotateSound = Gdx.audio.newSound(Gdx.files.internal("rotate.wav"));
        moveSound = Gdx.audio.newSound(Gdx.files.internal("move.wav"));
        lineClearSound = Gdx.audio.newSound(Gdx.files.internal("lineclear.wav"));
        youLoseSound = Gdx.audio.newSound(Gdx.files.internal("youlose.wav"));
        youWinSound = Gdx.audio.newSound(Gdx.files.internal("youwin.wav"));
        levelUpSound = Gdx.audio.newSound(Gdx.files.internal("levelup.wav"));
        tetrisSound = Gdx.audio.newSound(Gdx.files.internal("tetris.wav"));
    }

    public void lockdown() {
        lockdownSound.play();
    }

    public void rotate() {
        rotateSound.play(0.5f);
    }

    public void move() {
        moveSound.play(0.3f);
    }

    public void lineClear() {
        lineClearSound.play();
    }

    public void youLose() {
        youLoseSound.play();
    }

    public void youWin() {
        youWinSound.play();
    }

    public void levelUp() {
        levelUpSound.play();
    }

    public void tetris() {
        tetrisSound.play();
    }

    public void musicPause() {
        if(music != null)
            music.pause();
    }

    public void musicPlay(int level) {

        if(!isMusicEnabled())
            return;

        if(music != null) {
            music.stop();
            music.dispose();
        }

        String musicFile;

        int currentMusic = (level - 1) / 10;

        musicFile = MUSIC_FILES[currentMusic];

        music = Gdx.audio.newMusic(Gdx.files.internal(musicFile));

        music.setVolume(MUSIC_VOLUMES[currentMusic] * globalMusicVolume);
        music.setLooping(true);
        music.play();
    }

    public void musicStop() {
        if(music != null)
            music.stop();
    }


    public void dispose() {
        lockdownSound.dispose();
        rotateSound.dispose();
        moveSound.dispose();
        lineClearSound.dispose();
        youLoseSound.dispose();
        youWinSound.dispose();
        levelUpSound.dispose();
        tetrisSound.dispose();

        if(music != null) {
            music.stop();
            music.dispose();
        }
    }

    private boolean isMusicEnabled() {
        return globalMusicVolume > 0.0f;
    }

    public void setGlobalMusicVolume(float globalMusicVolume) {
        this.globalMusicVolume = globalMusicVolume;
    }
}
