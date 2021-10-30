package com.c1games.terminal.algo.units;

import com.c1games.terminal.algo.Coords;
import com.c1games.terminal.algo.GameIO;
import com.c1games.terminal.algo.map.GameState;

public class Action {
    public Coords location;
    public UnitType unitType;

    public Action(UnitType unitType, Coords location) {
        this.unitType = unitType;
        this.location = location;
    }
}
