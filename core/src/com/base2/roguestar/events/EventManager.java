package com.base2.roguestar.events;

import com.badlogic.gdx.utils.Array;

public class EventManager {

    private Array<Event> queue;
    private Array<EventSubscriber> subscribers;

    public void init() {
        queue = new Array<Event>();
        subscribers = new Array<EventSubscriber>();
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
