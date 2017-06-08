package com.base2.roguestar.phys2d;

import com.badlogic.gdx.utils.Array;

public class PhysWorld {
    private Array<PhysBody> bodies = new Array<PhysBody>();

    public Array<PhysBody> getBodies() {
        return bodies;
    }

    public PhysBody createBody() {
        PhysBody body = new PhysBody();
        this.bodies.add(body);
        return body;
    }
}
