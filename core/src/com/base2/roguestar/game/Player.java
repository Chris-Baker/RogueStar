package com.base2.roguestar.game;

import java.util.UUID;

public class Player {

    private UUID uid;
    private boolean isReady;
    private boolean isSpawned;

    public Player() {
        this.uid = UUID.randomUUID();
        this.isReady = false;
        this.isSpawned = false;
    }

    public Player(UUID uid) {
        this.uid = uid;
    }

    public Player(String uid) {
        this.uid = UUID.fromString(uid);
    }

    public UUID getUid() {
        return uid;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean isSpawned() {
        return isSpawned;
    }

    public void setSpawned(boolean spawned) {
        isSpawned = spawned;
    }
}
