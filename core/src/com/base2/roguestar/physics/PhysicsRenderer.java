package com.base2.roguestar.physics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.base2.roguestar.utils.Config;

public class PhysicsRenderer {

    private ShapeRenderer shapeRenderer;
    private final Matrix4 combined = new Matrix4();

    public void init() {
        shapeRenderer = new ShapeRenderer();
    }

    public void render(PhysicsManager physics, OrthographicCamera camera) {
        // the debug renderer and physics world work in meters, the camera matrix is in pixels
        // we need to scale the camera matrix by the pixels per meter value to make the scales
        // match and render the debug draw correctly over the tiled maps.
        combined.set(camera.combined).scl(Config.PIXELS_PER_METER);

        shapeRenderer.setProjectionMatrix(combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1);
        for (int i = 0, n = physics.getBodyCount(); i < n; i += 1) {
            Body body = physics.getBody(i);
            btCollisionObject collisionObject = body.getCollisionObject();
            btCollisionShape shape = collisionObject.getCollisionShape();

            float x = body.getX();
            float y = body.getY();
            float angle = body.getRotation();

            shapeRenderer.identity();

            if (shape instanceof btBoxShape) {
                btBoxShape boxShape = (btBoxShape) shape;
                float width = boxShape.getHalfExtentsWithMargin().x;
                float height = boxShape.getHalfExtentsWithMargin().y;

                shapeRenderer.translate(x, y, 0);
                shapeRenderer.rotate(0, 0, 1, angle);
                shapeRenderer.rect(-width, -height, width * 2, height * 2);
            }
            else if (shape instanceof btSphereShape) {
                btSphereShape sphereShape = (btSphereShape) shape;
                float radius = sphereShape.getRadius();
                shapeRenderer.circle(x, y, radius, 16);
            }
            else if (shape instanceof btCylinderShape) {
                btCylinderShape cylinderShape = (btCylinderShape) shape;
                float width = cylinderShape.getRadius();
                float height = cylinderShape.getHalfExtentsWithMargin().y;

                shapeRenderer.translate(x, y, 0);
                shapeRenderer.rotate(0, 0, 1, angle);
                shapeRenderer.rect(-width, -height, width * 2, height * 2);
            }
            else if (shape instanceof btCapsuleShape) {
                btCapsuleShape capsuleShape = (btCapsuleShape) shape;
                float width = capsuleShape.getRadius();
                float height = capsuleShape.getHalfHeight();

                shapeRenderer.translate(x, y, 0);
                shapeRenderer.rotate(0, 0, 1, angle);
                shapeRenderer.circle(0, height, width, 16);
                shapeRenderer.rect(-width, -height, width * 2, height * 2);
                shapeRenderer.circle(0, -height, width, 16);
            }
        }
        shapeRenderer.end();

    }

    public void dispose() {
        this.shapeRenderer.dispose();
    }

}
