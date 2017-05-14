package com.base2.roguestar.entities;

import com.badlogic.ashley.core.PooledEngine;
import com.base2.roguestar.RogueStarClient;
import com.base2.roguestar.entities.systems.CameraSystem;
import com.base2.roguestar.entities.systems.MovementSystem;

/**
 * Created by Chris on 28/03/2016.
 */
public class EntityManager {

    public PooledEngine engine;

    public void init(RogueStarClient game) {
        engine = new PooledEngine();

        engine.addSystem(new MovementSystem());
        engine.addSystem(new CameraSystem());

        engine.getSystem(CameraSystem.class).setCamera(game.camera);

    }

    public void update(float delta) {
        engine.update(delta);
    }

}
