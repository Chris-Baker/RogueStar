package com.base2.roguestar.game;

import com.base2.roguestar.RogueStarServer;
import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.events.Event;
import com.base2.roguestar.events.EventManager;
import com.base2.roguestar.events.EventSubscriber;
import com.base2.roguestar.events.messages.AddPlayerEvent;
import com.base2.roguestar.events.messages.PlayerReadyEvent;
import com.base2.roguestar.events.messages.SetGameStateEvent;
import com.base2.roguestar.events.messages.SetMapEvent;
import com.base2.roguestar.utils.Locator;

import java.util.*;

public class GameManager implements EventSubscriber {

    private EventManager events;
    private EntityManager entities;
    private Map<UUID, Player> players;
    private UUID localPlayerUid;
    private String mapPath;

    public GameManager() {
        this.players = new HashMap<UUID, Player>();
    }

    public void init() {
        this.events = Locator.getEventManager();
        this.entities = Locator.getEntityManager();
    }

    public String getMap() {
        return mapPath;
    }

    public void setMap(String mapPath) {
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
        else if (event instanceof PlayerReadyEvent) {
            PlayerReadyEvent playerReadyEvent = (PlayerReadyEvent)event;
            setPlayerReady(UUID.fromString(playerReadyEvent.uid), true);

            if (allReady()) {
                SetGameStateEvent setGameStateEvent = new SetGameStateEvent();
                setGameStateEvent.state = GameState.LOADING;
                events.queue(setGameStateEvent);
            }
        }
        else if (event instanceof SetMapEvent) {
            SetMapEvent setMapEvent = (SetMapEvent)event;
            setMap(setMapEvent.mapName);
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

    public void setPlayerReady(UUID uid, boolean isReady) {
        players.get(uid).setReady(isReady);
    }

    public boolean allReady() {
        boolean allReady = true;
        for (Player player: players.values()) {
            if (!player.isReady()) {
                allReady = false;
                break;
            }
        }
        return allReady;
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }
}
