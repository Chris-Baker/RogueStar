package com.base2.roguestar.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.base2.roguestar.controllers.KeyboardController;
import com.base2.roguestar.entities.components.*;

/**
 * Created by Chris on 28/03/2016.
 */
public class EntityFactory {

    public static void create(PooledEngine engine, World world, String type, float x, float y, float angle) {

        if(type.equals("player")) {
            player(engine, world, x, y, angle);
        }

    }

    private static Entity player(PooledEngine engine, World world, float x, float y, float angle) {

        Entity e = engine.createEntity();

        // Physics component
        CharacterComponent pc = engine.createComponent(CharacterComponent.class);

//        BodyDef bodyDef = new BodyDef();
//        bodyDef.type = BodyDef.BodyType.DynamicBody;
//        bodyDef.position.set(0, 0);
//
//        CircleShape shape = new CircleShape();
//        shape.setRadius(0.5f);
//
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.density = 2.5f;
//        fixtureDef.friction = 0.0f;
//        fixtureDef.restitution = 0.0f;
//        fixtureDef.isSensor = false;
//        fixtureDef.shape = shape;
//
//        Body body = world.createBody(bodyDef);
//        body.createFixture(fixtureDef);
//
//        body.setTransform(x, y, angle);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.fixedRotation = true;
        Body body = world.createBody(def);

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
        ControllerComponent cc = engine.createComponent(ControllerComponent.class);
        cc.controller = new KeyboardController();

        // register the controller as an input listener
        Gdx.input.setInputProcessor(cc.controller);

        e.add(cc);

        // player run speed
        RunSpeedComponent rc = engine.createComponent(RunSpeedComponent.class);
        rc.runSpeed = 750;
        e.add(rc);

        // follow camera
        CameraComponent cameraComponent = engine.createComponent(CameraComponent.class);
        e.add(cameraComponent);


        // Player animation component
        // we want to get a texture pack of the animations
        AnimatedSpriteComponent animationComponent = engine.createComponent(AnimatedSpriteComponent.class);
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

        engine.addEntity(e);

        return e;
    }

}
