package com.base2.roguestar.phys2d;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.base2.roguestar.utils.Config;

public class PhysDebugRenderer {

    private ShapeRenderer shapeRenderer;

    public void init() {
        shapeRenderer = new ShapeRenderer();
    }

    public void render(PhysWorld world, OrthographicCamera camera) {

        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1);
        for (PhysBody body: world.getBodies()) {
            for (PhysFixture physFixture: body.getFixtures()) {
                Shape2D shape = physFixture.getShape();

                if (shape instanceof Rectangle) {
                    Rectangle rectangle = (Rectangle)shape;
                    shapeRenderer.rect(
                            physFixture.getPosition().x * Config.PIXELS_PER_METER,
                            physFixture.getPosition().y * Config.PIXELS_PER_METER,
                            rectangle.width  * Config.PIXELS_PER_METER,
                            rectangle.height * Config.PIXELS_PER_METER);
                }
                else if (shape instanceof Circle) {
                    Circle circle = (Circle)shape;
                    shapeRenderer.circle(
                            physFixture.getPosition().x * Config.PIXELS_PER_METER,
                            physFixture.getPosition().y * Config.PIXELS_PER_METER,
                            circle.radius * Config.PIXELS_PER_METER);
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
        shapeRenderer.end();
    }
}
