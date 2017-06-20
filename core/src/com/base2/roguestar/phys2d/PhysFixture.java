package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.*;

public class PhysFixture {

    private PhysBody body;
    private Polygon shape;
    private Vector2 offset;

    // tmp object for collision response
    private Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

    protected PhysFixture(PhysBody body) {
        this.body = body;
        this.offset = new Vector2();
    }

    public PhysBody getBody() {
        return body;
    }

    public Polygon getShape() {
        return shape;
    }

    public void setShape(Polygon shape) {
        this.shape = shape;
    }

    public float getX() {
        return body.getX() + offset.x;
    }

    public float getY() {
        return body.getY() + offset.y;
    }

    public void setOffset(float x, float y) {
        this.offset.set(x, y);
        this.shape.setPosition(x + body.getX(), y + body.getY());
        body.calculateAABB();
    }

    public void setPosition(float x, float y) {
        this.shape.setPosition(x + offset.x, y + offset.y);
    }

    public void setPosition(Vector2 position) {
        this.setPosition(position.x, position.y);
    }

    public PhysContact overlaps(PhysFixture other) {
        boolean overlaps = Intersector.overlapConvexPolygons(shape, other.getShape(), mtv);
        PhysContact contact = (overlaps) ? new PhysContact(this, other, mtv.normal, mtv.depth) : null;
        return contact;
    }
}
