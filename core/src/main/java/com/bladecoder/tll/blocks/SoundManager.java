package com.bladecoder.tll.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
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

        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(0.5f);
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
        music.pause();
    }

    public void musicPlay() {
        //music.play();
    }

    public void musicStop() {
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

        music.stop();
        music.dispose();
    }
}
