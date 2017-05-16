package com.base2.roguestar.network;

import com.badlogic.gdx.utils.TimeUtils;
import com.base2.roguestar.RogueStarClient;
import com.base2.roguestar.network.messages.*;
import com.base2.roguestar.physics.SimulationSnapshot;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public class NetworkClient {

    private Client client;
    private long ping;
    private long serverTimeAdjustment;

    public void init(final RogueStarClient game) {

        try {
            client = new com.esotericsoftware.kryonet.Client();

            Kryo kryo = client.getKryo();
            kryo.register(TextMessage.class);
            kryo.register(CharacterControllerMessage.class);
            kryo.register(PhysicsBodyMessage.class);
            kryo.register(SyncSimulationRequestMessage.class);
            kryo.register(SyncSimulationResponseMessage.class);
            kryo.register(Ping.class);
            kryo.register(Ack.class);

            client.start();
            client.connect(5000, "localhost", 54555, 54777);

            SyncSimulationRequestMessage syncRequest = new SyncSimulationRequestMessage();
            client.sendTCP(syncRequest);

            Ping timeRequest = new Ping();
            timeRequest.timestamp = TimeUtils.nanoTime();
            client.sendUDP(timeRequest);

            client.addListener(new Listener() {
                public void received (Connection connection, Object object) {
                    if (object instanceof Ack) {
                        Ack response = (Ack)object;
                        ping = (TimeUtils.nanoTime() - response.clientSentTime);
                        serverTimeAdjustment = (response.timestamp - (ping)) - response.clientSentTime; // should this be ping / 2?
                        System.out.println("Ping: " + TimeUtils.nanosToMillis((ping)));
                        System.out.println("NetworkClient Time: " + TimeUtils.nanosToMillis(TimeUtils.nanoTime()));
                        System.out.println("Server Time: " + TimeUtils.nanosToMillis((response.timestamp - (ping / 2))));
                        System.out.println("Difference: " + TimeUtils.nanosToMillis(serverTimeAdjustment));
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
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getPing() {
        return ping;
    }

    public long getServerTimeAdjustment() {
        return serverTimeAdjustment;
    }

    public void sendUDP(Message message) {
        client.sendUDP(message);
    }
}
