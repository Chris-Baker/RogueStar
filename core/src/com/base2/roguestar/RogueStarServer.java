package com.base2.roguestar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.base2.roguestar.campaign.CampaignManager;
import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.network.messages.*;
import com.base2.roguestar.physics.PhysicsManager;
import com.base2.roguestar.physics.Simulation;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class RogueStarServer extends ApplicationAdapter {

	public final PhysicsManager physics = new PhysicsManager();
	public final CampaignManager campaign = new CampaignManager();
	public final EntityManager entities = new EntityManager();

	private static final float NETWORK_UPDATE_RATE = 1 / 10.0f;
	private float accum = 0;

	private Simulation simulation;

	private Server server;

	@Override
	public void create () {

		simulation = new Simulation();

		try {
			server = new Server();

			Kryo kryo = server.getKryo();
			kryo.register(TextMessage.class);
			kryo.register(CharacterControllerMessage.class);
			kryo.register(PhysicsBodyMessage.class);
			kryo.register(SyncSimulationRequestMessage.class);
			kryo.register(SyncSimulationResponseMessage.class);
			kryo.register(TimeRequestMessage.class);
			kryo.register(TimeResponseMessage.class);

			server.start();
			server.bind(54555, 54777);

			server.addListener(new Listener.LagListener(200, 200, new Listener() {
				public void received (Connection connection, Object object) {

					if (object instanceof TimeRequestMessage) {
						TimeRequestMessage request = (TimeRequestMessage)object;

						TimeResponseMessage response = new TimeResponseMessage();
						response.clientSentTime = request.timestamp;
						response.timestamp = TimeUtils.nanoTime();
						connection.sendUDP(response);
					}
					else if (object instanceof SyncSimulationRequestMessage) {
						SyncSimulationResponseMessage response = new SyncSimulationResponseMessage();
						response.x = simulation.px;
						response.y = simulation.py;
						connection.sendUDP(response);
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
				}
			}));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render () {

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

	}

	@Override
	public void dispose () {

	}
}
