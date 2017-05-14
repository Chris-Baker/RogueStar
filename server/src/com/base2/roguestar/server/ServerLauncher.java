package com.base2.roguestar.server;

import static org.mockito.Mockito.mock;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.base2.roguestar.RogueStarServer;
import com.badlogic.gdx.backends.headless.*;

public class ServerLauncher {
	public static void main (String[] arg) {

        Gdx.gl = mock(GL20.class);
        HeadlessApplicationConfiguration conf = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new RogueStarServer(), conf);
	}
}
