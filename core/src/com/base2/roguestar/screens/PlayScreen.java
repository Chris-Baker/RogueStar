package com.base2.roguestar.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.base2.roguestar.RogueStarClient;
import com.base2.roguestar.network.messages.*;
import com.base2.roguestar.physics.Simulation;
import com.base2.roguestar.physics.SimulationSnapshot;
import com.base2.roguestar.utils.CollisionLoader;
import com.base2.roguestar.utils.EntityLoader;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

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

        game.physics.init();
        game.campaign.init();
        game.entities.init(game);

        CollisionLoader.load(game.campaign.map, game.physics.world);
        EntityLoader.load(game.campaign.map, game.entities.engine, game.physics.world);
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
