package com.base2.roguestar.physics;

import com.badlogic.gdx.math.Vector2;

public class PhysicsBodySnapshot {

    public float timestamp;
    public Vector2 position = new Vector2();
    public Vector2 linearVelocity = new Vector2();
    public Vector2 angularVelocity = new Vector2();
    public float angle;

    public PhysicsBodySnapshot getDelta(PhysicsBodySnapshot other) {

        PhysicsBodySnapshot delta = new PhysicsBodySnapshot();
        delta.position.set(other.position.sub(position));
        delta.linearVelocity.set(other.linearVelocity.sub(linearVelocity));
        delta.angularVelocity.set(other.angularVelocity.sub(angularVelocity));
        delta.angle = other.angle - angle;

        return delta;
    }
}
