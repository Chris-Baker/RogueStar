package com.base2.roguestar.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

public class Body implements Disposable {

    private btCollisionObject collisionObject;
    private btCollisionShape collisionShape;

    private final Matrix4 transform;
    private final Vector3 translation;
    private final Quaternion rotation;

    public Body(btCollisionShape collisionShape) {
        this.collisionObject = new btCollisionObject();
        this.collisionObject.setCollisionShape(collisionShape);
        this.collisionShape = collisionShape;

        this.transform = new Matrix4();
        this.translation = new Vector3();
        this.rotation = new Quaternion();
    }

    public void setTransform(float x, float y, float angle) {
        setTranslation(x, y);
        setRotation(angle);
    }

    public void setTransform(Vector2 transform, float angle) {
        setTranslation(transform);
        setRotation(angle);
    }

    public void setTranslation(Vector2 translation) {
        setTranslation(translation.x, translation.y);
    }

    public void setTranslation(float x, float y) {
        this.translation.x = x;
        this.translation.y = y;
        this.transform.setTranslation(this.translation);
        this.collisionObject.setWorldTransform(transform);
    }

    public void translate(Vector2 translation) {
        translate(translation.x, translation.y);
    }

    public void translate(float x, float y) {
        this.transform.trn(x, y, 0);
        this.collisionObject.setWorldTransform(transform);
    }

    public void setRotation(float angle) {
        this.transform.getTranslation(this.translation);
        this.transform.setToRotation(Vector3.Z, angle);
        this.transform.trn(this.translation);
        this.collisionObject.setWorldTransform(transform);
    }

    public float getX() {
        this.collisionObject.getWorldTransform(this.transform);
        this.transform.getTranslation(translation);
        return this.translation.x;
    }

    public float getY() {
        this.collisionObject.getWorldTransform(this.transform);
        this.transform.getTranslation(translation);
        return this.translation.y;
    }

    public float getRotation() {
        this.collisionObject.getWorldTransform(this.transform);
        this.transform.getRotation(this.rotation);
        return this.rotation.getAngleAround(Vector3.Z);
    }

    @Override
    public void dispose() {
        collisionObject.dispose();
        collisionShape.dispose();
    }

    public btCollisionObject getCollisionObject() {
        return collisionObject;
    }

}
