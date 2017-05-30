package com.base2.roguestar.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.base2.roguestar.RogueStarClient;
import com.base2.roguestar.game.GameManager;
import com.base2.roguestar.network.messages.PlayerReadyMessage;
import com.base2.roguestar.network.messages.SetMapMessage;
import com.base2.roguestar.utils.Locator;

public class SetupScreen implements Screen {

    private GameManager gameManager;
    private RogueStarClient game;

    private Skin skin;
    private Stage stage;

    public SetupScreen(final RogueStarClient game) {
        this.game = game;
        this.gameManager = Locator.getGameManager();
        this.skin = new Skin(Gdx.files.internal("skins/plain.json"));
        this.stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    @Override
    public void show() {

        Table menu = new Table();
        menu.setFillParent(true);

        final TextButton setMapButton = new TextButton("Set Map", skin, "default");
        setMapButton.setWidth(200);
        setMapButton.setHeight(50);

        setMapButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SetMapMessage request = new SetMapMessage();
                request.mapName = "maps/map.tmx";
                game.network.sendUDP(request);
            }
        });
        menu.add(setMapButton).padBottom(10);
        menu.row();

        final TextButton readyButton = new TextButton("Ready", skin, "default");
        readyButton.setWidth(200);
        readyButton.setHeight(50);

        readyButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerReadyMessage request = new PlayerReadyMessage();
                request.uid = gameManager.getLocalPlayerUid().toString();
                game.network.sendUDP(request);
            }
        });
        menu.add(readyButton).padBottom(10);
        menu.row();

        stage.addActor(menu);

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
