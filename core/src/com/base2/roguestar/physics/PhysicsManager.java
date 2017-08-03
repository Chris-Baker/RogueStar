package com.base2.roguestar.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.utils.Array;
import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.entities.components.CharacterComponent;
import com.base2.roguestar.events.Event;
import com.base2.roguestar.events.EventSubscriber;
import com.base2.roguestar.events.messages.VerifiedPhysicsBodySnapshotEvent;
import com.base2.roguestar.utils.Config;
import com.base2.roguestar.utils.Locator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PhysicsManager implements EventSubscriber {

    // for fixed time step simulation
    private float accum = 0;
    private int iterations = 0;

    private btCollisionConfiguration collisionConfig;
    private btDispatcher dispatcher;
    private GameContactListener contactListener;
    private btBroadphaseInterface broadphase;
    private btCollisionWorld world;

    private final Array<Body> bodies = new Array<Body>();
    private final Array<Body> deathRow = new Array<Body>();

    private final Map<UUID, PhysicsBodySnapshot> previousFrame = new HashMap<UUID, PhysicsBodySnapshot>();
    private final Map<UUID, PhysicsBodySnapshot> verifiedSnapshots = new HashMap<UUID, PhysicsBodySnapshot>();
    private final Map<UUID, Array<PhysicsBodySnapshot>> unverifiedSnapshots = new HashMap<UUID, Array<PhysicsBodySnapshot>>();

    private EntityManager entities;
    private ComponentMapper<CharacterComponent> physicsMapper;

    public void init() {

        Bullet.init();

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        world = new btCollisionWorld(dispatcher, broadphase, collisionConfig);
        contactListener = new GameContactListener();

        deathRow.clear();
        physicsMapper = ComponentMapper.getFor(CharacterComponent.class);
        entities = Locator.getEntityManager();
    }

    public void preUpdate() {

//        // clear the frame store
//        previousFrame.clear();
//
//        // save a snapshot for every physics entity
//        for (Entity entity: entities.getEntitiesFor(Family.all(CharacterComponent.class).get())) {
//            CharacterComponent cc = physicsMapper.get(entity);
//             body = cc.character;
//            PhysicsBodySnapshot snapshot = new PhysicsBodySnapshot(body, entities.getUUID(entity));
//            previousFrame.put(snapshot.getUid(), snapshot);
//        }
    }

    public void update(float delta) {

        // remove any bodies from the world flagged for removal
        for (Body b: deathRow) {
            world.removeCollisionObject(b.getCollisionObject());
            bodies.removeValue(b, false);
            b.dispose();
        }
        deathRow.clear();

        // step the physics simulation
        accum += delta;
        iterations = 0;
        while (accum > Config.PHYSICS_TIME_STEP && iterations < Config.MAX_UPDATE_ITERATIONS) {
            // update bodies here
            world.performDiscreteCollisionDetection();
            accum -= Config.PHYSICS_TIME_STEP;
            iterations++;
        }
    }

    public void postUpdate() {

//        for (Entity entity: entities.getEntitiesFor(Family.all(CharacterComponent.class).get())) {
//
//            UUID uid = entities.getUUID(entity);
//            Body body = physicsMapper.get(entity).body;
//
//            // create an unverified snapshot delta for this frame
//            if (body.isAwake() || true) {
//                // generate unverified snapshots based on this physics update
//                PhysicsBodySnapshot unverifiedSnapshot = new PhysicsBodySnapshot(body, uid).getDelta(previousFrame.get(uid));
//
//                if (!unverifiedSnapshots.containsKey(uid)) {
//                    unverifiedSnapshots.put(uid, new Array<PhysicsBodySnapshot>());
//                }
//                unverifiedSnapshots.get(uid).add(unverifiedSnapshot);
//            }
//
//            // resolve any verified snapshots we have received
//            if (verifiedSnapshots.containsKey(uid)) {
//
//                PhysicsBodySnapshot verifiedSnapshot = verifiedSnapshots.remove(uid);
//
//                System.out.println("verified x: " + verifiedSnapshot.getX() + ", y: " + verifiedSnapshot.getY());
//
//                // if we have any client side state updates not verified by the server then we can apply
//                // those deltas to the verified state given to us by the server
//                if (unverifiedSnapshots.containsKey(uid)) {
//                    int index = 0;
//                    while (index < unverifiedSnapshots.get(uid).size) {
//                        PhysicsBodySnapshot unverifiedSnapshot = unverifiedSnapshots.get(uid).get(index);
//
//                        if (unverifiedSnapshot.getTimestamp() <= verifiedSnapshot.getTimestamp()) {
//                            unverifiedSnapshots.get(uid).removeIndex(index);
//                            //System.out.println("Dropped old unverified update");
//                        }
//                        else {
//                            verifiedSnapshot.applyDelta(unverifiedSnapshot);
////                            System.out.println("unverified time: " + unverifiedSnapshot.getTimestamp());
////                            System.out.println("verified time  : " + verifiedSnapshot.getTimestamp());
////                            System.out.println("now time       : " + TimeUtils.nanoTime());
////                            System.out.println("x: " + unverifiedSnapshot.getX() + ", y: " + unverifiedSnapshot.getY());
//                            index += 1;
//                        }
//                    }
//                }
//
//                // update our body with the state form the server
//                body.setTransform(verifiedSnapshot.getX(), verifiedSnapshot.getY(), verifiedSnapshot.getAngle());
//                body.setLinearVelocity(verifiedSnapshot.getVX(), verifiedSnapshot.getVY());
//                body.setAngularVelocity(verifiedSnapshot.getAngularVelocity());
//            }
//        }
    }

    public void addBody(Body body) {
        world.addCollisionObject(body.getCollisionObject());
        bodies.add(body);
    }

    public Body getBody(int index) {
        return bodies.get(index);
    }

    public int getBodyCount() {
        return bodies.size;
    }

    public void removeBody(Body b) {
        deathRow.add(b);
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
