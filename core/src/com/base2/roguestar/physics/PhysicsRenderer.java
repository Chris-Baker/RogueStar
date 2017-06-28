package com.base2.roguestar.physics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.base2.roguestar.phys2d.PhysBody;
import com.base2.roguestar.phys2d.PhysFixture;
import com.base2.roguestar.utils.Config;

/**
 * Created by Chris on 28/03/2016.
 */
public class PhysicsRenderer {

    private Box2DDebugRenderer debugRenderer;
    private final Matrix4 combined = new Matrix4();

    public void init() {
        debugRenderer = new Box2DDebugRenderer();
    }

    public void render(World world, OrthographicCamera camera) {
        // the debug renderer and physics world work in meters, the camera matrix is in pixels
        // we need to scale the camera matrix by the pixels per meter value to make the scales
        // match and render the debug draw correctly over the tiled maps.
        combined.set(camera.combined).scl(Config.PIXELS_PER_METER);
        debugRenderer.render(world, combined);
    }

    public void dispose() {
        debugRenderer.dispose();
    }

}
