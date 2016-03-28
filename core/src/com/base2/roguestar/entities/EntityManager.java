package com.base2.roguestar.entities;

import com.badlogic.ashley.core.PooledEngine;

/**
 * Created by Chris on 28/03/2016.
 */
public class EntityManager {

    public PooledEngine engine;

    public void init() {
        engine = new PooledEngine();
    }

    public void update(float delta) {

        engine.update(delta);
    }

}
