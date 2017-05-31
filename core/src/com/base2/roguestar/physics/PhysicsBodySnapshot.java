package com.base2.roguestar.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.UUID;

public class PhysicsBodySnapshot {

    private float timestamp;
    private String uid;
    private float x;
    private float y;
    private float vx;
    private float vy;
    private float angularVelocity;
    private float angle;

    public PhysicsBodySnapshot() {
    }

    public PhysicsBodySnapshot(Body body, UUID uid) {
        this.timestamp = TimeUtils.nanoTime();
        this.uid = uid.toString();
        this.x = body.getPosition().x;
        this.y = body.getPosition().y;
        this.vx = body.getLinearVelocity().x;
        this.vy = body.getLinearVelocity().y;
        this.angularVelocity = body.getAngularVelocity();
        this.angle = body.getAngle();
    }

    public PhysicsBodySnapshot getDelta(PhysicsBodySnapshot other) {

        PhysicsBodySnapshot delta = new PhysicsBodySnapshot();
        delta.timestamp = timestamp;
        delta.uid = uid;
        delta.x = x - other.x;
        delta.y = y - other.y;
        delta.vx = vx - other.vx;
        delta.vy = vy - other.vy;
        delta.angularVelocity = angularVelocity - other.angularVelocity;
        delta.angle = angle - other.angle;

        return delta;
    }

    public void applyDelta(PhysicsBodySnapshot delta) {
        this.x += delta.x;
        this.y += delta.y;
        this.vx += delta.vx;
        this.vy += delta.vy;
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVX() {
        return vx;
    }

    public float getVY() {
        return vy;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public float getAngle() {
        return angle;
    }
}
