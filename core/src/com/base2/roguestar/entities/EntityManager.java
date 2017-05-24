package com.base2.roguestar.entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.base2.roguestar.entities.systems.CameraSystem;
import com.base2.roguestar.entities.systems.MovementSystem;
import com.base2.roguestar.events.Event;
import com.base2.roguestar.events.EventSubscriber;
import com.base2.roguestar.events.messages.CreateEntityEvent;
import com.base2.roguestar.events.messages.EntityCreatedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityManager implements EventSubscriber {

    private PooledEngine engine;
    private Map<UUID, Entity> uidToEntity;
    private Map<Entity, UUID> EntityToUid;
    private EntityBuilder builder;

    public void init() {

        uidToEntity = new HashMap<UUID, Entity>();
        EntityToUid = new HashMap<Entity, UUID>();

        engine = new PooledEngine();
        engine.addSystem(new MovementSystem());

        builder = new EntityBuilder();
        builder.init();
    }

    public void setCamera(OrthographicCamera camera) {
        engine.addSystem(new CameraSystem());
        engine.getSystem(CameraSystem.class).setCamera(camera);
    }

    public void update(float delta) {
        engine.update(delta);
    }

    public Entity createEntity() {

        UUID uid = UUID.randomUUID();
        return createEntity(uid);
    }

    public Entity createEntity(UUID uid) {

        Entity e = engine.createEntity();
        uidToEntity.put(uid, e);
        EntityToUid.put(e, uid);
        return e;
    }

    public UUID getUUID(Entity e) {
        return EntityToUid.get(e);
    }

    public Entity getEntity(UUID uid) {
        return uidToEntity.get(uid);
    }

    public <T extends Component> T createComponent(Class<T> component) {
        return engine.createComponent(component);
    }

    public void addEntity(Entity e) {
        engine.addEntity(e);
    }

    @Override
    public void handleEvent(Event event) {

        if (event instanceof CreateEntityEvent) {
            CreateEntityEvent ce = (CreateEntityEvent)event;
            builder.create(ce.uid, ce.type, ce.x, ce.y, ce.rotation);
        }

    }
}
