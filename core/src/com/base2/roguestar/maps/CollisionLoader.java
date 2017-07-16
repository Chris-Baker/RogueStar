package com.base2.roguestar.maps;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.base2.roguestar.physics.PhysicsManager;
import com.base2.roguestar.utils.Config;

public class CollisionLoader {

    public static void load(TiledMap map, PhysicsManager physics) {
        parseObjectLayer(map.getLayers().get("collisions_1").getObjects(), physics);
    }

    private static void parseObjectLayer(MapObjects objects, PhysicsManager physics) {

        Vector3 translation = new Vector3();

        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) {
                continue;
            }

            btCollisionShape shape;

            if (object instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                shape = new btBoxShape(new Vector3((rectangle.width * 0.5f) / Config.PIXELS_PER_METER,
                        (rectangle.height * 0.5f) / Config.PIXELS_PER_METER, 0.5f));
                translation.set((rectangle.x + rectangle.width * 0.5f) / Config.PIXELS_PER_METER,
                        (rectangle.y + rectangle.height * 0.5f) / Config.PIXELS_PER_METER, 0f);

            } else if (object instanceof CircleMapObject) {
                Circle circle = ((CircleMapObject) object).getCircle();
                shape = new btSphereShape(circle.radius / Config.PIXELS_PER_METER);
                translation.set(circle.x / Config.PIXELS_PER_METER, circle.y / Config.PIXELS_PER_METER, 0);

            } else {
                continue;
            }

            btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(0f, null, shape, Vector3.Zero);
            btRigidBody body = new btRigidBody(info);
            body.translate(translation);
            physics.addBody(body);
        }
    }

}
