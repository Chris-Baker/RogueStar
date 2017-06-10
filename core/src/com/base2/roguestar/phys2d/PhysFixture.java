package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.*;

public class PhysFixture {

    private PhysBody body;
    private Shape2D shape;
    private Vector2 offset;

    protected PhysFixture(PhysBody body) {
        this.body = body;
        this.offset = new Vector2();
    }

    public Shape2D getShape() {
        return shape;
    }

    public void setShape(Shape2D shape) {
        this.shape = shape;

        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle)shape;
            this.offset.x += rectangle.x;
            this.offset.y += rectangle.y;
        }
        else if (shape instanceof Circle) {
            Circle circle = (Circle)shape;
            this.offset.x += circle.x;
            this.offset.y += circle.y;
        }
        else if (shape instanceof Polygon) {
            Polygon polygon = (Polygon)shape;
            this.offset.x += polygon.getX();
            this.offset.y += polygon.getY();
        }
        else if (shape instanceof Polyline) {
            Polyline polyline = (Polyline)shape;
            this.offset.x += polyline.getX();
            this.offset.y += polyline.getY();
        }
    }

    public float getX() {
        return body.getX() + offset.x;
    }

    public float getY() {
        return body.getY() + offset.y;
    }

    public void setOffset(float x, float y) {
        this.offset.set(x, y);

        x += body.getX();
        y += body.getY();

        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle)shape;
            rectangle.setPosition(x, y);
        }
        else if (shape instanceof Circle) {
            Circle circle = (Circle)shape;
            circle.setPosition(x, y);
        }
        else if (shape instanceof Polygon) {
            Polygon polygon = (Polygon)shape;
            polygon.setPosition(x, y);
        }
        else if (shape instanceof Polyline) {
            Polyline polyline = (Polyline)shape;
            polyline.setPosition(x, y);
        }

        body.calculateAABB();
    }

    public void setPosition(float x, float y) {

        x += offset.x;
        y += offset.y;

        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle)shape;
            rectangle.setPosition(x, y);
        }
        else if (shape instanceof Circle) {
            Circle circle = (Circle)shape;
            circle.setPosition(x, y);
        }
        else if (shape instanceof Polygon) {
            Polygon polygon = (Polygon)shape;
            polygon.setPosition(x, y);
        }
        else if (shape instanceof Polyline) {
            Polyline polyline = (Polyline)shape;
            polyline.setPosition(x, y);
        }
    }

    public void setPosition(Vector2 position) {
        this.setPosition(position.x, position.y);
    }

}
