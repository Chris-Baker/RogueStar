package com.base2.roguestar.maps;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.base2.roguestar.events.EventManager;
import com.base2.roguestar.events.messages.CreateEntityEvent;
import com.base2.roguestar.game.GameManager;
import com.base2.roguestar.utils.Config;
import com.base2.roguestar.utils.Locator;

import java.util.UUID;

public class MapObjectLoader {

    EventManager events;
    GameManager game;;

    protected void init() {
        this.events = Locator.getEventManager();
        this.game = Locator.getGameManager();
    }

    protected void loadEntities(TiledMap map) {
        parseObjectLayer(map.getLayers().get("entities").getObjects());
    }

    private void parseObjectLayer(MapObjects objects) {

        for (MapObject object : objects) {

            if (!(object instanceof TiledMapTileMapObject)) {
                continue;
            }

            TiledMapTileMapObject tile = (TiledMapTileMapObject) object;
            MapProperties properties = tile.getProperties();


            // tiled map object rotations use the top left corner as the origin and we need to convert that to
            // the center point

            // we can't get width and height directly but we can use the standard tile dimensions and the scale
            float width = tile.getScaleX() * Config.PIXELS_PER_METER;
            float height = tile.getScaleY() * Config.PIXELS_PER_METER;

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

            // queue up our event so it can be handled by the entity manager
            CreateEntityEvent event = new CreateEntityEvent();

            // we want to get the next unspawned player uid so this entity is linked
            if (type.equals("player")) {

                UUID playerUid = game.getUnspawnedPlayerUid();

                // we don't want to create an entity if there is no unspawned player
                if (playerUid == null) {
                    return;
                }

                event.uid = playerUid;
            }

            event.type = type;
            event.x = x;
            event.y = y;
            event.rotation = rotation;
            events.queue(event);
        }
    }
}
