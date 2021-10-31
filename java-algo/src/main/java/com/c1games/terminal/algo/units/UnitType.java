package com.c1games.terminal.algo.units;

import com.c1games.terminal.algo.Config;

public enum UnitType {
    Wall,
    Support,
    Turret,
    Scout,
    Demolisher,
    Interceptor,  // Spawn

    Upgrade,
    Remove;  // attemptUpgrade

    public static UnitType getRandom() {
        return values()[(int) (Math.random() * (values().length - 2))];
    }

}
