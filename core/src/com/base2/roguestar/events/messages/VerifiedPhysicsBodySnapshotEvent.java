package com.base2.roguestar.events.messages;

import com.base2.roguestar.events.Event;
import com.base2.roguestar.physics.PhysicsBodySnapshot;

public class VerifiedPhysicsBodySnapshotEvent implements Event {
    public PhysicsBodySnapshot snapshot;
}
