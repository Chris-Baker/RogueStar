package com.base2.roguestar.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.UUID;

public class PhysicsBodySnapshot {

    private long timestamp;
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

    public boolean isChanged(PhysicsBodySnapshot other) {

        if (other == null) {
            return true;
        }

        float deltaX = x - other.x;
        float deltaY = y - other.y;
        float deltaVx = vx - other.vx;
        float deltaVy = vy - other.vy;
        float deltaAngularVelocity = angularVelocity - other.angularVelocity;
        float deltaAngle = angle - other.angle;

        return deltaX + deltaY + deltaVx + deltaVy + deltaAngularVelocity + deltaAngle != 0;
    }

    public UUID getUid() {
        return UUID.fromString(uid);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
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
