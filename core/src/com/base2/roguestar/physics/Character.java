package com.base2.roguestar.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class Character extends Body {

    private float stepHeight;
    private float jumpSpeed;
    private float maxJumpHeight;
    private float maxSlope;

    public Character(btCollisionShape collisionShape) {
        super(collisionShape);
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public void setJumpSpeed(float jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
    }

    public void setMaxJumpHeight(float maxJumpHeight) {
        this.maxJumpHeight = maxJumpHeight;
    }

    public void setMaxSlope(float maxSlope) {
        this.maxSlope = maxSlope;
    }

    public boolean canJump() {
        return false;
    }

    public void jump() {

    }

    public void setVelocityForTimeInterval(Vector2 velocity, float deltaTime) {

    }
}
