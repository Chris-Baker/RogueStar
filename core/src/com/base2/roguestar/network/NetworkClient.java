package com.base2.roguestar.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.base2.roguestar.events.messages.AddPlayerEvent;
import com.base2.roguestar.game.GameState;
import com.base2.roguestar.RogueStarClient;
import com.base2.roguestar.events.Event;
import com.base2.roguestar.events.EventManager;
import com.base2.roguestar.events.EventSubscriber;
import com.base2.roguestar.events.messages.CreateEntityEvent;
import com.base2.roguestar.events.messages.PlayerInputEvent;
import com.base2.roguestar.maps.MapManager;
import com.base2.roguestar.network.messages.*;
import com.base2.roguestar.physics.SimulationSnapshot;
import com.base2.roguestar.utils.Locator;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.UUID;

public class NetworkClient implements EventSubscriber {

    private Client client;
    private boolean isConnected = false;
    private long ping;
    private long serverTimeAdjustment;

    private static final float PING_RATE = 1 / 10.0f;
    private float accum = 0;

    public void init(final RogueStarClient game) {

        final MapManager maps = Locator.getMapManager();
        final EventManager events = Locator.getEventManager();

        try {
            client = new Client();

            Kryo kryo = client.getKryo();
            kryo.register(TextMessage.class);
            kryo.register(CharacterControllerMessage.class);
            kryo.register(PhysicsBodyMessage.class);
            kryo.register(SyncSimulationRequestMessage.class);
            kryo.register(SyncSimulationResponseMessage.class);
            kryo.register(Ping.class);
            kryo.register(Ack.class);
            kryo.register(SetMapMessage.class);
            kryo.register(CreateEntityMessage.class);
            kryo.register(JoinAsPlayerMessage.class);

            client.addListener(new Listener() {

                public void connected (Connection connection) {
                    System.out.println("Connected: " + connection.getID());
                    NetworkClient.this.isConnected = true;

                    // tell the server we want to join as a player
                    client.sendUDP(new JoinAsPlayerMessage());
                }

                public void received (Connection connection, Object object) {

                    if (object instanceof Ack) {
                        Ack response = (Ack)object;
                        NetworkClient.this.ping = (TimeUtils.nanoTime() - response.clientSentTime);
                        serverTimeAdjustment = (response.timestamp - (NetworkClient.this.ping)) - response.clientSentTime; // should this be ping / 2?
//                        System.out.println("Ping: " + TimeUtils.nanosToMillis((NetworkClient.this.ping)));
//                        System.out.println("NetworkClient Time: " + TimeUtils.nanosToMillis(TimeUtils.nanoTime()));
//                        System.out.println("Server Time: " + TimeUtils.nanosToMillis((response.timestamp - (NetworkClient.this.ping / 2))));
//                        System.out.println("Difference: " + TimeUtils.nanosToMillis(serverTimeAdjustment));
                    }
                    else if (object instanceof SyncSimulationResponseMessage) {
                        SyncSimulationResponseMessage response = (SyncSimulationResponseMessage)object;
                        game.simulation.px = response.x;
                        game.simulation.py = response.y;
                    }
                    else if (object instanceof TextMessage) {
                        TextMessage response = (TextMessage)object;
                        System.out.println(response.message);
                    }
                    else if (object instanceof PhysicsBodyMessage) {
                        PhysicsBodyMessage response = (PhysicsBodyMessage)object;
                        SimulationSnapshot verifiedUpdate = new SimulationSnapshot();
                        verifiedUpdate.timestamp = response.timestamp;
                        verifiedUpdate.px = response.x;
                        verifiedUpdate.py = response.y;
                        game.verifiedUpdates.add(verifiedUpdate);
                    }
                    else if (object instanceof SetMapMessage) {
                        SetMapMessage request = (SetMapMessage) object;
                        final String mapName = request.mapName;
                        Gdx.app.postRunnable(new Runnable() {
                            public void run() {
                                maps.load(mapName);
                                game.setState(GameState.LOADING);
                            }
                        });
                    }
                    else if (object instanceof CreateEntityMessage) {
                        CreateEntityMessage request = (CreateEntityMessage)object;
                        CreateEntityEvent event = new CreateEntityEvent();
                        event.uid = UUID.fromString(request.uid);
                        event.type = request.type;
                        event.x = request.x;
                        event.y = request.y;
                        event.rotation = request.rotation;
                        events.queue(event);
                        System.out.println("Create entity: " + event.type);
                    }
                    else if (object instanceof JoinAsPlayerMessage) {
                        JoinAsPlayerMessage request = (JoinAsPlayerMessage) object;
                        AddPlayerEvent event = new AddPlayerEvent();
                        event.uid = UUID.fromString(request.uid);
                        event.isLocalPlayer = request.isLocalPlayer;
                        events.queue(event);
                    }
                }
            });

            client.start();
            client.connect(5000, "localhost", 54555, 54777);

            Ping ping = new Ping();
            client.sendUDP(ping);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(float deltaTime) {

        accum += deltaTime;

        if (accum >= PING_RATE) {
            accum = 0;
            Ping ping = new Ping();
            client.sendUDP(ping);
        }

    }

    public long getPing() {
        return ping;
    }

    public long getServerTimeAdjustment() {
        return serverTimeAdjustment;
    }

    public void sendUDP(Message message) {
        if (this.isConnected) {
            client.sendUDP(message);
        }
        else {
            System.out.println("Not connected");
        }
    }

    public void sendTCP(Message message) {
        if (this.isConnected) {
            client.sendTCP(message);
        }
        else {
            System.out.println("Not connected");
        }
    }

    @Override
    public void handleEvent(Event event) {

        if (event instanceof PlayerInputEvent) {
            PlayerInputEvent playerInput = (PlayerInputEvent)event;

            // create network request to send player control inputs
            CharacterControllerMessage request = new CharacterControllerMessage();
            request.timestamp = TimeUtils.nanoTime();
            request.uid = playerInput.uid.toString();
            request.jump = playerInput.jump;
            request.moveLeft = playerInput.moveLeft;
            request.moveRight = playerInput.moveRight;

            sendUDP(request);
        }

    }
}
