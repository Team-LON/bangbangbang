package com.c1games.terminal.algo.units;

import com.c1games.terminal.algo.Config;

public enum UnitType {
    Scout,
    Demolisher,
    Interceptor,
    Wall,
    Support,
    Turret,     // Spawn

    Upgrade,
    Remove;  // attemptUpgrade

    public static UnitType getRandom() {
        return values()[(int) (Math.random() * (values().length - 2))];
    }

    public static UnitType getRandomMobile() {
        return values()[(int) (Math.random() * (4-0) + 0)];
    }

    public static UnitType getRandomStructure() {
        return values()[(int) (Math.random() * (8-4) + 4)];
    }

}
