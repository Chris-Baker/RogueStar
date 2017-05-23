package com.base2.roguestar.network.messages;

import com.base2.roguestar.network.Message;

/**
 * Created by chrisb on 10/05/2017.
 */
public class CharacterControllerMessage extends Message {

    public boolean moveLeft;
    public boolean moveRight;
    public boolean jump;
}
