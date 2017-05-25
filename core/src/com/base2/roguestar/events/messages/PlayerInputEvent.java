package com.base2.roguestar.events.messages;

import com.base2.roguestar.events.Event;

import java.util.UUID;

public class PlayerInputEvent implements Event {

    public UUID uid;
    public boolean jump;
    public boolean moveLeft;
    public boolean moveRight;
}
