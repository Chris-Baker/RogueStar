package com.base2.roguestar.game;

import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.events.Event;
import com.base2.roguestar.events.EventSubscriber;
import com.base2.roguestar.events.messages.AddPlayerEvent;
import com.base2.roguestar.utils.Locator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager implements EventSubscriber {

    private EntityManager entities;
    private Map<UUID, Player> players;
    private UUID localPlayerUid;
    private String mapPath;

    public GameManager() {
        this.players = new HashMap<UUID, Player>();
    }

    public void init() {
        this.entities = Locator.getEntityManager();
    }

    public String getMapPath() {
        return mapPath;
    }

    public void setMapPath(String mapPath) {
        this.mapPath = mapPath;
    }

    public void addPlayer(Player player) {
        this.players.put(player.getUid(), player);
    }

    public Player removePlayer(UUID uid) {

        if (localPlayerUid.equals(uid)) {
            localPlayerUid = null;
        }

        return players.remove(uid);
    }

    public UUID getLocalPlayerUid() {
        return localPlayerUid;
    }

    public void setLocalPlayerUid(UUID uid) {
        this.localPlayerUid = uid;
    }

    public boolean isLocalPlayer(UUID uid) {
        return uid == this.localPlayerUid;
    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof AddPlayerEvent) {
            AddPlayerEvent addPlayer = (AddPlayerEvent)event;
            Player player = new Player(addPlayer.uid);
            this.addPlayer(player);

            if (addPlayer.isLocalPlayer) {
                this.setLocalPlayerUid(addPlayer.uid);
            }
        }
    }

    public UUID getUnspawnedPlayerUid() {
        for (Player player : players.values()) {
            if (!entities.entityExists(player.getUid())) {
                return player.getUid();
            }
        }
        return null;
    }
}
