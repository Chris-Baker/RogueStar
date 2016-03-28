package com.base2.roguestar.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Chris on 27/03/2016.
 */
public class CollisionLoader {

    public static void load(TiledMap map, World physicsWorld) {
        parseObjectLayer(map.getLayers().get("collisions_1").getObjects(), physicsWorld);
    }

    private static void parseObjectLayer(MapObjects objects, World physicsWorld) {

        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) {
                continue;
            }

            Shape shape;

            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject) object);
            } else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject) object);
            } else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject) object);
            } else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject) object);
            } else {
                continue;
            }

            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.StaticBody;
            Body body = physicsWorld.createBody(bd);
            body.createFixture(shape, 1);

            //bodies.add(body);


            shape.dispose();
        }
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
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

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / Config.PIXELS_PER_METER);
        circleShape.setPosition(new Vector2(circle.x / Config.PIXELS_PER_METER, circle.y / Config.PIXELS_PER_METER));
        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            System.out.println(vertices[i]);
            worldVertices[i] = vertices[i] / Config.PIXELS_PER_METER;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
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
