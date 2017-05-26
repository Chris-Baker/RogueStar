package com.base2.roguestar.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    private Map<UUID, Player> players;
    private String mapPath;

    public GameManager() {
        this.players = new HashMap<UUID, Player>();
    }

    public String getMapPath() {
        return mapPath;
    }

    public void setMapPath(String mapPath) {
        this.mapPath = mapPath;
    }

    public void addPlayer(Player player) {
        this.players.put(player.uid, player);
    }

    public Player removePlayer(UUID uid) {
        return players.remove(uid);
    }
}
