package com.base2.roguestar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.base2.roguestar.events.EventManager;
import com.base2.roguestar.maps.MapManager;
import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.network.messages.*;
import com.base2.roguestar.physics.PhysicsManager;
import com.base2.roguestar.physics.Simulation;
import com.base2.roguestar.utils.CollisionLoader;
import com.base2.roguestar.utils.EntityLoader;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class RogueStarServer extends ApplicationAdapter {

	//	Server
	//	Pre game state
	//   - start up listener
	//   - accept up to 4 player connections
	//   - for each connection create a player object and share it with all connections
	//   - with the player object track each player connection so if they disconnect and reconnect they will persist
	//   - listen for player class selections
	//   - listen for weapon and equipment upgrades
	//   - listen for all players ready
	//   - if players disconnect from this state then remove them and delete their player object
	//	any point after this disconnected players will go through the loading state and be added
	//	back into play from where they where previously
	//	Loading game state
	//   - load the map and create the physic simulation and entities
	//   - spawn an entity for each player
	//   - signal that the server is ready
	//   - once all clients have signaled they are ready start the game
	//   - tell clients to start the game
	//
	//	Game state
	//   - handle player input
	//   - update all players with movement and events

	public final EventManager events = new EventManager();
	public final PhysicsManager physics = new PhysicsManager();
	public final MapManager maps = new MapManager();
	public final EntityManager entities = new EntityManager();

	private static final float NETWORK_UPDATE_RATE = 1 / 10.0f;
	private float accum = 0;

	private Simulation simulation;

	private Server server;

	private GameState gameState;

	@Override
	public void create () {

		this.setState(GameState.SETUP);

		events.init();
		physics.init();
		entities.init();

		// subscribe to events
		events.subscribe(physics);
		events.subscribe(entities);

		simulation = new Simulation();

		try {
			server = new Server();

			Kryo kryo = server.getKryo();
			kryo.register(TextMessage.class);
			kryo.register(CharacterControllerMessage.class);
			kryo.register(PhysicsBodyMessage.class);
			kryo.register(SyncSimulationRequestMessage.class);
			kryo.register(SyncSimulationResponseMessage.class);
			kryo.register(Ping.class);
			kryo.register(Ack.class);
			kryo.register(SetMapMessage.class);

			server.start();
			server.bind(54555, 54777);

			server.addListener(new Listener.LagListener(200, 200, new Listener() {
				public void received (Connection connection, Object object) {

					if (object instanceof Ping) {
						Ping request = (Ping)object;
						Ack response = new Ack();
						response.clientSentTime = request.timestamp;
						response.timestamp = TimeUtils.nanoTime();
						connection.sendUDP(response);
					}
					else if (object instanceof SyncSimulationRequestMessage) {
						SyncSimulationResponseMessage response = new SyncSimulationResponseMessage();
						response.x = simulation.px;
						response.y = simulation.py;
						connection.sendTCP(response);
					}
					else if (object instanceof TextMessage) {
						TextMessage request = (TextMessage)object;
						System.out.println(request.message);

						TextMessage response = new TextMessage();
						response.message = "Thanks";
						connection.sendTCP(response);
					}
					else if (object instanceof CharacterControllerMessage) {
						CharacterControllerMessage request = (CharacterControllerMessage) object;

						// set the character controller of this entity
						if (request.moveLeft) {
							simulation.px -= 1;
						}

						if (request.moveRight) {
							simulation.px += 1;
						}
					}
					else if (object instanceof SetMapMessage) {
						SetMapMessage request = (SetMapMessage) object;
						final String mapName = request.mapName;
						Gdx.app.postRunnable(new Runnable() {
							public void run() {
								maps.load(mapName);
								RogueStarServer.this.setState(GameState.LOADING);
							}
						});
						server.sendToAllTCP(request);
					}
				}
			}));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render () {

		// on enter state stuff
		switch (gameState) {

			case SETUP:

				break;

			case LOADING:

				// map is loaded

				// load static collision bodies
				CollisionLoader.load(maps.getMap(), physics.world);

				// this loader should send each entity to the client for it to load
				EntityLoader.load(maps.getMap(), entities, physics.world, server);

				// we need to know which entities are the players

				// map is loaded we can change the state to playing and signal to all players to start the game
				setState(GameState.PLAYING);

				break;

			case PLAYING:

				accum += Gdx.graphics.getDeltaTime();

				// iterate over all entities and get the physics components

				// we should have the previous snapshot already from last update
				// we can see if there are new objects then we must send an update for them
				// we can compare the properties of existing ones and if thee are changes we also include them in the update

				// send snapshot object
				if (accum >= NETWORK_UPDATE_RATE) {
					accum = 0;
					PhysicsBodyMessage response = new PhysicsBodyMessage();
					response.timestamp = TimeUtils.nanoTime();
					response.x = simulation.px;
					response.y = simulation.py;
					server.sendToAllUDP(response);
				}

				break;

			case FINISHED:

				break;
		}

	}

	@Override
	public void dispose () {

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
