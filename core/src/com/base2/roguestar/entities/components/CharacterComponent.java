package com.base2.roguestar.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Created by Chris on 28/03/2016.
 */
public class CharacterComponent implements Component {

    public Body body;
    public Fixture physicsFixture;
    public Fixture sensorFixture;
    public boolean isGrounded = false;
}
