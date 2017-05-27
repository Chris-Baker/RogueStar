package com.base2.roguestar.network.messages;

import com.base2.roguestar.network.Message;

public class JoinAsPlayerMessage extends Message {
    public String uid;
    public boolean isLocalPlayer;
}
