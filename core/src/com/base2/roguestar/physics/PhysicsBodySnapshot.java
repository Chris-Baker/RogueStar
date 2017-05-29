package com.base2.roguestar.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.UUID;

public class PhysicsBodySnapshot {

    private float timestamp;
    private String uid;
    private Vector2 position = new Vector2();
    private Vector2 linearVelocity = new Vector2();
    private float angularVelocity;
    private float angle;

    public PhysicsBodySnapshot() {
    }

    public PhysicsBodySnapshot(Body body, UUID uid) {
        this.timestamp = TimeUtils.nanoTime();
        this.uid = uid.toString();
        this.position.set(body.getPosition());
        this.linearVelocity.set(body.getLinearVelocity());
        this.angularVelocity = body.getAngularVelocity();
        this.angle = body.getAngle();
    }

    public PhysicsBodySnapshot getDelta(PhysicsBodySnapshot other) {

        PhysicsBodySnapshot delta = new PhysicsBodySnapshot();
        delta.timestamp = other.timestamp;
        delta.uid = other.uid;
        delta.position.set(other.position.sub(position));
        delta.linearVelocity.set(other.linearVelocity.sub(linearVelocity));
        delta.angularVelocity = other.angularVelocity - angularVelocity;
        delta.angle = other.angle - angle;

        return delta;
    }

    public UUID getUid() {
        return UUID.fromString(uid);
    }
}
