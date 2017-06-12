package com.base2.roguestar;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;
import com.base2.roguestar.controllers.CharacterController;
import com.base2.roguestar.entities.components.CharacterComponent;
import com.base2.roguestar.entities.components.ControllerComponent;
import com.base2.roguestar.events.Event;
import com.base2.roguestar.events.EventManager;
import com.base2.roguestar.events.EventSubscriber;
import com.base2.roguestar.events.messages.EntityCreatedEvent;
import com.base2.roguestar.events.messages.PlayerReadyEvent;
import com.base2.roguestar.events.messages.SetGameStateEvent;
import com.base2.roguestar.game.GameManager;
import com.base2.roguestar.game.GameState;
import com.base2.roguestar.game.Player;
import com.base2.roguestar.maps.MapManager;
import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.network.messages.*;
import com.base2.roguestar.physics.PhysicsBodySnapshot;
import com.base2.roguestar.physics.PhysicsManager;
import com.base2.roguestar.maps.CollisionLoader;
import com.base2.roguestar.utils.Locator;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.UUID;

public class RogueStarServer extends ApplicationAdapter implements EventSubscriber {

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

	private final EventManager events = new EventManager();
	private final PhysicsManager physics = new PhysicsManager();
	private final EntityManager entities = new EntityManager();
	private final MapManager maps = new MapManager();
	private final GameManager game = new GameManager();

	private static final float NETWORK_UPDATE_RATE = 1 / 10.0f;
	private float accum = 0;

	private ComponentMapper<CharacterComponent> physicsMapper;

	private Server server;

	private GameState gameState;

	@Override
	public void create () {

		this.setState(GameState.SETUP);

		// provide all our managers to our locator
		Locator.provide(events);
		Locator.provide(physics);
		Locator.provide(entities);
		Locator.provide(maps);
		Locator.provide(game);

		events.init();
		physics.init();
		entities.init();
		game.init();

		// subscribe to events
		events.subscribe(physics);
		events.subscribe(entities);
		events.subscribe(game);
		events.subscribe(this);

		physicsMapper = ComponentMapper.getFor(CharacterComponent.class);

		try {
			server = new Server();

			Kryo kryo = server.getKryo();
			kryo.register(TextMessage.class);
			kryo.register(CharacterControllerMessage.class);
			kryo.register(PhysicsBodySnapshotMessage.class);
			kryo.register(SyncSimulationRequestMessage.class);
			kryo.register(SyncSimulationResponseMessage.class);
			kryo.register(Ping.class);
			kryo.register(Ack.class);
			kryo.register(SetMapMessage.class);
			kryo.register(CreateEntityMessage.class);
			kryo.register(JoinAsPlayerMessage.class);
			kryo.register(PhysicsBodySnapshot.class);
			kryo.register(Vector2.class);
			kryo.register(PlayerReadyMessage.class);

			server.start();
			server.bind(54555, 54777);

			server.addListener(new Listener.LagListener(22, 22, new Listener() {
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
						final CharacterControllerMessage request = (CharacterControllerMessage) object;

						Gdx.app.postRunnable(new Runnable() {
							public void run() {
								Entity e = entities.getEntity(UUID.fromString(request.uid));
								CharacterController cc = e.getComponent(ControllerComponent.class).controller;
								cc.jump = request.jump;
								cc.moveLeft = request.moveLeft;
								cc.moveRight = request.moveRight;
							}
						});
					}
					else if (object instanceof PlayerReadyMessage) {
						PlayerReadyMessage request = (PlayerReadyMessage)object;
						PlayerReadyEvent event = new PlayerReadyEvent();
						event.uid = request.uid;
						events.queue(event);
						server.sendToAllTCP(request);
					}
					else if (object instanceof SetMapMessage) {
						SetMapMessage request = (SetMapMessage) object;
						final String mapName = request.mapName;
						Gdx.app.postRunnable(new Runnable() {
							public void run() {
								game.setMap(mapName);
							}
						});
						server.sendToAllTCP(request);
					}
					else if (object instanceof JoinAsPlayerMessage) {

						// create a new player and add it to the game manager
						Player newPlayer = new Player();
						game.addPlayer(newPlayer);


						// send all joined players to the new player
						for (Player player: game.getPlayers()) {
							JoinAsPlayerMessage request = new JoinAsPlayerMessage();
							request.uid = player.getUid().toString();
							request.isLocalPlayer = player.getUid().toString().equals(newPlayer.getUid().toString());
							connection.sendUDP(request);
						}

						// send the new player to all other players
						JoinAsPlayerMessage response = new JoinAsPlayerMessage();
						response.uid = newPlayer.getUid().toString();
						response.isLocalPlayer = false;
						server.sendToAllExceptUDP(connection.getID(), response);
					}
				}
			}));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render () {

		float deltaTime = Gdx.graphics.getDeltaTime();

		this.events.update();

		// on enter state stuff
		switch (gameState) {

			case SETUP:

				break;

			case LOADING:

				// map is loaded

				// load static collision bodies
				CollisionLoader.load(maps.getMap(), physics.getWorld(), physics.getPhysWorld());

				// this loader should send each entity to the client for it to load
				maps.loadEntities();

				// we need to know which entities are the players

				// map is loaded we can change the state to playing and signal to all players to start the game
				setState(GameState.PLAYING);

				break;

			case PLAYING:

				// update managers
				this.physics.preUpdate();
				this.entities.update(deltaTime);
				this.physics.update(deltaTime);

				accum += deltaTime;

				// iterate over all the physics components


				// we should have the previous snapshot already from last update
				// we can see if there are new objects then we must send an update for them
				// we can compare the properties of existing ones and if thee are changes we also include them in the update

				// send snapshot object
				if (accum >= NETWORK_UPDATE_RATE) {
					accum = 0;

					// iterate over our physics components, create and send physics snapshots for each body
					for (Entity entity: entities.getEntitiesFor(Family.all(CharacterComponent.class).get())) {
						CharacterComponent cc = physicsMapper.get(entity);
						Body body = cc.body;
						PhysicsBodySnapshot bodySnapshot = new PhysicsBodySnapshot(body, entities.getUUID(entity));
						PhysicsBodySnapshot PhysicsBodySnapshot = this.physics.getPreviousSnapshot(entities.getUUID(entity));

						if (bodySnapshot.isChanged(PhysicsBodySnapshot)) {
							PhysicsBodySnapshotMessage response = new PhysicsBodySnapshotMessage();
							response.timestamp = TimeUtils.nanoTime();
							response.snapshot = bodySnapshot;
							server.sendToAllUDP(response);
						}
					}
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
				maps.load(game.getMap());
				break;

			case PLAYING:
				System.out.println("Set state: Playing");
				break;

			case FINISHED:
				System.out.println("Set state: Finished");
				break;
		}

	}

	@Override
	public void handleEvent(Event event) {

		if (event instanceof EntityCreatedEvent) {
			EntityCreatedEvent ce = (EntityCreatedEvent)event;
			CreateEntityMessage request = new CreateEntityMessage();
			request.uid = ce.uid.toString();
			request.type = ce.type;
			request.x = ce.x;
			request.y = ce.y;
			request.rotation = ce.rotation;
			server.sendToAllTCP(request);
		}
		if (event instanceof SetGameStateEvent) {
			SetGameStateEvent setGameStateEvent = (SetGameStateEvent)event;
			this.setState(setGameStateEvent.state);
		}
	}
}
