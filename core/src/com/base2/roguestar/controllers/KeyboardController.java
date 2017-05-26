package com.base2.roguestar.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.base2.roguestar.utils.Preferences;

/**
 * Created by Chris on 28/03/2016.
 */
public class KeyboardController extends CharacterController implements InputProcessor {

    /* (non-Javadoc)
         * @see com.badlogic.gdx.InputProcessor#keyDown(int)
         */
    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Preferences.KEY_JUMP) {
            this.jump = true;
        }
        else if(keycode == Preferences.KEY_LEFT) {
            this.moveLeft = true;
        }
        else if(keycode == Preferences.KEY_RIGHT) {
            this.moveRight = true;
        }

        //idleTime = 0;

        return false;
    }

    /* (non-Javadoc)
     * @see com.badlogic.gdx.InputProcessor#keyUp(int)
     */
    @Override
    public boolean keyUp(int keycode) {

        if(keycode == Preferences.KEY_JUMP) {
            this.jump = false;
        }
        else if(keycode == Preferences.KEY_LEFT) {
            this.moveLeft = false;
        }
        else if(keycode == Preferences.KEY_RIGHT) {
            this.moveRight = false;
        }
        else if(keycode == Input.Keys.ESCAPE) {
            Gdx.app.exit();
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
