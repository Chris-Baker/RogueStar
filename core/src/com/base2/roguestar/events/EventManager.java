package com.base2.roguestar.events;

import com.badlogic.gdx.utils.Array;

public class EventManager {

    private Array<Event> queue = new Array<Event>();
    private Array<EventSubscriber> subscribers = new Array<EventSubscriber>();

    public EventManager() {

    }

    public void subscribe(EventSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(EventSubscriber subscriber) {
        subscribers.removeValue(subscriber, false);
    }

    public void update() {

        for (Event event: queue) {
            for (EventSubscriber subscriber: subscribers) {
                subscriber.handleEvent(event);
            }
        }
    }
}
