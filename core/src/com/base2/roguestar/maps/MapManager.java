package com.base2.roguestar.maps;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by Chris on 28/03/2016.
 */
public class MapManager {

    private TmxMapLoader loader;
    private TiledMap map;

    public void load(String fileName) {
        loader = new TmxMapLoader();
        map = loader.load(fileName);
        System.out.println(fileName + " loaded.");
    }

    public TiledMap getMap() {
        return map;
    }

    public void dispose() {
        map.dispose();
    }
}
