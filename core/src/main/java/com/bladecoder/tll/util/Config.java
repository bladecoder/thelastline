/*******************************************************************************
 * Copyright 2014 Rafael Garcia Moreno.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.bladecoder.tll.util;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.util.Properties;

public final class Config {

	public static final String VERSION_PROP = "version";

	public static final String PROPERTIES_FILENAME = "tll.properties";
	public static final String PREFS_FILENAME = "prefs.properties";

	public static final String USER_FOLDER = ".tll/";

	private static Config instance;

	private final Properties config = new Properties();
	private final Properties prefs = new Properties();

	private Config() {
		load();
	}

	public static final Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}

		return instance;
	}

	public String getProperty(String key, String defaultValue) {
		return config.getProperty(key, defaultValue);
	}

	public void load() {
		config.clear();

		try {
			config.load(Gdx.files.internal(PROPERTIES_FILENAME).reader());
		} catch (Exception e) {
			EngineLogger.error("ERROR LOADING " + PROPERTIES_FILENAME + " :" + e.getMessage());
			return;
		}

		FileHandle prefsFile = getUserFile(PREFS_FILENAME);

		if (prefsFile.exists()) {
			try {
				prefs.load(prefsFile.reader());
			} catch (IOException e) {
				EngineLogger.error("ERROR LOADING PREFERENCES " + PREFS_FILENAME + ": " + e.getMessage());
			}
		}
	}

	public boolean getProperty(String key, boolean defaultValue) {
		boolean result = false;

		try {
			result = Boolean.parseBoolean(getProperty(key, String.valueOf(defaultValue)));
		} catch (Exception e) {
		}

		return result;
	}

	public int getProperty(String key, int defaultValue) {
		int result = 0;

		try {
			result = Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
		} catch (Exception e) {
		}

		return result;
	}

	public String getPref(String name, String defaultValue) {
		return prefs.getProperty(name, defaultValue);
	}

	public int getPref(String name, int defaultValue) {
		int result = 0;

		try {
			result = Integer.parseInt(prefs.getProperty(name, String.valueOf(defaultValue)));
		} catch (Exception e) {
		}

		return result;
	}

	public void setPref(String name, String value) {
		prefs.setProperty(name, value);
	}
	public void setPref(String name, int value) {
		prefs.setProperty(name, value + "");
	}

	public void savePrefs() {
		try {
			prefs.store(getUserFile(PREFS_FILENAME).writer(false), null);
		} catch (IOException e) {
			EngineLogger.error("ERROR SAVING PREFERENCES: " + e.getMessage());
		}
	}

	public FileHandle getUserFile(String filename) {
		return getUserFolder().child(filename);
	}

	public FileHandle getUserFolder() {
		FileHandle file = null;

		if (Gdx.app.getType() == Application.ApplicationType.Desktop) {

			StringBuilder sb = new StringBuilder();

			if (System.getProperty("os.name").toLowerCase().contains("mac")
					&& System.getenv("HOME").contains("Containers")) {

				file = Gdx.files.absolute(System.getenv("HOME") + "/" + sb.append(USER_FOLDER).toString());
			} else {

				file = Gdx.files.external(sb.append(USER_FOLDER).toString());
			}

		} else {
			file = Gdx.files.local(USER_FOLDER);
		}

		return file;
	}
}
