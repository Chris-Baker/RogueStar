package com.base2.roguestar.entities.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.base2.roguestar.controllers.KeyboardController;
import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.entities.components.CharacterComponent;
import com.base2.roguestar.entities.components.ControllerComponent;
import com.base2.roguestar.entities.components.RunSpeedComponent;
import com.base2.roguestar.events.EventManager;
import com.base2.roguestar.events.messages.PlayerInputEvent;
import com.base2.roguestar.utils.Locator;

public class PlayerInputEventSystem extends IteratingSystem {

    private ComponentMapper<ControllerComponent> cm;
    private EventManager events;
    private EntityManager entities;

    public PlayerInputEventSystem() {
        super(Family.all(ControllerComponent.class, CharacterComponent.class, RunSpeedComponent.class).get());

        cm = ComponentMapper.getFor(ControllerComponent.class);
        events = Locator.getEventManager();
        entities = Locator.getEntityManager();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        // get our controller component and input listener
        ControllerComponent c = cm.get(entity);
        KeyboardController controller = c.controller;

        // create an event containing the input data
        PlayerInputEvent event = new PlayerInputEvent();
        event.uid = entities.getUUID(entity);
        event.jump = controller.jump;
        event.moveLeft = controller.moveLeft;
        event.moveRight = controller.moveRight;
        events.queue(event);
    }
}
