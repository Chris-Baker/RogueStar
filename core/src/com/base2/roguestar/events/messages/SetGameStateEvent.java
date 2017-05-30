package com.base2.roguestar.events.messages;

import com.base2.roguestar.events.Event;
import com.base2.roguestar.game.GameState;

public class SetGameStateEvent implements Event {
    public GameState state;
}
