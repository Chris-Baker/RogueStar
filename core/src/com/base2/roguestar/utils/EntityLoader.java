package com.base2.roguestar.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.base2.roguestar.entities.EntityFactory;
import com.base2.roguestar.entities.EntityManager;
import com.esotericsoftware.kryonet.Server;

/**
 * Created by Chris on 28/03/2016.
 */
public class EntityLoader {

    public static void load(TiledMap map, EntityManager entityManager, World world, Server server) {
        parseObjectLayer(map.getLayers().get("entities").getObjects(), entityManager, world, server);
    }

    private static void parseObjectLayer(MapObjects objects, EntityManager entityManager, World world, Server server) {

        for (MapObject object : objects) {

            if (!(object instanceof TiledMapTileMapObject)) {
                continue;
            }

            TiledMapTileMapObject tile = (TiledMapTileMapObject) object;
            MapProperties properties = tile.getProperties();



            // convert positions and rotation to box2d coordinates
            //float x = (tile.getX() + (Config.PIXELS_PER_METER / 2)) / Config.PIXELS_PER_METER;
            //float y = (tile.getY() + (Config.PIXELS_PER_METER / 2)) / Config.PIXELS_PER_METER;

            // tiled map object rotations use the top left corner as the origin and we need to convert that to
            // the center point

            // we can't get width and height directly but we can use the standard tile dimensions and the scale
            float width = tile.getScaleX() * Config.PIXELS_PER_METER;
            float height = tile.getScaleY() * Config.PIXELS_PER_METER;

            System.out.print(height);

            // top left corner
            float x = tile.getX() / Config.PIXELS_PER_METER;
            float y = (tile.getY() + height) / Config.PIXELS_PER_METER;

            // convert the degrees rotation to rads
            float rotation = tile.getRotation() * MathUtils.degreesToRadians;

            // get the radius in metres, half of diagonal for rectangular object using Pythagoras
            float radius = (float) ((Math.sqrt((width * width + height * height))) / 2f) / Config.PIXELS_PER_METER;

            // Angle in rads at diagonal of rectangular object
            double theta = (Math.tanh(height / width) * MathUtils.degreesToRadians);

            // Finding new position if rotation was with respect to the top-left corner of the object.
            // X=x+ radius*cos(theta-angle)+(h/2)cos(90+angle)
            // Y= y+radius*sin(theta-angle)-(h/2)sin(90+angle)
            x += radius * Math.cos(-rotation + theta) + ((height / Config.PIXELS_PER_METER / 2) * Math.cos(90 * MathUtils.degreesToRadians + rotation));
            y += radius * Math.sin(-rotation + theta) - (height / Config.PIXELS_PER_METER / 2) * Math.sin(90 * MathUtils.degreesToRadians + rotation);

            String type = properties.get("type", String.class);

            Entity e = EntityFactory.create(entityManager, world, type, x, y, rotation);

            // tell clients to load the entity too

            // get the id

            // create a request with all required info

            // send / queue request
        }
    }

//    public static void rotateObject(Body body, float x1, float y1, float width1,
//                                    float height1, float rotation,float PPM) {
//
//        final float DEGREES_TO_RADS = 0.0174532925199432957f;
//
//        // Top left corner of object
//        Vector2 pos = new Vector2((x1) / PPM, (y1 + height1) / PPM);
//
//        // angle of rotation in radians
//        float angle = DEGTORAD * (rotation);
//
//        // half of diagonal for rectangular object
//        float radius = (float) ((Math.sqrt((width1 * width1 + height1 * height1))) / 2f) / PPM;
//        // Angle at diagonal of rectangular object
//        double theta = (Math.tanh(height1 / width1) * DEGTORAD);
//
//        // Finding new position if rotation was with respect to top-left corner of object.
//        // X=x+ radius*cos(theta-angle)+(h/2)cos(90+angle)
//        // Y= y+radius*sin(theta-angle)-(h/2)sin(90+angle)
//        pos = pos.add((float) (radius * Math.cos(-angle + theta)),
//                (float) (radius * Math.sin(-angle + theta))).add(
//                (float) ((height1 / PPM / 2) * Math.cos(90 * DEGTORAD
//                        + angle)),
//                (float) (-(height1 / PPM / 2) * Math.sin(90
//                        * DEGTORAD + angle)));
//        // transform the body
//        body.setTransform(pos, -angle);
//
//    }

}
