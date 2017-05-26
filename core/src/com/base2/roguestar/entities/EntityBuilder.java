package com.base2.roguestar.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.base2.roguestar.controllers.KeyboardController;
import com.base2.roguestar.entities.components.*;
import com.base2.roguestar.events.EventManager;
import com.base2.roguestar.events.messages.EntityCreatedEvent;
import com.base2.roguestar.physics.PhysicsManager;
import com.base2.roguestar.utils.Locator;

import java.util.UUID;

public class EntityBuilder {

    EntityManager entities;
    PhysicsManager physics;
    EventManager events;

    public void init() {
        this.entities = Locator.getEntityManager();
        this.physics = Locator.getPhysicsManager();
        this.events = Locator.getEventManager();
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

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.fixedRotation = true;
        Body body = physics.getWorld().createBody(def);

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(0.5f, 1f);
        Fixture playerPhysicsFixture = body.createFixture(poly, 1);
        poly.dispose();

        CircleShape circle = new CircleShape();
        circle.setRadius(0.5f);
        circle.setPosition(new Vector2(0, -1f));
        Fixture playerSensorFixture = body.createFixture(circle, 0);
        circle.dispose();

        body.setBullet(true);

        body.setTransform(x, y, angle);

        body.setUserData(e);

        pc.body = body;
        pc.physicsFixture = playerPhysicsFixture;
        pc.sensorFixture = playerSensorFixture;
        e.add(pc);

        // player keyboard controller
        ControllerComponent cc = entities.createComponent(ControllerComponent.class);
        cc.controller = new KeyboardController();

        // register the controller as an input listener
        Gdx.input.setInputProcessor(cc.controller);

        e.add(cc);

        // player run speed
        RunSpeedComponent rc = entities.createComponent(RunSpeedComponent.class);
        rc.runSpeed = 750;
        e.add(rc);

        // follow camera
        CameraComponent cameraComponent = entities.createComponent(CameraComponent.class);
        e.add(cameraComponent);


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