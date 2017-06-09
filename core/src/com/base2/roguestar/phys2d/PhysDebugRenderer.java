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
                            physFixture.getX() * Config.PIXELS_PER_METER,
                            physFixture.getY() * Config.PIXELS_PER_METER,
                            rectangle.width  * Config.PIXELS_PER_METER,
                            rectangle.height * Config.PIXELS_PER_METER);
                }
                else if (shape instanceof Circle) {
                    Circle circle = (Circle)shape;
                    shapeRenderer.circle(
                            physFixture.getX() * Config.PIXELS_PER_METER,
                            physFixture.getY() * Config.PIXELS_PER_METER,
                            circle.radius * Config.PIXELS_PER_METER);
                }
                else if (shape instanceof Polygon) {
                    Polygon polygon = new Polygon(((Polygon)shape).getTransformedVertices());
                    polygon.scale(Config.PIXELS_PER_METER);
                    polygon.setPosition(physFixture.getX() * Config.PIXELS_PER_METER, physFixture.getY() * Config.PIXELS_PER_METER);
                    shapeRenderer.polygon(polygon.getTransformedVertices());

                }
                else if (shape instanceof Polyline) {
                    Polyline polyline = new Polyline(((Polyline)shape).getTransformedVertices());
                    polyline.scale(Config.PIXELS_PER_METER);
                    polyline.setPosition(physFixture.getX() * Config.PIXELS_PER_METER, physFixture.getY() * Config.PIXELS_PER_METER);
                    shapeRenderer.polygon(polyline.getTransformedVertices());
                }
            }
        }
        shapeRenderer.end();
    }
}
