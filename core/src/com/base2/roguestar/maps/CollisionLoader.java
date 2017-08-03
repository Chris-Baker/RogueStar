package com.base2.roguestar.maps;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.base2.roguestar.physics.Body;
import com.base2.roguestar.physics.PhysicsManager;
import com.base2.roguestar.utils.Config;

public class CollisionLoader {

    public static void load(TiledMap map, PhysicsManager physics) {
        parseObjectLayer(map.getLayers().get("collisions_1").getObjects(), physics);
    }

    private static void parseObjectLayer(MapObjects objects, PhysicsManager physics) {

        float scale = 1 / Config.PIXELS_PER_METER;
        Vector2 translation = new Vector2();
        Vector2 offset = new Vector2();

        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) {
                continue;
            }

            float angle = 0;
            btCollisionShape shape;

            if (object instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                float halfWidth = rectangle.width * 0.5f;
                float halfHeight = rectangle.height * 0.5f;

                if (object.getProperties().get("rotation", Float.class) != null) {
                    angle = object.getProperties().get("rotation", Float.class);
                }

                shape = new btBoxShape(new Vector3(halfWidth, halfHeight, 8.0f).scl(scale));
                translation.set(rectangle.x, rectangle.y + (halfHeight * 2)).scl(scale);
                offset.set(halfWidth, -halfHeight).rotate(-angle).scl(scale);

            } else if (object instanceof CircleMapObject) {
                Circle circle = ((CircleMapObject) object).getCircle();
                shape = new btSphereShape(circle.radius / Config.PIXELS_PER_METER);
                translation.set(circle.x, circle.y).scl(scale);
                offset.set(0, 0);
            } else {
                continue;
            }

            Body body = new Body(shape);
            body.setTransform(translation, angle);
            body.translate(offset);
            physics.addBody(body);

            System.out.println(offset);
        }
    }
}
