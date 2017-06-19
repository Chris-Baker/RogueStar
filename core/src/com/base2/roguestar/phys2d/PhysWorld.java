package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class PhysWorld {

    // temp objects for collision repsonse
    private Vector2 newPosition = new Vector2();

    private IntMap<PhysContact> contacts = new IntMap<PhysContact>();
    private Array<PhysBody> bodies = new Array<PhysBody>();

    public Array<PhysBody> getBodies() {
        return bodies;
    }

    public PhysBody createBody() {
        PhysBody body = new PhysBody();
        this.bodies.add(body);
        return body;
    }

    public void overlaps(PhysFixture fixture, PhysFixture other) {

        int hash = fixture.hashCode() ^ other.hashCode();

        // check if there is already a contact
        if (!contacts.containsKey(hash)) {

            PhysContact contact = fixture.overlaps(other);

            // check if these two fixtures overlap
            if (contact != null) {

                // register a new contact
                this.contacts.put(hash, contact);

                // resolve contact
                resolve(contact);
            }
        }
    }

    private void resolve(PhysContact contact) {

        PhysBody bodyA = contact.getFixtureA().getBody();
        PhysBody bodyB = contact.getFixtureB().getBody();

        if (bodyA.getType() == PhysBodyType.STATIC && bodyB.getType() == PhysBodyType.STATIC) {
            return;
        }

        PhysBody body = null;

        if (bodyA.getType() == PhysBodyType.KINEMATIC) {
            body = bodyA;
        }
        else {
            body = bodyB;
        }

        // use the velocity and normal to calculate the direction
        float directionX = ((body.getVelocity().x > 0 && contact.getNormal().x > 0)
                            || (body.getVelocity().x < 0 && contact.getNormal().x < 0)) ? -1 : 1;

        float directionY = ((body.getVelocity().y > 0 && contact.getNormal().y > 0)
                || (body.getVelocity().y < 0 && contact.getNormal().y < 0)) ? -1 : 1;

        // calculate our adjusted positions
        newPosition.set(contact.getNormal());
        newPosition.x *= directionX;
        newPosition.y *= directionY;
        newPosition.scl(contact.getDepth());
        newPosition.add(body.getX(), body.getY());
        body.setPosition(newPosition);

        // update the box2D body which corresponds to this body
        Body b2dBody = (Body) body.getUserData();
        b2dBody.setTransform(body.getX(), body.getY(), b2dBody.getAngle());

        // this contact is now resolved
        this.contacts.remove(contact.hashCode());
    }
}
