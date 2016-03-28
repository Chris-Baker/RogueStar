package com.base2.roguestar.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Chris on 20/03/2016.
 */
public class Player extends Sprite {

    // movement velocity
    private final Vector2 velocity;
    private final float runSpeed = 60 * 2;
    private final float gravity = 60 * 1.8f;

    public Player(Sprite sprite) {
        super(sprite);
        this.velocity = new Vector2();
    }

    @Override
    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());
        super.draw(batch);
    }

    private void update(float deltaTime) {
        velocity.y = MathUtils.clamp(velocity.y - (gravity * deltaTime), -runSpeed, runSpeed);

        super.setX(super.getX() + (velocity.x * deltaTime));
        super.setY(super.getY() + (velocity.y * deltaTime));
    }

}
