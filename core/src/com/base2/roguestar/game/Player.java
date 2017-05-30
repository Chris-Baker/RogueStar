package com.base2.roguestar.game;

import java.util.UUID;

public class Player {

    private UUID uid;
    private boolean isReady;

    public Player() {
        this.uid = UUID.randomUUID();
        this.isReady = false;
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

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean isReady() {
        return isReady;
    }
}
