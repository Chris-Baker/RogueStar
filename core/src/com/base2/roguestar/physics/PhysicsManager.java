package com.base2.roguestar.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.entities.components.CharacterComponent;
import com.base2.roguestar.events.Event;
import com.base2.roguestar.events.EventSubscriber;
import com.base2.roguestar.events.messages.VerifiedPhysicsBodySnapshotEvent;
import com.base2.roguestar.phys2d.AABB;
import com.base2.roguestar.phys2d.PhysBody;
import com.base2.roguestar.phys2d.PhysFixture;
import com.base2.roguestar.phys2d.PhysWorld;
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

    private PhysWorld physWorld;
    private World world;
    private final Array<Body> deathRow = new Array<Body>();
    final Array<Fixture> fixtures = new Array<Fixture>();

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
        physWorld = new PhysWorld();
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

        // collisions for phys2D using box2D AABB
        for (Entity entity: entities.getEntitiesFor(Family.all(CharacterComponent.class).get())) {

            UUID uid = entities.getUUID(entity);
            CharacterComponent cc = physicsMapper.get(entity);
            Body body = cc.body;
            PhysBody physBody = cc.physBody;

            // get our bounding box from the phys2D body so that we can query the Box2D AABB
            // and find any overlapping fixtures
            AABB aabb = physBody.getAABB();
            Array<Fixture> fixtures = getObjectsInRange(aabb.getMinX(), aabb.getMinY(), aabb.getMaxX(), aabb.getMaxY());

            for (int i = 0, n = fixtures.size; i < n; i += 1) {

                Fixture fixture = fixtures.get(i);

                if (!body.getFixtureList().contains(fixture, false)) {

                    if (fixture.getUserData() instanceof PhysFixture) {
                        // get phys fixture from the box2d fixture
                        PhysFixture other = (PhysFixture) fixture.getUserData();

                        // check for any overlaps
                        for (int j = 0, m = physBody.getFixtures().size; j < m; j += 1) {
                            PhysFixture physFixture = physBody.getFixtures().get(j);
                            physWorld.overlaps(physFixture, other);
                        }
                    }
                }
            }
        }
    }

    public void postUpdate() {

        for (Entity entity: entities.getEntitiesFor(Family.all(CharacterComponent.class).get())) {

            UUID uid = entities.getUUID(entity);
            CharacterComponent cc = physicsMapper.get(entity);
            Body body = cc.body;
            PhysBody physBody = cc.physBody;

            // create an unverified snapshot delta for this frame
            if (body.isAwake() || true) {
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
                        }
                        else {
                            verifiedSnapshot.applyDelta(unverifiedSnapshot);
                            index += 1;
                        }
                    }
                }

                // update our body with the state form the server
                body.setTransform(body.getPosition().lerp(new Vector2(verifiedSnapshot.getX(), verifiedSnapshot.getY()), 0.5f), verifiedSnapshot.getAngle());
                body.setLinearVelocity(verifiedSnapshot.getVX(), verifiedSnapshot.getVY());
                body.setAngularVelocity(verifiedSnapshot.getAngularVelocity());

                // update our phys body to match our box2d body
                physBody.setPosition(body.getPosition().x, body.getPosition().y);
            }
        }
    }

    private Array<Fixture> getObjectsInRange(float minX, float minY, float maxX, float maxY) {
        fixtures.clear();
        world.QueryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                fixtures.add(fixture);
                return true;
            }
        }, minX, minY, maxX, maxY);
        return fixtures;
    }

    public void removeBody(Body b) {
        deathRow.add(b);
    }

    public World getWorld() {
        return this.world;
    }

    public PhysWorld getPhysWorld() {
        return physWorld;
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
    }

    public PhysicsBodySnapshot getPreviousSnapshot(UUID uid) {
        return this.previousFrame.get(uid);
    }
}
