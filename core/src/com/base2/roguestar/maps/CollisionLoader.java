package com.base2.roguestar.maps;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.base2.roguestar.phys2d.PhysBody;
import com.base2.roguestar.phys2d.PhysWorld;
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
            Shape2D shape2D;

            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectangle = (RectangleMapObject) object;
                shape = getBox2dRectangle(rectangle);
                shape2D = getRectangle(rectangle);

            } else if (object instanceof PolygonMapObject) {
                PolygonMapObject polygon = (PolygonMapObject) object;
                shape = getBox2dPolygon(polygon);
                shape2D = getPolygon(polygon);

            } else if (object instanceof PolylineMapObject) {
                PolylineMapObject polyline = (PolylineMapObject) object;
                shape = getBox2dPolyline(polyline);
                shape2D = getPolyline(polyline);

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
            physBody.createFixture(shape2D);

            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.StaticBody;
            Body body = physicsWorld.createBody(bd);
            Fixture fixture = body.createFixture(shape, 1);
            body.setUserData(physBody);

            shape.dispose();
        }
    }

    private static Rectangle getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = new Rectangle(rectangleObject.getRectangle());
        rectangle.set(rectangle.x / Config.PIXELS_PER_METER,
                rectangle.y / Config.PIXELS_PER_METER,
                rectangle.width / Config.PIXELS_PER_METER,
                rectangle.height / Config.PIXELS_PER_METER);
        return rectangle;
    }

    private static Circle getCircle(EllipseMapObject ellipseObject) {
        Ellipse ellipse = ellipseObject.getEllipse();
        Circle circle = new Circle();
        circle.set((ellipse.x + ellipse.width * 0.5f) / Config.PIXELS_PER_METER, (ellipse.y + ellipse.width * 0.5f) / Config.PIXELS_PER_METER, ellipse.width / 2 / Config.PIXELS_PER_METER);
        return circle;
    }

    private static Polygon getPolygon(PolygonMapObject polygonObject) {
        Polygon polygon = new Polygon(polygonObject.getPolygon().getTransformedVertices());
        polygon.setScale(1 / Config.PIXELS_PER_METER, 1 / Config.PIXELS_PER_METER);
        polygon.setPosition(polygon.getX() / Config.PIXELS_PER_METER, polygon.getY() / Config.PIXELS_PER_METER);
        return polygon;
    }

    private static Polyline getPolyline(PolylineMapObject polylineObject) {
        Polyline polyline = new Polyline(polylineObject.getPolyline().getTransformedVertices());
        polyline.setScale(1 / Config.PIXELS_PER_METER, 1 / Config.PIXELS_PER_METER);
        polyline.setPosition(polyline.getX() / Config.PIXELS_PER_METER, polyline.getY() / Config.PIXELS_PER_METER);
        return polyline;
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

    private static ChainShape getBox2dPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / Config.PIXELS_PER_METER;
            worldVertices[i].y = vertices[i * 2 + 1] / Config.PIXELS_PER_METER;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }

}
