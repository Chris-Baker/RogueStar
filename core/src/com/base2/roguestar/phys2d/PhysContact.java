package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.Vector2;

public class PhysContact {

    private PhysFixture fixtureA;
    private PhysFixture fixtureB;
    private Vector2 normal;
    private float depth;

    public PhysContact(PhysFixture fixtureA, PhysFixture fixtureB, Vector2 normal, float depth) {
        this.fixtureA = fixtureA;
        this.fixtureB = fixtureB;
        this.normal = new Vector2(normal);
        this.depth = depth;
    }

    public PhysFixture getFixtureA() {
        return fixtureA;
    }

    public PhysFixture getFixtureB() {
        return fixtureB;
    }

    public Vector2 getNormal() {
        return normal;
    }

    public float getDepth() {
        return depth;
    }

    @Override
    public int hashCode() {
        return fixtureA.hashCode() ^ fixtureB.hashCode();
    }
}