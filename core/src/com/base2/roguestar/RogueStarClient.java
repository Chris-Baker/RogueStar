package com.base2.roguestar;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.base2.roguestar.events.EventManager;
import com.base2.roguestar.game.GameManager;
import com.base2.roguestar.game.GameState;
import com.base2.roguestar.maps.MapManager;
import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.network.NetworkClient;
import com.base2.roguestar.physics.PhysicsManager;
import com.base2.roguestar.screens.PlayScreen;
import com.base2.roguestar.screens.SetupScreen;
import com.base2.roguestar.maps.CollisionLoader;
import com.base2.roguestar.utils.Locator;

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

	private final EventManager events = new EventManager();
	private final PhysicsManager physics = new PhysicsManager();
	private final EntityManager entities = new EntityManager();
	private final MapManager maps = new MapManager();
	private final GameManager game = new GameManager();
	public final NetworkClient network = new NetworkClient();

	private final OrthographicCamera camera = new OrthographicCamera();

	private GameState gameState = null;

	@Override
	public void create() {

		// provide all our managers to our locator
		Locator.provide(events);
		Locator.provide(physics);
		Locator.provide(entities);
		Locator.provide(maps);
		Locator.provide(game);

		events.init();
		network.init(this);
		physics.init();
		entities.init();
		entities.setCamera(this.camera);

		// subscribe to managers events
		events.subscribe(network);
		events.subscribe(physics);
		events.subscribe(entities);
		events.subscribe(maps);
		events.subscribe(game);

		// set our starting game state and screen
		setState(GameState.SETUP);
		setScreen(new SetupScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
		//physics.dispose();
		//maps.dispose();
	}

	@Override
	public void render() {

		float deltaTime = Gdx.graphics.getDeltaTime();

		this.events.update();

		// state specific update stuff
		switch (gameState) {

			case SETUP:

				// map is chosen by the game initiator and game is listed
				// kick off a game after the player has chosen map, class, and equipment

				// player joins setup room

				// player is configured with the server


				// wait for user to click ready

				// wait for server to signal all ready

				break;

			case LOADING:

				// network client initiates the map load and game state change in message handler
				// server sends map to load
				// load map

				// load static collision bodies
				CollisionLoader.load(maps.getMap(), physics.getWorld());

				// server sends entities to load

				// we need to know which entities are the players

				// server signals map loaded
				// we can just set the state for now
				setState(GameState.PLAYING);
				setScreen(new PlayScreen(camera));

				break;

			case PLAYING:

				// store our previous frame simulation so we can calculate the deltas of everything
				physics.preUpdate();

				// update managers
				this.entities.update(deltaTime);
				this.physics.update(deltaTime);
				this.network.update(deltaTime);

				// store our update as an unverified update
				physics.postUpdate();

				break;

			case FINISHED:

				break;
		}

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// render the screen
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

		// on enter state stuff
		switch (gameState) {

			case SETUP:
				System.out.println("Set state: Setup");
				break;

			case LOADING:
				System.out.println("Set state: Loading");
				break;

			case PLAYING:
				System.out.println("Set state: Playing");
				break;

			case FINISHED:
				System.out.println("Set state: Finished");
				break;
		}

	}

}
