package com.base2.roguestar.game;

import java.util.UUID;

public class Player {

    private UUID uid;

    public Player() {
        this.uid = UUID.randomUUID();
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
}
