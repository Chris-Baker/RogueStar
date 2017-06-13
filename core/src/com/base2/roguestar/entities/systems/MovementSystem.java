package com.base2.roguestar.entities.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.base2.roguestar.controllers.CharacterController;
import com.base2.roguestar.controllers.KeyboardController;
import com.base2.roguestar.entities.components.ControllerComponent;
import com.base2.roguestar.entities.components.CharacterComponent;
import com.base2.roguestar.entities.components.RunSpeedComponent;
import com.base2.roguestar.phys2d.PhysBody;

/**
 * Created by Chris on 28/03/2016.
 */
public class MovementSystem extends IteratingSystem {

    private ComponentMapper<ControllerComponent> cm;
    private ComponentMapper<CharacterComponent> pm;
    private ComponentMapper<RunSpeedComponent> rm;

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

        CharacterController controller = c.controller;
        Body body = p.body;
        PhysBody physBody = p.physBody;
        int runSpeed = r.runSpeed;
        float angle = body.getAngle();
        float x = physBody.getX();
        float y = physBody.getY();

        // reset horizontal velocity
        physBody.setVelocity(0, physBody.getVelocity().y);

        // move left
        if (controller.moveLeft && !controller.moveRight) {
            physBody.setVelocity((physBody.getVelocity().x -= runSpeed) * deltaTime, physBody.getVelocity().y);
            physBody.setPosition(x - (runSpeed * deltaTime), y);
            body.setTransform(physBody.getX(), physBody.getY(), angle);
        }

        // move right
        if (controller.moveRight && !controller.moveLeft) {
            physBody.setVelocity((physBody.getVelocity().x += runSpeed) * deltaTime, physBody.getVelocity().y);
            physBody.setPosition(x + (runSpeed * deltaTime), y);
            body.setTransform(physBody.getX(), physBody.getY(), angle);
        }

        // jump, but only when grounded
        if (controller.jump) {
            controller.jump = false;
            if (p.isGrounded) {
                // do the jump
                p.isGrounded = false;
            }
        }
    }
}
