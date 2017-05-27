package com.base2.roguestar.events.messages;

import com.base2.roguestar.events.Event;

import java.util.UUID;

public class AddPlayerEvent implements Event {
    public UUID uid;
    public boolean isLocalPlayer;
}
