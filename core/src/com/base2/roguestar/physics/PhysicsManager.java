package com.base2.roguestar.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.entities.components.CharacterComponent;
import com.base2.roguestar.events.Event;
import com.base2.roguestar.events.EventSubscriber;
import com.base2.roguestar.events.messages.UnverifiedPhysicsBodySnapshotEvent;
import com.base2.roguestar.events.messages.VerifiedPhysicsBodySnapshotEvent;
import com.base2.roguestar.utils.Config;
import com.base2.roguestar.utils.Locator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Chris on 28/03/2016.
 */
public class PhysicsManager implements EventSubscriber {

    // for fixed time step simulation
    private float accum = 0;
    private int iterations = 0;
    private final int VELOCITY_ITERATIONS = 8;
    private final int POSITION_ITERATIONS = 3;

    private World world;
    private final Array<Body> deathRow = new Array<Body>();

    private final Map<UUID, PhysicsBodySnapshot> previousFrame = new HashMap<UUID, PhysicsBodySnapshot>();
    private final Map<UUID, PhysicsBodySnapshot> verifiedSnapshots = new HashMap<UUID, PhysicsBodySnapshot>();
    private final Map<UUID, Array<PhysicsBodySnapshot>> unverifiedSnapshots = new HashMap<UUID, Array<PhysicsBodySnapshot>>();

    private EntityManager entities;
    private ComponentMapper<CharacterComponent> physicsMapper;

    public void init() {
        world = new World(new Vector2(0, -25.0f), true);
        world.setContactListener(new CollisionHandler());
        deathRow.clear();
        physicsMapper = ComponentMapper.getFor(CharacterComponent.class);
        entities = Locator.getEntityManager();
    }

    public void preUpdate() {

        // clear the frame store
        previousFrame.clear();

        // save a snapshot for every physics entity
        for (Entity entity: entities.getEntitiesFor(Family.all(CharacterComponent.class).get())) {
            CharacterComponent cc = physicsMapper.get(entity);
            Body body = cc.body;
            PhysicsBodySnapshot snapshot = new PhysicsBodySnapshot(body, entities.getUUID(entity));
            previousFrame.put(snapshot.getUid(), snapshot);
        }
    }

    public void update(float delta) {

        // remove any bodies from the world flagged for removal
        for (Body b: deathRow) {
            world.destroyBody(b);
        }
        deathRow.clear();

        // step the physics simulation
        accum += delta;
        iterations = 0;
        while (accum > Config.PHYSICS_TIME_STEP && iterations < Config.MAX_UPDATE_ITERATIONS) {
            world.step(Config.PHYSICS_TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accum -= Config.PHYSICS_TIME_STEP;
            iterations++;
        }
    }

    public void postUpdate() {

        for (Entity entity: entities.getEntitiesFor(Family.all(CharacterComponent.class).get())) {

            UUID uid = entities.getUUID(entity);
            Body body = physicsMapper.get(entity).body;

            if (body.isAwake()) {
                // generate unverified snapshots based on this physics update
                PhysicsBodySnapshot unverifiedSnapshot = new PhysicsBodySnapshot(body, uid).getDelta(previousFrame.get(uid));

                if (!unverifiedSnapshots.containsKey(uid)) {
                    unverifiedSnapshots.put(uid, new Array<PhysicsBodySnapshot>());
                }
                unverifiedSnapshots.get(uid).add(unverifiedSnapshot);
            }

            // resolve any verified snapshots we have received
            if (verifiedSnapshots.containsKey(uid)) {

                PhysicsBodySnapshot verifiedSnapshot = verifiedSnapshots.remove(uid);

                // if we have any client side state updates not verified by the server then we can apply
                // those deltas to the verified state given to us by the server
                if (unverifiedSnapshots.containsKey(uid)) {
                    int index = 0;
                    while (index < unverifiedSnapshots.get(uid).size) {
                        PhysicsBodySnapshot unverifiedSnapshot = unverifiedSnapshots.get(uid).get(index);

                        if (unverifiedSnapshot.getTimestamp() <= verifiedSnapshot.getTimestamp()) {
                            unverifiedSnapshots.get(uid).removeIndex(index);
                            System.out.println("Dropped old unverified update");
                        }
                        else {
                            //verifiedSnapshot.applyDelta(unverifiedSnapshot);
                            System.out.println("x: " + unverifiedSnapshot.getX() + ", y: " + unverifiedSnapshot.getY());
                            index += 1;
                        }
                    }
                }

                // update our body with the state form the server
                body.setTransform(verifiedSnapshot.getX(), verifiedSnapshot.getY(), verifiedSnapshot.getAngle());
                body.setLinearVelocity(verifiedSnapshot.getVX(), verifiedSnapshot.getVY());
                body.setAngularVelocity(verifiedSnapshot.getAngularVelocity());
            }
        }
    }

    public void removeBody(Body b) {
        deathRow.add(b);
    }

    public World getWorld() {
        return this.world;
    }

    public void dispose() {
        world.dispose();
    }

    @Override
    public void handleEvent(Event event) {

        if (event instanceof VerifiedPhysicsBodySnapshotEvent) {
            VerifiedPhysicsBodySnapshotEvent verifiedPhysicsBodySnapshotEvent = (VerifiedPhysicsBodySnapshotEvent)event;
            PhysicsBodySnapshot snapshot = verifiedPhysicsBodySnapshotEvent.snapshot;
            verifiedSnapshots.put(snapshot.getUid(), snapshot);
        }
//        else if (event instanceof UnverifiedPhysicsBodySnapshotEvent) {
//            UnverifiedPhysicsBodySnapshotEvent unverifiedPhysicsBodySnapshotEvent = (UnverifiedPhysicsBodySnapshotEvent)event;
//            PhysicsBodySnapshot snapshot = unverifiedPhysicsBodySnapshotEvent.snapshot;
//            UUID uid = snapshot.getUid();
//
//            if (!unverifiedSnapshots.containsKey(uid)) {
//                unverifiedSnapshots.put(uid, new Array<PhysicsBodySnapshot>());
//            }
//            unverifiedSnapshots.get(uid).add(snapshot);
//        }
    }
}
