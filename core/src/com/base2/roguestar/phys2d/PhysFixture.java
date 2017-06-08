package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;

public class PhysFixture {

    private Shape2D shape;
    private Vector2 position;

    protected PhysFixture() {
        this.position = new Vector2();
    }

    public Shape2D getShape() {
        return shape;
    }

    public void setShape(Shape2D shape) {
        this.shape = shape;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }
}