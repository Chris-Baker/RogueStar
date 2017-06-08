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
    private ShapeRenderer shapeRenderer;
    private final Matrix4 combined = new Matrix4();
    private final Array<Body> bodies = new Array<Body>();

    public void init() {
        debugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();
    }

    public void render(World world, OrthographicCamera camera) {
        // the debug renderer and physics world work in meters, the camera matrix is in pixels
        // we need to scale the camera matrix by the pixels per meter value to make the scales
        // match and render the debug draw correctly over the tiled maps.
        combined.set(camera.combined).scl(Config.PIXELS_PER_METER);
        debugRenderer.render(world, combined);


        shapeRenderer.setProjectionMatrix(camera.combined);
        world.getBodies(bodies);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1);
        for (Body body: bodies) {
            Object object = body.getUserData();
            if (object != null && object instanceof PhysBody) {
                PhysBody physBody = (PhysBody)object;

                for (PhysFixture physFixture: physBody.getFixtures()) {
                    Shape2D shape = physFixture.getShape();

                    if (shape instanceof Rectangle) {
                        Rectangle rectangle = (Rectangle)shape;
                        shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                    }
                    else if (shape instanceof Circle) {
                        Circle circle = (Circle)shape;
                        shapeRenderer.circle(circle.x, circle.y, circle.radius);
                    }
                    else if (shape instanceof Polygon) {
                        Polygon polygon = (Polygon)shape;
                        shapeRenderer.polygon(polygon.getTransformedVertices());
                    }
                    else if (shape instanceof Polyline) {
                        Polyline polyline = (Polyline)shape;
                        shapeRenderer.polygon(polyline.getTransformedVertices());
                    }
                }
            }
        }
        shapeRenderer.end();
    }

    public void dispose() {
        debugRenderer.dispose();
    }

}
