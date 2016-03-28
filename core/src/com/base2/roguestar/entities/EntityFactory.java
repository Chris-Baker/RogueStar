package com.base2.roguestar.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.*;
import com.base2.roguestar.entities.components.PhysicsComponent;
import com.base2.roguestar.utils.Config;

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

        PhysicsComponent pc = new PhysicsComponent();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, 0);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 2.5f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.isSensor = false;
        fixtureDef.shape = shape;

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        body.setTransform(x, y, angle);

        pc.body = body;
        e.add(pc);

        return e;
    }

}
