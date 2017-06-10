package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.*;

public class AABB {

    private Vector2 tmpMin = new Vector2();
    private Vector2 tmpMax = new Vector2();

    private Vector2 min = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
    private Vector2 max = new Vector2(Float.MIN_VALUE, Float.MIN_VALUE);
    private Vector2 position = new Vector2();

    public void extend(Vector2 min, Vector2 max) {
        this.min.set((this.min.x < min.x) ? this.min.x : min.x, (this.min.y < min.y) ? this.min.y : min.y);
        this.max.set((this.max.x > max.x) ? this.max.x : max.x, (this.max.y > max.y) ? this.max.y : max.y);
    }

    public void extend(AABB aabb) {
        this.extend(aabb.min, aabb.max);
    }

    public void extend(Shape2D shape) {
        if (shape instanceof Rectangle) {
            extend((Rectangle)shape);
        }
        else if (shape instanceof Circle) {
            extend((Circle)shape);
        }
        else if (shape instanceof Polygon) {
            extend(((Polygon)shape).getBoundingRectangle());
        }
        else if (shape instanceof Polyline) {
            extend(((Polyline)shape).getTransformedVertices());
        }
    }

    public void extend(Rectangle rectangle) {
        tmpMin.set(rectangle.getX(), rectangle.getY());
        tmpMax.set(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight());
        extend(tmpMin, tmpMax);
    }

    public void extend(Circle circle) {
        tmpMin.set(circle.x - circle.radius, circle.y - circle.radius);
        tmpMax.set(circle.x + circle.radius, circle.y + circle.radius);
        extend(tmpMin, tmpMax);
    }

    public void extend(Polygon polygon) {
        extend(polygon.getBoundingRectangle());
    }

    public void extend(Polyline polyline) {
        extend(polyline.getTransformedVertices());
    }

    public void extend(float[] vertices) {
        tmpMin.set(Float.MAX_VALUE, Float.MAX_VALUE);
        tmpMax.set(Float.MIN_VALUE, Float.MIN_VALUE);

        for (int i = 0, n = vertices.length; i < n; i += 2) {
            tmpMin.x = (vertices[i] < tmpMin.x) ? vertices[i] : tmpMin.x;
            tmpMin.y = (vertices[i + 1] < tmpMin.y) ? vertices[i + 1] : tmpMin.y;
            tmpMax.x = (vertices[i] > tmpMax.x) ? vertices[i] : tmpMax.x;
            tmpMax.y = (vertices[i + 1] > tmpMax.y) ? vertices[i + 1] : tmpMax.y;
        }
        extend(tmpMin, tmpMax);
    }

    public void set(Vector2 min, Vector2 max) {
        this.min.set(min.x < max.x ? min.x : max.x, min.y < max.y ? min.y : max.y);
        this.max.set(min.x > max.x ? min.x : max.x, min.y > max.y ? min.y : max.y);
    }

    public void set(AABB aabb) {
        this.set(aabb.min, aabb.max);
    }

    public void set(Shape2D shape) {
        if (shape instanceof Rectangle) {
            set((Rectangle)shape);
        }
        else if (shape instanceof Circle) {
            set((Circle)shape);
        }
        else if (shape instanceof Polygon) {
            set(((Polygon)shape).getBoundingRectangle());
        }
        else if (shape instanceof Polyline) {
            set(((Polyline)shape).getTransformedVertices());
        }
    }

    public void set(Rectangle rectangle) {
        tmpMin.set(rectangle.getX(), rectangle.getY());
        tmpMax.set(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight());
        set(tmpMin, tmpMax);
    }

    public void set(Circle circle) {
        tmpMin.set(circle.x - circle.radius, circle.y - circle.radius);
        tmpMax.set(circle.x + circle.radius, circle.y + circle.radius);
        set(tmpMin, tmpMax);
    }

    public void set(Polygon polygon) {
        set(polygon.getBoundingRectangle());
    }

    public void set(Polyline polyline) {
        set(polyline.getTransformedVertices());
    }

    public void set(float[] vertices) {
        tmpMin.set(Float.MAX_VALUE, Float.MAX_VALUE);
        tmpMax.set(Float.MIN_VALUE, Float.MIN_VALUE);

        for (int i = 0, n = vertices.length; i < n; i += 2) {
            tmpMin.x = (vertices[i] < tmpMin.x) ? vertices[i] : tmpMin.x;
            tmpMin.y = (vertices[i + 1] < tmpMin.y) ? vertices[i + 1] : tmpMin.y;
            tmpMax.x = (vertices[i] > tmpMax.x) ? vertices[i] : tmpMax.x;
            tmpMax.y = (vertices[i + 1] > tmpMax.y) ? vertices[i + 1] : tmpMax.y;
        }
        set(tmpMin, tmpMax);
    }

    public float getMinX() {
        return this.min.x;
    }

    public float getMinY() {
        return this.min.y;
    }

    public float getMaxX() {
        return this.max.x;
    }

    public float getMaxY() {
        return this.max.y;
    }

    public void setPosition(float x, float y) {
        float deltaX = x - this.position.x;
        float deltaY = y - this.position.y;
        this.position.add(deltaX, deltaY);
        this.min.add(deltaX, deltaY);
        this.max.add(deltaX, deltaY);
    }
}
