package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.*;

public class PhysFixture {

    private PhysBody body;
    private Shape2D shape;
    private Vector2 offset;

    // tmp objects for overlap checks
    private float[] rectangleVerticesA = new float[8];
    private float[] rectangleVerticesB = new float[8];
    private Vector2 start = new Vector2();
    private Vector2 end = new Vector2();
    private Vector2 center = new Vector2();

    // tmp objects for collision response
    private Vector2 displacement = new Vector2();

    protected PhysFixture(PhysBody body) {
        this.body = body;
        this.offset = new Vector2();
    }

    public PhysBody getBody() {
        return body;
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

    public boolean overlaps(PhysFixture other, Intersector.MinimumTranslationVector mtv) {

        boolean overlaps = false;

        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle)shape;
            overlaps = overlaps(rectangle, other.getShape(), mtv);
        }
        else if (shape instanceof Circle) {
            Circle circle = (Circle)shape;
            overlaps = overlaps(circle, other.getShape(), mtv);
        }
        else if (shape instanceof Polygon) {
            Polygon polygon = (Polygon)shape;
            overlaps = overlaps(polygon, other.getShape(), mtv);
        }
        else if (shape instanceof Polyline) {
            Polyline polyline = (Polyline)shape;
            overlaps = overlaps(polyline, other.getShape(), mtv);
        }

        return overlaps;
    }

    private boolean overlaps(Rectangle rectangle, Shape2D other, Intersector.MinimumTranslationVector mtv) {

        boolean overlaps = false;

        if (other instanceof Rectangle) {
            overlaps = overlaps((Rectangle)other, rectangle, mtv);
        }
        else if (other instanceof Circle) {
            overlaps = overlaps((Circle)other, rectangle, mtv);
        }
        else if (other instanceof Polygon) {
            Polygon polygon = (Polygon)other;
            overlaps = overlaps(polygon.getTransformedVertices(), rectangle, mtv);
        }
        else if (other instanceof Polyline) {
            Polyline polyline = (Polyline)other;
            overlaps = overlaps(polyline.getTransformedVertices(), rectangle, mtv);
        }

        return overlaps;
    }

    private boolean overlaps(Circle circle, Shape2D other, Intersector.MinimumTranslationVector mtv) {

        boolean overlaps = false;

        if (other instanceof Rectangle) {
            overlaps = overlaps(circle, (Rectangle)other, mtv);
        }
        else if (other instanceof Circle) {
            overlaps = overlaps((Circle)other, circle, mtv);
        }
        else if (other instanceof Polygon) {
            Polygon polygon = (Polygon)other;
            overlaps = overlaps(polygon.getTransformedVertices(), circle, mtv);
        }
        else if (other instanceof Polyline) {
            Polyline polyline = (Polyline)other;
            overlaps = overlaps(polyline.getTransformedVertices(), circle, mtv);
        }

        return overlaps;
    }

    private boolean overlaps(Polygon polygon, Shape2D other, Intersector.MinimumTranslationVector mtv) {

        boolean overlaps = false;

        if (other instanceof Rectangle) {
            overlaps = overlaps(polygon.getTransformedVertices(), (Rectangle)other, mtv);
        }
        else if (other instanceof Circle) {
            overlaps = overlaps(polygon.getTransformedVertices(), (Circle)other, mtv);
        }
        else if (other instanceof Polygon) {
            overlaps = Intersector.overlapConvexPolygons(polygon, (Polygon)other, mtv);
        }
        else if (other instanceof Polyline) {
            Polyline polyline = (Polyline)other;
            overlaps = Intersector.overlapConvexPolygons(polygon.getTransformedVertices(), polyline.getTransformedVertices(), mtv);
        }

        return overlaps;
    }

    private boolean overlaps(Polyline polyline, Shape2D other, Intersector.MinimumTranslationVector mtv) {

        boolean overlaps = false;

        if (other instanceof Rectangle) {
            overlaps = overlaps(polyline.getTransformedVertices(), (Rectangle)other, mtv);
        }
        else if (other instanceof Circle) {
            overlaps = overlaps(polyline.getTransformedVertices(), (Circle)other, mtv);
        }
        else if (other instanceof Polygon) {
            Polygon polygon = (Polygon)other;
            overlaps = Intersector.overlapConvexPolygons(polyline.getTransformedVertices(), polygon.getTransformedVertices(), mtv);
        }
        else if (other instanceof Polyline) {
            overlaps = Intersector.overlapConvexPolygons(((Polyline)other).getTransformedVertices(), polyline.getTransformedVertices(), mtv);
        }

        return overlaps;
    }

    private boolean overlaps(Circle circleA, Circle circleB, Intersector.MinimumTranslationVector mtv) {

        boolean overlaps = Intersector.overlaps(circleA, circleB);

        if (overlaps) {
            mtv.normal.set(circleA.x - circleB.x, circleA.y - circleB.y).nor();
            mtv.depth = (circleA.radius + circleB.radius) - Vector2.dst(circleA.x, circleA.y, circleB.x, circleB.y);
        }

        return overlaps;
    }

    private boolean overlaps(float[] vertices, Circle circle, Intersector.MinimumTranslationVector mtv) {

        boolean overlaps = false;
        float depth = 0;
        center.set(circle.x, circle.y);

        for (int i = 0, n = vertices.length; i < n && !overlaps; i += 2) {
            depth = Intersector.intersectSegmentCircleDisplace(
                    start.set(vertices[(i + (vertices.length - 2)) % vertices.length], vertices[(i + (vertices.length - 1)) % vertices.length]),
                    end.set(vertices[i], vertices[i + 1]),
                    center,
                    circle.radius,
                    displacement);

            // the above returns Float.POSITIVE_INFINITY if there is no overlap
            // so we have an overlap if return is not that
            overlaps = depth != Float.POSITIVE_INFINITY;
        }

        // set out mtv object if we have an overlap
        if (overlaps) {
            mtv.normal.set(displacement);
            mtv.depth = depth;
        }

        return overlaps;
    }

    private boolean overlaps(Circle circle, Rectangle rectangle, Intersector.MinimumTranslationVector mtv) {

        return overlaps(getVertices(rectangleVerticesA, rectangle), circle, mtv);
    }

    private boolean overlaps(Rectangle rectangleA, Rectangle rectangleB, Intersector.MinimumTranslationVector mtv) {

        return Intersector.overlapConvexPolygons(
                getVertices(rectangleVerticesA, rectangleA),
                getVertices(rectangleVerticesB, rectangleB),
                mtv);
    }

    private boolean overlaps(float[] vertices, Rectangle rectangle, Intersector.MinimumTranslationVector mtv) {

        return Intersector.overlapConvexPolygons(vertices, getVertices(rectangleVerticesA, rectangle), mtv);
    }

    private float[] getVertices(float[] vertices, Rectangle rectangle) {
        vertices[0] = rectangle.getX();
        vertices[1] = rectangle.getY();

        vertices[2] = rectangle.getX();
        vertices[3] = rectangle.getY() + rectangle.getHeight();

        vertices[4] = rectangle.getX() + rectangle.getWidth();
        vertices[5] = rectangle.getY() + rectangle.getHeight();

        vertices[6] = rectangle.getX() + rectangle.getWidth();
        vertices[7] = rectangle.getY();

        return vertices;
    }
}
