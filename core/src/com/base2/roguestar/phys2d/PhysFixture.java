package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.*;

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

        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle)shape;
            this.position.x += rectangle.x;
            this.position.y += rectangle.y;
        }
        else if (shape instanceof Circle) {
            Circle circle = (Circle)shape;
            this.position.x += circle.x;
            this.position.y += circle.y;
        }
        else if (shape instanceof Polygon) {
            Polygon polygon = (Polygon)shape;
            this.position.x += polygon.getX();
            this.position.y += polygon.getY();
        }
        else if (shape instanceof Polyline) {
            Polyline polyline = (Polyline)shape;
            this.position.x += polyline.getX();
            this.position.y += polyline.getY();
        }
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
