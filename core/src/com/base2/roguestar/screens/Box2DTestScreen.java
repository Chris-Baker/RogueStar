package com.base2.roguestar.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.base2.roguestar.entities.Player;

/**
 * Created by Chris on 19/03/2016.
 */
public class Box2DTestScreen implements Screen {

    private final float PIXELS_PER_METER = 16;

    private final float TIME_STEP = 1 / 60f;
    private final int VELOCITY_ITERATIONS = 8;
    private final int POSITION_ITERATIONS = 3;

    private World physicsWorld;
    private Box2DDebugRenderer renderer;

    private OrthographicCamera camera;
    private Player player;

    @Override
    public void show() {

        physicsWorld = new World(new Vector2(0, 0), true);

        renderer = new Box2DDebugRenderer();

        camera = new OrthographicCamera();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, 0);

        CircleShape shape = new CircleShape();
        shape.setRadius(1.0f);

        FixtureDef  fixtureDef = new FixtureDef();
        fixtureDef.density = 2.5f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.isSensor = false;
        fixtureDef.shape = shape;

        Body body = physicsWorld.createBody(bodyDef);
        body.createFixture(fixtureDef);

        player = new Player(new Sprite(new Texture("images/player.png")));
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        physicsWorld.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

        renderer.render(physicsWorld, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width / PIXELS_PER_METER;
        camera.viewportHeight = height / PIXELS_PER_METER;
        camera.update();
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
        physicsWorld.dispose();
        renderer.dispose();
    }
}
