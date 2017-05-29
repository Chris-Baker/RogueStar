package com.base2.roguestar.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.base2.roguestar.events.Event;
import com.base2.roguestar.events.EventSubscriber;
import com.base2.roguestar.events.messages.UnverifiedPhysicsBodySnapshotEvent;
import com.base2.roguestar.events.messages.VerifiedPhysicsBodySnapshotEvent;
import com.base2.roguestar.utils.Config;

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

    private final Map<UUID, PhysicsBodySnapshot> verifiedSnapshots = new HashMap<UUID, PhysicsBodySnapshot>();
    private final Map<UUID, Array<PhysicsBodySnapshot>> unverifiedSnapshots = new HashMap<UUID, Array<PhysicsBodySnapshot>>();

    public void init() {

        world = new World(new Vector2(0, -25.0f), true);
        world.setContactListener(new CollisionHandler());
        deathRow.clear();
    }

    public void preUpdate() {
        // save a snapshot for every physics entity
        //previousFrame.px = simulation.px;
        //previousFrame.py = simulation.py;
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
        // resolve verified and unverified updates
        //				SimulationSnapshot updateDelta = new SimulationSnapshot();
//				updateDelta.timestamp = TimeUtils.nanoTime();
//				updateDelta.px = simulation.px - previousFrame.px;
//				updateDelta.py = simulation.py - previousFrame.py;

//				if (updateDelta.px != 0) {
//					unverifiedUpdates.add(updateDelta);
//				}
//
//				// insert verified updates and reapply unverified updates
//				if (verifiedUpdates.size > 0) {
//					// get the timestamp of the verified update
//					verifiedUpdate = verifiedUpdates.pop();
//					verifiedUpdates.clear();
//
//					int index = 0;
//					while (index < unverifiedUpdates.size) {
//						SimulationSnapshot unverifiedUpdate = unverifiedUpdates.get(index);
//
//						if (unverifiedUpdate.timestamp <= (verifiedUpdate.timestamp) - network.getPing() + network.getServerTimeAdjustment()) {
//							unverifiedUpdates.removeIndex(index);
//						}
//						else {
//							verifiedUpdate.px += unverifiedUpdate.px;
//							verifiedUpdate.py += unverifiedUpdate.py;
//							index += 1;
//						}
//					}
//
//					// apply that to the simulation for rendering
//					simulation.px = verifiedUpdate.px;
//					simulation.py = verifiedUpdate.py;
//
//				}
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
        else if (event instanceof UnverifiedPhysicsBodySnapshotEvent) {
            UnverifiedPhysicsBodySnapshotEvent unverifiedPhysicsBodySnapshotEvent = (UnverifiedPhysicsBodySnapshotEvent)event;
            PhysicsBodySnapshot snapshot = unverifiedPhysicsBodySnapshotEvent.snapshot;
            UUID uid = snapshot.getUid();

            if (!unverifiedSnapshots.containsKey(snapshot.getUid())) {
                unverifiedSnapshots.put(uid, new Array<PhysicsBodySnapshot>());
            }
            unverifiedSnapshots.get(uid).add(snapshot);
        }
    }
}
