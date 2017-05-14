package com.base2.roguestar.entities.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.base2.roguestar.controllers.KeyboardController;
import com.base2.roguestar.entities.components.ControllerComponent;
import com.base2.roguestar.entities.components.CharacterComponent;
import com.base2.roguestar.entities.components.RunSpeedComponent;

/**
 * Created by Chris on 28/03/2016.
 */
public class MovementSystem extends IteratingSystem {

    private final float MAX_VELOCITY = 14.0f;

    private ComponentMapper<ControllerComponent> cm;
    private ComponentMapper<CharacterComponent> pm;
    private ComponentMapper<RunSpeedComponent> rm;

    private final Vector2 velocity = new Vector2();

    private float stillTime = 0;
    private long lastGroundTime = 0;

    public MovementSystem() {
        super(Family.all(ControllerComponent.class, CharacterComponent.class, RunSpeedComponent.class).get());

        cm = ComponentMapper.getFor(ControllerComponent.class);
        pm = ComponentMapper.getFor(CharacterComponent.class);
        rm = ComponentMapper.getFor(RunSpeedComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        CharacterComponent p = pm.get(entity);
        ControllerComponent c = cm.get(entity);
        RunSpeedComponent r = rm.get(entity);

        KeyboardController controller = c.controller;
        Body body = p.body;
        Fixture physicsFixture = p.physicsFixture;
        Fixture sensorFixture = p.sensorFixture;
        int runSpeed = r.runSpeed;

        Vector2 vel = body.getLinearVelocity();
        Vector2 pos = body.getPosition();

        System.out.println(p.isGrounded);

//        if (p.isGrounded) {
//            lastGroundTime = System.nanoTime();
//        } else {
//            if (System.nanoTime() - lastGroundTime < 100000000) {
//                p.isGrounded = true;
//            }
//        }

        // cap max velocity on x
        if (Math.abs(vel.x) > MAX_VELOCITY) {
            vel.x = Math.signum(vel.x) * MAX_VELOCITY;
            body.setLinearVelocity(vel.x, vel.y);
        }

        // calculate stilltime & damp
        if (!controller.moveLeft && !controller.moveRight) {
            stillTime += deltaTime;
            body.setLinearVelocity(vel.x * 0.9f, vel.y);
        }
        else {
            stillTime = 0;
        }

        // disable friction while jumping
        if (!p.isGrounded) {
            physicsFixture.setFriction(0f);
            sensorFixture.setFriction(0f);
        } else {
            if (!controller.moveLeft && !controller.moveRight && stillTime > 0.2) {
                physicsFixture.setFriction(100f);
                sensorFixture.setFriction(100f);
            }
            else {
                physicsFixture.setFriction(0.0f);
                sensorFixture.setFriction(0.0f);
            }

//            if(groundedPlatform != null && groundedPlatform.dist == 0) {
//                player.applyLinearImpulse(0, -24, pos.x, pos.y,true);
//            }
        }

        // apply left impulse, but only if max velocity is not reached yet
        if (controller.moveLeft && vel.x > -MAX_VELOCITY) {
            body.applyLinearImpulse(-4f, 0, pos.x, pos.y, true);
        }

        // apply right impulse, but only if max velocity is not reached yet
        if (controller.moveRight && vel.x < MAX_VELOCITY) {
            body.applyLinearImpulse(4f, 0, pos.x, pos.y, true);
        }

        // jump, but only when grounded
        if (controller.jump) {
            controller.jump = false;

            if (p.isGrounded) {
                body.setLinearVelocity(vel.x, 0);
                body.setTransform(pos.x, pos.y + 0.01f, 0);
                body.applyLinearImpulse(0, 25, pos.x, pos.y, true);
                p.isGrounded = false;
            }
        }
    }
}
