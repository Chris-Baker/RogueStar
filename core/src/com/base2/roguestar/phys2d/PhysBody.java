package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PhysBody {

    private Array<PhysFixture> fixtures = new Array<PhysFixture>();
    private PhysBodyType type = PhysBodyType.STATIC;
    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();
    private AABB aabb = new AABB();
    private Object userData;

    protected PhysBody() {}

    public Array<PhysFixture> getFixtures() {
        return fixtures;
    }

    public PhysFixture createFixture(Shape2D shape) {

        PhysFixture fixture = new PhysFixture(this);
        fixture.setShape(shape);
        this.aabb.extend(fixture.getShape());
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
        this.aabb.setPosition(x, y);
        for (PhysFixture fixture: fixtures) {
            fixture.setPosition(x, y);
        }
    }

    public void setPosition(Vector2 position) {
        this.setPosition(position.x, position.y);
    }

    public AABB getAABB() {
        return aabb;
    }

    public void calculateAABB() {
        if (fixtures.size > 0) {
            aabb.set(fixtures.first().getShape());
            for (PhysFixture fixture: fixtures) {
                aabb.extend(fixture.getShape());
            }
        }
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }
}
