package com.base2.roguestar.entities.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.base2.roguestar.entities.components.CameraComponent;
import com.base2.roguestar.entities.components.CharacterComponent;
import com.base2.roguestar.physics.Body;
import com.base2.roguestar.utils.Config;

/**
 * Created by Chris on 28/03/2016.
 */
public class CameraSystem extends IteratingSystem {

    private ComponentMapper<CharacterComponent> pm;

    private Vector3 target = new Vector3();
    private OrthographicCamera camera;

    public CameraSystem() {
        super(Family.all(CameraComponent.class, CharacterComponent.class).get());

        pm = ComponentMapper.getFor(CharacterComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        CharacterComponent p = pm.get(entity);

        Body body = p.character;

        target.set(body.getX(), body.getY(), 0).scl(Config.PIXELS_PER_METER);

        camera.position.lerp(target, 10 * deltaTime);
        camera.update();
    }
    
    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }
}
