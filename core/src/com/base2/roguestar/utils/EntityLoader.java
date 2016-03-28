package com.base2.roguestar.utils;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.base2.roguestar.entities.EntityFactory;

/**
 * Created by Chris on 28/03/2016.
 */
public class EntityLoader {

    public static void load(TiledMap map, PooledEngine engine, World world) {
        parseObjectLayer(map.getLayers().get("entities").getObjects(), engine, world);
    }

    private static void parseObjectLayer(MapObjects objects, PooledEngine engine, World world) {

        for (MapObject object : objects) {

            if (!(object instanceof TiledMapTileMapObject)) {
                continue;
            }

            TiledMapTileMapObject tile = (TiledMapTileMapObject) object;
            MapProperties properties = tile.getProperties();

            // convert positions and rotation to box2d coordinates
            float x = (tile.getX() + (Config.PIXELS_PER_METER / 2)) / Config.PIXELS_PER_METER;
            float y = (tile.getY() + (Config.PIXELS_PER_METER / 2)) / Config.PIXELS_PER_METER;
            float angle = 0; //tile.getRotation(); rotation messes up the origin
            String type = properties.get("type", String.class);

            EntityFactory.create(engine, world, type, x, y, angle);

        }
    }

}
