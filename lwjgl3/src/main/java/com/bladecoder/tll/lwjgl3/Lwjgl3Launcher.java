package com.bladecoder.tll.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bladecoder.tll.TLLGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    private static boolean windowed = false;

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.

        parseArgs(args);
        createApplication();
    }

    private static void parseArgs(String[] args) {
        for (String arg: args) {
            if(arg.equals("-w")) {
                windowed = true;
            }
        }
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new TLLGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("The Last Line");
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        if(windowed) {
            //configuration.setWindowedMode((int)(Lwjgl3ApplicationConfiguration.getDisplayMode().width * .4f), (int)(Lwjgl3ApplicationConfiguration.getDisplayMode().height * .7f));
            configuration.setWindowedMode(1920/2, 1080/2);
            configuration.setResizable(true);
        } else {
            configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        }

        configuration.setWindowIcon("icon128.png", "icon64.png", "icon32.png", "icon16.png");
        return configuration;
    }
}