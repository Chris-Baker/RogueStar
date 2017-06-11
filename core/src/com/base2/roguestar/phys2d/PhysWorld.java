package com.base2.roguestar.phys2d;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class PhysWorld {

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

            // check if these two fixtures overlap
            if (fixture.overlaps(other)) {

                // create the contact
                PhysContact contact = new PhysContact(fixture, other);

                // register a new contact
                this.contacts.put(hash, contact);

                // resolve contact
                resolve(contact);
            }
        }
    }

    private void resolve(PhysContact contact) {
        System.out.println("resolve contact");
        this.contacts.remove(contact.hashCode());
    }
}
