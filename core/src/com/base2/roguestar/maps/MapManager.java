package com.base2.roguestar.maps;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by Chris on 28/03/2016.
 */
public class MapManager {

    private TmxMapLoader loader;
    public TiledMap map;

    public void init() {
        loader = new TmxMapLoader();
        map = loader.load("maps/map.tmx");
    }

    public void dispose() {
        map.dispose();
    }
}
