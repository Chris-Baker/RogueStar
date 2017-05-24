package com.base2.roguestar.events;

import com.badlogic.gdx.utils.Array;
import com.base2.roguestar.events.messages.CreateEntityEvent;

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

    public void queue(Event event) {
        this.queue.add(event);
    }

    public void update() {

        for (Event event: queue) {

            System.out.println("Dispatch event " + event.getClass());

            for (EventSubscriber subscriber: subscribers) {
                subscriber.handleEvent(event);
            }
        }
        queue.clear();
    }
}
