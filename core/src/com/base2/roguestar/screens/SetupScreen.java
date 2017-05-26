package com.base2.roguestar.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.base2.roguestar.RogueStarClient;
import com.base2.roguestar.network.messages.SetMapMessage;

public class SetupScreen implements Screen {

    private RogueStarClient game;

    private Skin skin;
    private Stage stage;

    public SetupScreen(final RogueStarClient game) {
        this.game = game;
        this.skin = new Skin(Gdx.files.internal("skins/plain.json"));
        this.stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    @Override
    public void show() {

        final TextButton button = new TextButton("Ready", skin, "default");
        button.setWidth(200);
        button.setHeight(50);

        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SetMapMessage request = new SetMapMessage();
                request.mapName = "maps/map.tmx";
                game.network.sendUDP(request);
            }
        });
        stage.addActor(button);

        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float deltaTime) {
        stage.act(deltaTime);
        stage.draw();
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
        skin.dispose();
        stage.dispose();
    }
}
