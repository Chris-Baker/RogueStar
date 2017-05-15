package com.base2.roguestar;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.base2.roguestar.campaign.CampaignManager;
import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.physics.PhysicsManager;
import com.base2.roguestar.screens.PlayScreen;

public class RogueStarClient extends Game {

	//	Client
	//   - connect to the server
	//
	//	Pre game screen
	//   - show screen
	//   - choose class
	//   - updated weapons and equipment
	//   - click ready or cancel game
	//   - transition screen to loading
	//
	//	Loading screen
	//   - show screen
	//   - get the name of the map to load from the server
	//   - load the map and create the physics simulation for static bodies only
	//   - sync simulation and entities with the server
	//   - once everything is constructed and the server has signaled ready
	//	then tell the server player is ready and give the user ready feedback
	//   - show other player loading progress
	//   - transition screen to play
	//
	//	Game screen
	//   - handle movement updates from server

	public final PhysicsManager physics = new PhysicsManager();
	public final CampaignManager campaign = new CampaignManager();
	public final EntityManager entities = new EntityManager();

	public final OrthographicCamera camera = new OrthographicCamera();

	@Override
	public void create() {
		setScreen(new PlayScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
		physics.dispose();
		campaign.dispose();
	}

	@Override
	public void render() {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}
