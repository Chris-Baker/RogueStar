package com.base2.roguestar.entities.components;

import com.badlogic.ashley.core.Component;
import com.base2.roguestar.controllers.CharacterController;
import com.base2.roguestar.controllers.CharacterControllerSnapshot;

public class ControllerComponent implements Component{

    public CharacterController controller;
    public CharacterControllerSnapshot snapshot;

}
