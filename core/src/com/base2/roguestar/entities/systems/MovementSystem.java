package com.base2.roguestar.entities.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.base2.roguestar.controllers.CharacterController;
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

    private Vector3 velocity = new Vector3();

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
        btKinematicCharacterController characterController = p.character;

        velocity.x = 0;

        // move left
        if (controller.moveLeft) {
            velocity.x -= 1;
        }

        // move right
        if (controller.moveRight) {
            velocity.x += 1;
        }

        characterController.setVelocityForTimeInterval(velocity, deltaTime);

        // jump
        if (controller.jump) {
            if (characterController.canJump()) {
                characterController.jump();
            }
        }
    }
}
