package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;

public class PhysFixture {

    private PhysBody body;
    private Shape2D shape;
    private Vector2 position;

    protected PhysFixture(PhysBody body) {
        this.body = body;
        this.position = new Vector2();
    }

    public Shape2D getShape() {
        return shape;
    }

    public void setShape(Shape2D shape) {
        this.shape = shape;
    }

    public float getX() {
        return body.getX() + position.x;
    }

    public float getY() {
        return body.getY() + position.y;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }
}
