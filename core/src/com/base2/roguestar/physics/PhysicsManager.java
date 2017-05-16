package com.base2.roguestar.physics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.base2.roguestar.utils.Config;

/**
 * Created by Chris on 28/03/2016.
 */
public class PhysicsManager {

    // for fixed time step simulation
    private float accum = 0;
    private int iterations = 0;
    private final int VELOCITY_ITERATIONS = 8;
    private final int POSITION_ITERATIONS = 3;

    public World world;
    private final Array<Body> deathRow = new Array<Body>();

    private Box2DDebugRenderer debugRenderer;
    private final Matrix4 combined = new Matrix4();

    public void init() {

        world = new World(new Vector2(0, -25.0f), true);
        world.setContactListener(new CollisionHandler());
        //debugRenderer = new Box2DDebugRenderer();
        deathRow.clear();
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

    public void debugRender(OrthographicCamera camera) {
        // the debug renderer and physics world work in meters, the camera matrix is in pixels
        // we need to scale the camera matrix by the pixels per meter value to make the scales
        // match and render the debug draw correctly over the tiled maps.
        combined.set(camera.combined).scl(Config.PIXELS_PER_METER);
        //combined.set(camera.combined);
        debugRenderer.render(world, combined);
    }

    public void removeBody(Body b) {
        deathRow.add(b);
    }

    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
    }

}
