package com.base2.roguestar.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.base2.roguestar.RogueStarClient;
import com.base2.roguestar.utils.Config;

public class DesktopLauncher {
	public static void main (String[] arg) {

		// pack a bunch of textures
//		TexturePacker.Settings settings = new TexturePacker.Settings();
//		settings.maxWidth = 512;
//		settings.maxHeight = 512;
//		TexturePacker.process(settings, "../assets-build/images/complete", "../core/assets/images", "game");



		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new RogueStarClient(), config);
	}
}
