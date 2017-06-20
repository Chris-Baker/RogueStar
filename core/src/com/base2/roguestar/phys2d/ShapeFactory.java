package com.base2.roguestar.phys2d;

import com.badlogic.gdx.math.Polygon;

public class ShapeFactory {

    /**
     * Generate a polygon with the given number of sides and radius.
     *
     * @param radius the radius of the polygon
     * @param numSides the number of sides the polygon has
     * @return a polygon with the given number of sides and radius.
     */
    public static Polygon getRegularPolygon(float radius, int numSides) {

        float[] vertices = new float[numSides * 2];
        float angle = 0;
        float angleIncrement = (float)((Math.PI * 2) / numSides);

        for (int i = 0, n = numSides * 2; i < n; i += 2) {
            float x = radius * (float)Math.cos(angle);
            float y = radius * (float)Math.sin(angle);

            vertices[i] = x;
            vertices[i + 1] = y;

            angle += angleIncrement;
        }

        Polygon polygon = new Polygon(vertices);
        return polygon;
    }

    /**
     * Get a rectangle polygon with the given width and height.
     *
     * @param width the polygon width
     * @param height the polygon height
     * @return a rectangular polygon with the given width and height.
     */
    public static Polygon getRectangle(float width, float height) {

        float x = 0;
        float y = 0;

        float[] vertices = new float[8];

        vertices[0] = x;
        vertices[1] = y;

        vertices[2] = x + width;
        vertices[3] = y;

        vertices[4] = x + width;
        vertices[5] = y + height;

        vertices[6] = x;
        vertices[7] = y + height;

        Polygon polygon = new Polygon(vertices);
        return polygon;
    }

}
