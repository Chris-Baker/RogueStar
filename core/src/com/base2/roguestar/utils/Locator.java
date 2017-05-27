package com.base2.roguestar.utils;

import com.base2.roguestar.entities.EntityManager;
import com.base2.roguestar.events.EventManager;
import com.base2.roguestar.game.GameManager;
import com.base2.roguestar.maps.MapManager;
import com.base2.roguestar.physics.PhysicsManager;

public class Locator {

    private static EventManager events;
    private static PhysicsManager physics;
    private static EntityManager entities;
    private static MapManager maps;
    private static GameManager game;

    public static void provide(EventManager events) {
        Locator.events = events;
    }

    public static void provide(EntityManager entities) {
        Locator.entities = entities;
    }

    public static void provide(PhysicsManager physics) {
        Locator.physics = physics;
    }

    public static void provide(MapManager maps) {
        Locator.maps = maps;
    }

    public static void provide(GameManager game) {
        Locator.game = game;
    }

    public static EventManager getEventManager() {
        return events;
    }

    public static PhysicsManager getPhysicsManager() {
        return physics;
    }

    public static EntityManager getEntityManager() {
        return entities;
    }

    public static MapManager getMapManager() {
        return maps;
    }

    public static GameManager getGameManager() {
        return game;
    }
}
