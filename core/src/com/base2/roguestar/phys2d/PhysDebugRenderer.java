package com.base2.roguestar.phys2d;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;

public class PhysDebugRenderer {

    private ShapeRenderer shapeRenderer;

    public void init() {
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(PhysWorld world, OrthographicCamera camera, float scale) {

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (PhysBody body: world.getBodies()) {

            // draw all our fixtures
            shapeRenderer.setColor(0, 1, 0, 1);
            for (PhysFixture physFixture: body.getFixtures()) {
                Shape2D shape = physFixture.getShape();

                if (shape instanceof Rectangle) {
                    Rectangle rectangle = (Rectangle)shape;
                    shapeRenderer.rect(
                            physFixture.getX() * scale,
                            physFixture.getY() * scale,
                            rectangle.width  * scale,
                            rectangle.height * scale);
                }
                else if (shape instanceof Circle) {
                    Circle circle = (Circle)shape;
                    shapeRenderer.circle(
                            physFixture.getX() * scale,
                            physFixture.getY() * scale,
                            circle.radius * scale);
                }
                else if (shape instanceof Polygon) {
                    Polygon polygon = new Polygon(((Polygon)shape).getTransformedVertices());
                    polygon.setScale(scale, scale);
                    polygon.setPosition(physFixture.getX() * scale, physFixture.getY() * scale);
                    shapeRenderer.polygon(polygon.getTransformedVertices());
                }
                else if (shape instanceof Polyline) {
                    Polyline polyline = new Polyline(((Polyline)shape).getTransformedVertices());
                    polyline.setScale(scale, scale);
                    polyline.setPosition(physFixture.getX() * scale, physFixture.getY() * scale);
                    shapeRenderer.polygon(polyline.getTransformedVertices());
                }
            }

            // draw our AABB
            AABB aabb = body.getAABB();
            shapeRenderer.setColor(1, 0.5f, 0.5f, 1);
            shapeRenderer.rect(
                    aabb.getMinX() * scale,
                    aabb.getMinY() * scale,
                    (aabb.getMaxX() - aabb.getMinX())  * scale,
                    (aabb.getMaxY() - aabb.getMinY()) * scale);
        }
        shapeRenderer.end();
    }
}
