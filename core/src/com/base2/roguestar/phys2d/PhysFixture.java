package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.*;

public class PhysFixture {

    private PhysBody body;
    private Shape2D shape;
    private Vector2 offset;

    // tmp objects for overlap checks
    private float[] rectangleVertices = new float[8];
    Vector2 start = new Vector2();
    Vector2 end = new Vector2();
    Vector2 center = new Vector2();

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

    public boolean overlaps(PhysFixture other) {

        boolean overlaps = false;

        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle)shape;
            overlaps = overlaps(rectangle, other.getShape());
        }
        else if (shape instanceof Circle) {
            Circle circle = (Circle)shape;
            overlaps = overlaps(circle, other.getShape());
        }
        else if (shape instanceof Polygon) {
            Polygon polygon = (Polygon)shape;
            overlaps = overlaps(polygon, other.getShape());
        }
        else if (shape instanceof Polyline) {
            Polyline polyline = (Polyline)shape;
            overlaps = overlaps(polyline, other.getShape());
        }

        return overlaps;
    }

    private boolean overlaps(Rectangle rectangle, Shape2D other) {

        boolean overlaps = false;

        if (other instanceof Rectangle) {
            overlaps = Intersector.overlaps((Rectangle)other, rectangle);
        }
        else if (other instanceof Circle) {
            overlaps = Intersector.overlaps((Circle)other, rectangle);
        }
        else if (other instanceof Polygon) {
            Polygon polygon = (Polygon)other;
            overlaps = overlaps(polygon.getTransformedVertices(), rectangle);
        }
        else if (other instanceof Polyline) {
            Polyline polyline = (Polyline)other;
            overlaps = overlaps(polyline.getTransformedVertices(), rectangle);
        }

        return overlaps;
    }

    private boolean overlaps(Circle circle, Shape2D other) {

        boolean overlaps = false;

        if (other instanceof Rectangle) {
            overlaps = Intersector.overlaps(circle, (Rectangle)other);
        }
        else if (other instanceof Circle) {
            overlaps = Intersector.overlaps((Circle)other, circle);
        }
        else if (other instanceof Polygon) {
            Polygon polygon = (Polygon)other;
            overlaps = overlaps(polygon.getTransformedVertices(), circle);
        }
        else if (other instanceof Polyline) {
            Polyline polyline = (Polyline)other;
            overlaps = overlaps(polyline.getTransformedVertices(), circle);
        }

        return overlaps;
    }

    private boolean overlaps(Polygon polygon, Shape2D other) {

        boolean overlaps = false;

        if (other instanceof Rectangle) {
            overlaps = overlaps(polygon.getTransformedVertices(), (Rectangle)other);
        }
        else if (other instanceof Circle) {
            overlaps = overlaps(polygon.getTransformedVertices(), (Circle)other);
        }
        else if (other instanceof Polygon) {
            overlaps = Intersector.overlapConvexPolygons(polygon, (Polygon)other);
        }
        else if (other instanceof Polyline) {
            Polyline polyline = (Polyline)other;
            overlaps = Intersector.overlapConvexPolygons(polygon.getTransformedVertices(), polyline.getTransformedVertices(), null);
        }

        return overlaps;
    }

    private boolean overlaps(Polyline polyline, Shape2D other) {

        boolean overlaps = false;

        if (other instanceof Rectangle) {
            overlaps = overlaps(polyline.getTransformedVertices(), (Rectangle)other);
        }
        else if (other instanceof Circle) {
            overlaps = overlaps(polyline.getTransformedVertices(), (Circle)other);
        }
        else if (other instanceof Polygon) {
            Polygon polygon = (Polygon)other;
            overlaps = Intersector.overlapConvexPolygons(polyline.getTransformedVertices(), polygon.getTransformedVertices(), null);
        }
        else if (other instanceof Polyline) {
            overlaps = Intersector.overlapConvexPolygons(((Polyline)other).getTransformedVertices(), polyline.getTransformedVertices(), null);
        }

        return overlaps;
    }

    private boolean overlaps(float[] vertices, Circle circle) {

        boolean overlaps = false;
        float squareRadius = circle.radius * circle.radius;
        center.set(circle.x, circle.y);

        for (int i = 0, n = vertices.length; i < n && !overlaps; i += 2) {
            overlaps = Intersector.intersectSegmentCircle(
                    start.set(vertices[(i + (vertices.length - 2)) % vertices.length], vertices[(i + (vertices.length - 1)) % vertices.length]),
                    end.set(vertices[i], vertices[i + 1]),
                    center,
                    squareRadius);
        }
        return overlaps;
    }

    private boolean overlaps(float[] vertices, Rectangle rectangle) {

        boolean overlaps = false;

        rectangleVertices[0] = rectangle.getX();
        rectangleVertices[1] = rectangle.getY();

        rectangleVertices[2] = rectangle.getX();
        rectangleVertices[3] = rectangle.getY() + rectangle.getHeight();

        rectangleVertices[4] = rectangle.getX() + rectangle.getWidth();
        rectangleVertices[5] = rectangle.getY() + rectangle.getHeight();

        rectangleVertices[6] = rectangle.getX() + rectangle.getWidth();
        rectangleVertices[7] = rectangle.getY();

        overlaps = Intersector.overlapConvexPolygons(vertices, rectangleVertices, null);

        return overlaps;
    }
}
