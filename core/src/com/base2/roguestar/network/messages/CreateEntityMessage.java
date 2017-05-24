package com.base2.roguestar.network.messages;

import com.base2.roguestar.network.Message;

public class CreateEntityMessage extends Message {

    public String uid;
    public String type;
    public float x;
    public float y;
    public float rotation;
}
