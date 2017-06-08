package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PhysBody {

    private Array<PhysFixture> fixtures = new Array<PhysFixture>();
    private PhysBodyType type = PhysBodyType.STATIC;
    private Vector2 position = new Vector2();

    public Array<PhysFixture> getFixtures() {
        return fixtures;
    }

    public PhysFixture createFixture(Shape2D shape) {

        PhysFixture fixture = new PhysFixture();
        fixture.setShape(shape);
        this.fixtures.add(fixture);
        return fixture;
    }

    public PhysBodyType getType() {
        return type;
    }

    public void setType(PhysBodyType type) {
        this.type = type;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }
}
