package com.base2.roguestar.entities.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.base2.roguestar.controllers.CharacterController;
import com.base2.roguestar.entities.components.ControllerComponent;
import com.base2.roguestar.entities.components.CharacterComponent;
import com.base2.roguestar.physics.Character;

public class MovementSystem extends IteratingSystem {

    private ComponentMapper<ControllerComponent> cm;
    private ComponentMapper<CharacterComponent> pm;

    private Vector2 velocity = new Vector2();

    public MovementSystem() {
        super(Family.all(ControllerComponent.class, CharacterComponent.class).get());

        cm = ComponentMapper.getFor(ControllerComponent.class);
        pm = ComponentMapper.getFor(CharacterComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        CharacterComponent p = pm.get(entity);
        ControllerComponent c = cm.get(entity);

        CharacterController controller = c.controller;
        Character character = p.character;

        velocity.x = 0;

        // move left
        if (controller.moveLeft) {
            velocity.x -= 1;
        }

        // move right
        if (controller.moveRight) {
            velocity.x += 1;
        }

        character.setVelocityForTimeInterval(velocity, deltaTime);

        // jump
        if (controller.jump) {
            if (character.canJump()) {
                character.jump();
            }
        }
    }
}
