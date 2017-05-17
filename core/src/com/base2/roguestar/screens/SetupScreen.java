package com.base2.roguestar.screens;

import com.badlogic.gdx.Screen;
import com.base2.roguestar.RogueStarClient;

public class SetupScreen implements Screen {

    private RogueStarClient game;

    public SetupScreen(RogueStarClient game) {
        this.game = game;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
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
