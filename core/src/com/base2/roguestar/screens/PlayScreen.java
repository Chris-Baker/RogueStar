package com.base2.roguestar.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.base2.roguestar.RogueStarClient;
import com.base2.roguestar.maps.MapManager;
import com.base2.roguestar.maps.MapRenderer;
import com.base2.roguestar.phys2d.PhysDebugRenderer;
import com.base2.roguestar.physics.PhysicsManager;
import com.base2.roguestar.physics.PhysicsRenderer;
import com.base2.roguestar.utils.Config;
import com.base2.roguestar.utils.Locator;

/**
 * Created by Chris on 19/03/2016.
 */
public class PlayScreen implements Screen {

    private PhysicsManager physics;
    private MapManager maps;
    private MapRenderer mapRenderer;
    private PhysicsRenderer physicsRenderer;
    private PhysDebugRenderer physDebugRenderer;
    private OrthographicCamera camera;

    public PlayScreen(OrthographicCamera camera) {
        this.mapRenderer = new MapRenderer();
        this.physicsRenderer = new PhysicsRenderer();
        this.physDebugRenderer = new PhysDebugRenderer();
        this.camera = camera;
    }

    @Override
    public void show() {
        this.physics = Locator.getPhysicsManager();
        this.maps = Locator.getMapManager();
        this.mapRenderer.init(this.maps.getMap());
        this.physicsRenderer.init();
        this.physDebugRenderer.init();
    }

    @Override
    public void render(float delta) {
        //mapRenderer.render(game.camera);
        physicsRenderer.render(physics.getWorld(), this.camera);
        //physDebugRenderer.render(physics.getPhysWorld(), this.camera, Config.PIXELS_PER_METER);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
    }
}
