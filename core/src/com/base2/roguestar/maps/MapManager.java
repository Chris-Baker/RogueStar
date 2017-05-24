package com.base2.roguestar.maps;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.base2.roguestar.events.Event;
import com.base2.roguestar.events.EventSubscriber;

/**
 * Created by Chris on 28/03/2016.
 */
public class MapManager implements EventSubscriber {

    private TmxMapLoader loader;
    private TiledMap map;
    private MapObjectLoader objects;

    public void load(String fileName) {
        loader = new TmxMapLoader();
        map = loader.load(fileName);
        objects = new MapObjectLoader();
        objects.init();
    }

    public void loadEntities() {
        objects.loadEntities(map);
    }

    public TiledMap getMap() {
        return map;
    }

    public void dispose() {
        map.dispose();
    }

    @Override
    public void handleEvent(Event event) {

    }
}
