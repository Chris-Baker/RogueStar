package com.base2.roguestar.controllers;

public class CharacterControllerSnapshot extends CharacterController {

    public void set(CharacterController controller) {
        jump = controller.jump;
        moveLeft = controller.moveLeft;
        moveRight = controller.moveRight;
    }
}
