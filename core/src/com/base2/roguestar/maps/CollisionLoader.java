package com.base2.roguestar.maps;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.base2.roguestar.phys2d.PhysBody;
import com.base2.roguestar.phys2d.PhysFixture;
import com.base2.roguestar.phys2d.PhysWorld;
import com.base2.roguestar.phys2d.ShapeFactory;
import com.base2.roguestar.utils.Config;

public class CollisionLoader {

    public static void load(TiledMap map, World physicsWorld, PhysWorld physWorld) {
        parseObjectLayer(map.getLayers().get("collisions_1").getObjects(), physicsWorld, physWorld);
    }

    private static void parseObjectLayer(MapObjects objects, World physicsWorld, PhysWorld physWorld) {

        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) {
                continue;
            }

            Shape shape;
            Polygon shape2D;

            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectangle = (RectangleMapObject) object;
                shape = getBox2dRectangle(rectangle);
                shape2D = getRectangle(rectangle);

            } else if (object instanceof PolygonMapObject) {
                PolygonMapObject polygon = (PolygonMapObject) object;
                shape = getBox2dPolygon(polygon);
                shape2D = getPolygon(polygon);

            } else if (object instanceof EllipseMapObject) {
                EllipseMapObject ellipse = (EllipseMapObject) object;

                if (ellipse.getEllipse().width == ellipse.getEllipse().height) {
                    shape = getBox2dCircle(ellipse);
                    shape2D = getCircle(ellipse);
                }
                else {
                    continue;
                }
            } else {
                System.out.println("Shape loader not found: " + object.getClass());
                continue;
            }

            PhysBody physBody = physWorld.createBody();
            PhysFixture physFixture = physBody.createFixture(shape2D);

            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.StaticBody;
            Body body = physicsWorld.createBody(bd);
            Fixture fixture = body.createFixture(shape, 1);
            fixture.setUserData(physFixture);
            body.setUserData(physBody);

            shape.dispose();
        }
    }

    private static Polygon getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = new Rectangle(rectangleObject.getRectangle());
        Polygon polygon = ShapeFactory.getRectangle(rectangle.width / Config.PIXELS_PER_METER, rectangle.height / Config.PIXELS_PER_METER);
        polygon.setPosition(rectangle.x / Config.PIXELS_PER_METER,rectangle.y / Config.PIXELS_PER_METER);
        return polygon;
    }

    private static Polygon getCircle(EllipseMapObject ellipseObject) {
        Ellipse ellipse = ellipseObject.getEllipse();

        Polygon polygon = ShapeFactory.getRegularPolygon(ellipse.width / 2 / Config.PIXELS_PER_METER, 6);
        polygon.setPosition((ellipse.x + ellipse.width * 0.5f) / Config.PIXELS_PER_METER,
                (ellipse.y + ellipse.width * 0.5f) / Config.PIXELS_PER_METER);

        return polygon;
    }

    private static Polygon getPolygon(PolygonMapObject polygonObject) {
        Polygon polygon = new Polygon(polygonObject.getPolygon().getTransformedVertices());
        polygon.setScale(1 / Config.PIXELS_PER_METER, 1 / Config.PIXELS_PER_METER);
        polygon.setPosition(polygon.getX() / Config.PIXELS_PER_METER, polygon.getY() / Config.PIXELS_PER_METER);
        return polygon;
    }

    private static PolygonShape getBox2dRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / Config.PIXELS_PER_METER,
                (rectangle.y + rectangle.height * 0.5f) / Config.PIXELS_PER_METER);
        polygon.setAsBox(rectangle.width * 0.5f / Config.PIXELS_PER_METER,
                rectangle.height * 0.5f / Config.PIXELS_PER_METER,
                size,
                0.0f);
        return polygon;
    }

    private static CircleShape getBox2dCircle(EllipseMapObject ellipseObject) {
        Ellipse ellipse = ellipseObject.getEllipse();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(ellipse.width / 2 / Config.PIXELS_PER_METER);
        circleShape.setPosition(new Vector2((ellipse.x + ellipse.width * 0.5f) / Config.PIXELS_PER_METER, (ellipse.y + ellipse.width * 0.5f) / Config.PIXELS_PER_METER));
        return circleShape;
    }

    private static PolygonShape getBox2dPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i] / Config.PIXELS_PER_METER;
        }

        polygon.set(worldVertices);
        return polygon;
    }

}
