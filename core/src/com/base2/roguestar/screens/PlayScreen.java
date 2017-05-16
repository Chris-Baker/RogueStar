package com.base2.roguestar.screens;

import com.badlogic.gdx.Screen;
import com.base2.roguestar.RogueStarClient;
import com.base2.roguestar.utils.CollisionLoader;
import com.base2.roguestar.utils.EntityLoader;

/**
 * Created by Chris on 19/03/2016.
 */
public class PlayScreen implements Screen {

    private RogueStarClient game;

    public PlayScreen(RogueStarClient game) {
        this.game = game;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        //game.entities.update(delta);
        //game.physics.update(delta);

        //game.maps.render(game.camera);
        //game.physics.debugRender(game.camera);
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
