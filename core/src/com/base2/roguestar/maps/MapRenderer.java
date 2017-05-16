package com.base2.roguestar.maps;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * Created by Chris on 28/03/2016.
 */
public class MapRenderer {

    private OrthogonalTiledMapRenderer renderer;

    public void init(TiledMap map) {
        renderer = new OrthogonalTiledMapRenderer(map);
    }

    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    public void dispose() {
        renderer.dispose();
    }
}
