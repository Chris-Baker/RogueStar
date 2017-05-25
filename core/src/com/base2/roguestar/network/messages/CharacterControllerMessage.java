package com.base2.roguestar.network.messages;

import com.base2.roguestar.network.Message;

public class CharacterControllerMessage extends Message {

    public String uid;
    public boolean jump;
    public boolean moveLeft;
    public boolean moveRight;
}
