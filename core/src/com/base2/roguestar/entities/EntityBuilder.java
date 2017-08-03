package com.base2.roguestar.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.base2.roguestar.controllers.CharacterControllerSnapshot;
import com.base2.roguestar.controllers.KeyboardController;
import com.base2.roguestar.controllers.NetworkController;
import com.base2.roguestar.entities.components.*;
import com.base2.roguestar.events.EventManager;
import com.base2.roguestar.events.messages.EntityCreatedEvent;
import com.base2.roguestar.game.GameManager;
import com.base2.roguestar.physics.Character;
import com.base2.roguestar.physics.PhysicsManager;
import com.base2.roguestar.utils.Locator;

import java.util.UUID;

public class EntityBuilder {

    EntityManager entities;
    PhysicsManager physics;
    EventManager events;
    GameManager game;

    public void init() {
        this.entities = Locator.getEntityManager();
        this.physics = Locator.getPhysicsManager();
        this.events = Locator.getEventManager();
        this.game = Locator.getGameManager();
    }

    public void create(String type, float x, float y, float rotation) {
        Entity e = entities.createEntity();
        create(e, type, x, y, rotation);
    }

    public void create(UUID uid, String type, float x, float y, float rotation) {

        if (uid == null) {
            create(type, x, y, rotation);
            return;
        }

        Entity e = entities.createEntity(uid);
        create(e, type, x, y, rotation);
    }

    private Entity create(Entity e, String type, float x, float y, float rotation) {

        if(type.equals("player")) {
            e = player(e, x, y, rotation);
        }

        if (e != null) {
            EntityCreatedEvent event = new EntityCreatedEvent();
            event.uid = entities.getUUID(e);
            event.type = type;
            event.x = x;
            event.y = y;
            event.rotation = rotation;
            events.queue(event);
        }

        return e;
    }

    private Entity player(Entity e, float x, float y, float angle) {

        // Physics component
        CharacterComponent pc = entities.createComponent(CharacterComponent.class);

        float stepHeight = 1.0f;
        float jumpSpeed = 0.1f;
        float maxJumpHeight = 2.0f;
        float maxSlope = 0.78f;

        btCollisionShape collisionShape = new btCapsuleShape(.25f, 1f);

        Character character = new Character(collisionShape);
        character.setStepHeight(stepHeight);
        character.setJumpSpeed(jumpSpeed);
        character.setMaxJumpHeight(maxJumpHeight);
        character.setMaxSlope(maxSlope);
        character.setTransform(x, y, angle);

        physics.addBody(character);

        pc.character = character;

        e.add(pc);

        // player keyboard controller
        ControllerComponent cc = entities.createComponent(ControllerComponent.class);
        cc.snapshot = new CharacterControllerSnapshot();

        if (entities.getUUID(e).equals(game.getLocalPlayerUid())) {
            cc.controller = new KeyboardController();
            Gdx.input.setInputProcessor((InputProcessor) cc.controller);

            // follow camera for local player
            CameraComponent cameraComponent = entities.createComponent(CameraComponent.class);
            e.add(cameraComponent);

            System.out.println("local player " + entities.getUUID(e));
        }
        else {
            cc.controller = new NetworkController();
            System.out.println("network player " + entities.getUUID(e));
        }

        e.add(cc);

        // player run speed
        RunSpeedComponent rc = entities.createComponent(RunSpeedComponent.class);
        rc.runSpeed = 750;
        e.add(rc);


        // Player animation component
        // we want to get a texture pack of the animations
        AnimatedSpriteComponent animationComponent = entities.createComponent(AnimatedSpriteComponent.class);
        //animationComponent.sprite = new AnimatedSprite(new Animation());
        // https://www.youtube.com/watch?v=SVyYvi0I6Bc
        // maybe we can alter the animated sprite class to accept multiple named animations
        // just write your own simple animation class
        // we almost want a state object where we can pass in a named state and a corresponding view
        // there should be states and transitions
        // either a pair of states which form a from to transition
        // or simple on exit state on enter state

        // there should be a simple text bassed format which can be loaded for these entities

        // the format should specify the states and entity can be in and the corresponding view

        // there should be a messaging system so that entities can handle messages
        // maybe use the GDX AI classes

        // entity states should be based on events and controller input

        // the view / animated sprite component or system should subscribe to state change events

        entities.addEntity(e);

        return e;
    }

}
