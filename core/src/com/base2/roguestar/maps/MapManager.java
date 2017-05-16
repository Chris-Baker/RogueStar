package com.base2.roguestar.maps;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by Chris on 28/03/2016.
 */
public class MapManager {

    private TmxMapLoader loader;
    public TiledMap map;
    //private OrthogonalTiledMapRenderer renderer;

    public void init() {

        loader = new TmxMapLoader();
        map = loader.load("maps/map.tmx");

        //renderer = new OrthogonalTiledMapRenderer(map);
    }

    public void render(OrthographicCamera camera) {

        //renderer.setView(camera);
       // renderer.render();
    }

    public void dispose() {
        map.dispose();
        //renderer.dispose();
    }
}
