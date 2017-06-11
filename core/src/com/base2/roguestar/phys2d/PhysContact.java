package com.base2.roguestar.phys2d;

public class PhysContact {

    private PhysFixture fixtureA;
    private PhysFixture fixtureB;

    public PhysContact(PhysFixture fixtureA, PhysFixture fixtureB) {
        this.fixtureA = fixtureA;
        this.fixtureB = fixtureB;
    }

    public PhysFixture getFixtureA() {
        return fixtureA;
    }

    public PhysFixture getFixtureB() {
        return fixtureB;
    }

    @Override
    public int hashCode() {
        return fixtureA.hashCode() ^ fixtureB.hashCode();
    }
}