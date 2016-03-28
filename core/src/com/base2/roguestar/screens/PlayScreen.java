package com.base2.roguestar.screens;

import com.badlogic.gdx.Screen;
import com.base2.roguestar.RogueStar;
import com.base2.roguestar.utils.CollisionLoader;
import com.base2.roguestar.utils.EntityLoader;

/**
 * Created by Chris on 19/03/2016.
 */
public class PlayScreen implements Screen {

    private RogueStar game;

    public PlayScreen(RogueStar game) {
        this.game = game;
    }

    @Override
    public void show() {

        game.physics.init();
        game.campaign.init();
        game.entities.init();

        CollisionLoader.load(game.campaign.map, game.physics.world);
        EntityLoader.load(game.campaign.map, game.entities.engine, game.physics.world);
    }

    @Override
    public void render(float delta) {

        // update entities
        game.physics.update();

        game.campaign.render(game.camera);
        game.physics.debugRender(game.camera);
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
