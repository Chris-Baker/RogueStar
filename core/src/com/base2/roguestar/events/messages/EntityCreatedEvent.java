package com.base2.roguestar.events.messages;

import com.base2.roguestar.events.Event;

import java.util.UUID;

public class EntityCreatedEvent implements Event {

    public UUID uid;
    public String type;
    public float x;
    public float y;
    public float rotation;
}
