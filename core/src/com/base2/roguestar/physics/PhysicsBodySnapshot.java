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
        delta.timestamp = timestamp;
        delta.uid = uid;
        delta.position.set(position.sub(other.position));
        delta.linearVelocity.set(linearVelocity.sub(other.linearVelocity));
        delta.angularVelocity = angularVelocity - other.angularVelocity;
        delta.angle = angle - other.angle;

        return delta;
    }

    public void applyDelta(PhysicsBodySnapshot delta) {
        this.position.add(delta.position);
        this.linearVelocity.add(delta.linearVelocity);
        this.angularVelocity += delta.angularVelocity;
        this.angle += delta.angle;
    }

    public UUID getUid() {
        return UUID.fromString(uid);
    }

    public float getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(float timestamp) {
        this.timestamp = timestamp;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getLinearVelocity() {
        return linearVelocity;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public float getAngle() {
        return angle;
    }
}
