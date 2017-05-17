package com.base2.roguestar;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.base2.roguestar.maps.MapManager;
import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.network.NetworkClient;
import com.base2.roguestar.network.messages.*;
import com.base2.roguestar.physics.PhysicsManager;
import com.base2.roguestar.physics.Simulation;
import com.base2.roguestar.physics.SimulationSnapshot;
import com.base2.roguestar.screens.PlayScreen;

public class RogueStarClient extends Game {

	// build a very basic set of screens which map one to one with the major game states
	// the screens should just be dumb views so that the server can more easily remain separated.

	// 1. setup screen
	//    this screen will be replaced by the whole splash screen and menu screens for options and
	//    creating joining games. This will just have a single button to say the player is ready which
	//    will hard code a bunch of options which will later be player driven such as the map to load
	//    class and equipment. When the user clicks the ready button the server it notified and the game
	//    state is transitioned once all players are ready.

	// 2. loading screen
	//	  this screen shows loading animation whilst the game assets are loaded and the simulation is synced
	//    with the server and all other players.
	//    look at previous implementation of async asset loader

	// 3. play screen
	//    the simulation is updated by user input and the server and the game world is rendered




	//	NetworkClient
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
	public final MapManager maps = new MapManager();
	public final EntityManager entities = new EntityManager();
	public final NetworkClient network = new NetworkClient();

	public final OrthographicCamera camera = new OrthographicCamera();

	// simulation
	public Simulation simulation;
	private Simulation previousFrame;
	private Array<SimulationSnapshot> unverifiedUpdates;
	public Array<SimulationSnapshot> verifiedUpdates;
	private SimulationSnapshot verifiedUpdate;

	// player movement
	private boolean moveLeft = false;
	private boolean moveRight = false;
	private boolean jump = false;

	// render
	private ShapeRenderer shapeRenderer;
	//public final OrthographicCamera camera  = new OrthographicCamera(640, 480);

	private GameState gameState = GameState.SETUP;

	@Override
	public void create() {

		setScreen(new PlayScreen(this));

		network.init(this);

		shapeRenderer = new ShapeRenderer();
		simulation = new Simulation();
		previousFrame = new Simulation();
		unverifiedUpdates = new Array<SimulationSnapshot>();
		verifiedUpdates = new Array<SimulationSnapshot>();

		// kick off a game after the player has chosen map, class, and equipment
		SetMapMessage request = new SetMapMessage();
		request.mapName = "maps/map.tmx";
		network.sendUDP(request);

//        game.physics.init();
//        game.entities.init(game);

//        CollisionLoader.load(game.maps.getMap(), game.physics.world);
//        EntityLoader.load(game.maps.getMap(), game.entities.engine, game.physics.world);

	}

	@Override
	public void dispose() {
		super.dispose();
		//physics.dispose();
		//maps.dispose();
	}

	@Override
	public void render() {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// get user input
		this.moveLeft = Gdx.input.isKeyPressed(Input.Keys.A);
		this.moveRight = Gdx.input.isKeyPressed(Input.Keys.D);
		this.jump = Gdx.input.isKeyPressed(Input.Keys.SPACE);

		// update our previous frame simulation so we can calculate the deltas of everything
		previousFrame.px = simulation.px;
		previousFrame.py = simulation.py;

		// update the simulation locally based on player input
		if (this.moveLeft) {
			simulation.px -= 1;
		}
		if (this.moveRight) {
			simulation.px += 1;
		}

		// store our update as an unverified update
		SimulationSnapshot updateDelta = new SimulationSnapshot();
		updateDelta.timestamp = TimeUtils.nanoTime();
		updateDelta.px = simulation.px - previousFrame.px;
		updateDelta.py = simulation.py - previousFrame.py;

		if (updateDelta.px != 0) {
			unverifiedUpdates.add(updateDelta);
		}

		// insert verified updates and reapply unverified updates
		if (verifiedUpdates.size > 0) {
			// get the timestamp of the verified update
			verifiedUpdate = verifiedUpdates.pop();
			verifiedUpdates.clear();

			int index = 0;
			while (index < unverifiedUpdates.size) {
				SimulationSnapshot unverifiedUpdate = unverifiedUpdates.get(index);

				if (unverifiedUpdate.timestamp <= (verifiedUpdate.timestamp) - network.getPing() + network.getServerTimeAdjustment()) {
					unverifiedUpdates.removeIndex(index);
				}
				else {
					verifiedUpdate.px += unverifiedUpdate.px;
					verifiedUpdate.py += unverifiedUpdate.py;
					index += 1;
				}
			}

			// apply that to the simulation for rendering
			simulation.px = verifiedUpdate.px;
			simulation.py = verifiedUpdate.py;

		}

		// create network request to send player control inputs
		CharacterControllerMessage request = new CharacterControllerMessage();
		request.timestamp = TimeUtils.nanoTime();
		request.moveLeft = this.moveLeft;
		request.moveRight = this.moveRight;
		request.jump = this.jump;

		network.sendUDP(request);

		// render the world
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(1, 1, 0, 1);
		shapeRenderer.rect(simulation.px, simulation.py, 32, 32);
		if (verifiedUpdate != null) {
			shapeRenderer.setColor(0, 1, 0, 1);
			shapeRenderer.rect(verifiedUpdate.px, verifiedUpdate.py, 32, 32);
		}
		shapeRenderer.end();


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

	public void setState(GameState state) {
		this.gameState = state;
	}

}
