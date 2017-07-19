package com.base2.roguestar.physics;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;

public class GameContactListener  extends ContactListener {
    @Override
    public boolean onContactAdded (int userValue0, int partId0, int index0, boolean match0, int userValue1, int partId1, int index1, boolean match1) {
        return true;
    }
}