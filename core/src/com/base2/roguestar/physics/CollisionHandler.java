package com.base2.roguestar.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.base2.roguestar.entities.components.CharacterComponent;

/**
 * Created by Chris on 08/05/2017.
 */
public class CollisionHandler implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        //System.out.println("contact started");

        // get the two bodies
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        this.dispatch(bodyA, bodyB, contact);
        this.dispatch(bodyB, bodyA, contact);
    }

    private void dispatch(Body body, Body other, Contact contact) {

        if (body.getUserData() == null) {
            return;
        }

        // get the entity
        Entity e = (Entity) body.getUserData();

        // see if we have a character component
        CharacterComponent cc = e.getComponent(CharacterComponent.class);

        if (cc != null) {

            // figure out the angle of the contact so we don't think we are on the floor when we hit a wall
            if (contact.getWorldManifold().getNormal().y > 0) {
                cc.isGrounded = true;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        //System.out.println("contact ended");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}