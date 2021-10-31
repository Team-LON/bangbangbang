package com.c1games.terminal.starteralgo;

import com.c1games.terminal.algo.*;
import com.c1games.terminal.algo.io.GameLoop;
import com.c1games.terminal.algo.io.GameLoopDriver;
import com.c1games.terminal.algo.map.GameState;
import com.c1games.terminal.algo.map.MapBounds;
import com.c1games.terminal.algo.map.SpawnCommand;
import com.c1games.terminal.algo.map.Unit;
import com.c1games.terminal.algo.serialization.JsonDeserializeClassFromTuple;
import com.c1games.terminal.algo.serialization.JsonSerializeClassToTuple;
import com.c1games.terminal.algo.units.Action;
import com.c1games.terminal.algo.units.UnitType;
import com.c1games.terminal.algo.units.UnitTypeAtlas;
import com.google.gson.JsonElement;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Java implementation of the standard starter algo.
 */
public class StarterAlgo implements GameLoop {
    public static void main(String[] args) {
        new GameLoopDriver(new StarterAlgo()).run();
    }

    private static final Coords[] wallProtectTurrets = {
            new Coords(8, 12),
            new Coords(19, 12)
    };

    private static final Coords[] defensiveTurretLocations = {
            new Coords(0, 13),
            new Coords(27, 13),
            new Coords(8, 11),
            new Coords(19, 11),
            new Coords(13, 11),
            new Coords(14, 11)
    };

    private static final Coords[] supportLocations = {
            new Coords(13, 2),
            new Coords(14, 2),
            new Coords(13, 3),
            new Coords(14, 3)
    };

    private final Random rand = new Random();

    private ArrayList<Coords> scoredOnLocations = new ArrayList<>();

    @Override
    public void initialize(GameIO io, Config config) {
        GameIO.debug().println("Configuring your custom java algo strategy...");
        long seed = rand.nextLong();
        rand.setSeed(seed);
        GameIO.debug().println("Set random seed to: " + seed);
    }

    /*
    Update the gameState according to the action
     */
    public GameState update(GameState gameState, Action action) {
        gameState.spawn(action.location, action.unitType);
        return gameState;
    }

    private ArrayList<Coords> getStructureUnitLocations(GameState gameState) {
        ArrayList<Coords> coords = new ArrayList<Coords>();
        //for (FrameData.PlayerUnitList structureType :
                //List.of(gameState.data.p1Units.wall, gameState.data.p1Units.turret, gameState.data.p1Units.support)) {
        for (FrameData.PlayerUnit unit : gameState.data.p1Units.wall) {
            coords.add(new Coords(unit.x, unit.y));
        }
        for (FrameData.PlayerUnit unit : gameState.data.p1Units.turret) {
            coords.add(new Coords(unit.x, unit.y));
        }
        for (FrameData.PlayerUnit unit : gameState.data.p1Units.support) {
            coords.add(new Coords(unit.x, unit.y));
        }
        return coords;
    }

    public Action generateRandomStructureAction(GameState gameState) {
        Action action;
        UnitType unitType;
        ArrayList<Coords> coords;
        Coords location;
        Random random_method;
        do {
            // random generate a unit type
            unitType = UnitType.getRandomStructure();
            // according to action type, random generate a location
            switch (unitType) {
            case Upgrade:
                // fallthrough
            case Remove:
                coords = getStructureUnitLocations(gameState);
                break;
            default:
                coords = MapBounds.getBottomGrid();
            }
            // get a random coordinate from coords
            random_method = new Random();
            int index = random_method.nextInt(coords.size());
            location = coords.get(index);
        } while (!gameState.canSpawn(location, unitType, 1).affirmative());

        action = new Action(unitType, location);

        return action;
    }

    public Action generateRandomMobileAction(GameState gameState) {
        Action action;
        UnitType unitType;
        ArrayList<Coords> coords;
        Coords location;
        Random random_method;
        do {
            // random generate a unit type
            unitType = UnitType.getRandomMobile();
            // according to action type, random generate a location
            coords = MapBounds.getBottomEdges();
            random_method = new Random();
            int index = random_method.nextInt(coords.size());
            location = coords.get(index);
        } while (!gameState.canSpawn(location, unitType, 1).affirmative());

        action = new Action(unitType, location);

        return action;
    }

    public ArrayList<Action> generateRandomActions(GameState gameState) {
        // maintain a deep copy of the game state  --> gameStateCopy
//        Gson gson = new Gson();
//        String deepCopy = gson.toJson(gameState);
//        GameState gameStateCopy = gson.fromJson(deepCopy, GameState.class);

        // UnitTypeAtlas atlas = new UnitTypeAtlas(gameState.config);
        // Gson gson = FrameData.gson(atlas);
        // FrameData data = gson.fromJson(gameState.data.toString(), FrameData.class);
        GameState gameStateCopy = new GameState(gameState.config, gameState.data);

        ArrayList<Action> actions = new ArrayList<>();
        Action action;
        while (actions.size() < 1) {
            action = generateRandomStructureAction(gameStateCopy);
            actions.add(action);
            gameStateCopy = update(gameStateCopy, action);
        }
        while (actions.size() < 2) {
            action = generateRandomMobileAction(gameStateCopy);
            actions.add(action);
            gameStateCopy = update(gameStateCopy, action);
        }
        return actions;
    }

    /**
     * Make a move in the game.
     */
    @Override
    public void onTurn(GameIO io, GameState gameState) {
        GameIO.debug().println("Performing turn " + gameState.data.turnInfo.turnNumber + " of your custom algo strategy");
        ArrayList<Action> actions = generateRandomActions(gameState);
        for (Action action : actions) {
            switch (action.unitType) {
            case Wall:
                gameState.attemptSpawn(action.location, UnitType.Wall);
                GameIO.debug().println("Building a wall at " + action.location);
                break;
            case Turret:
                gameState.attemptSpawn(action.location, UnitType.Turret);
                GameIO.debug().println("Building a turret at " + action.location);
                break;
            case Support:
                gameState.attemptSpawn(action.location, UnitType.Support);
                GameIO.debug().println("Building a support at " + action.location);
                break;
            case Scout:
                gameState.attemptSpawn(action.location, UnitType.Scout);
                GameIO.debug().println("Spawning a scout at " + action.location);
                break;
            case Demolisher:
                gameState.attemptSpawn(action.location, UnitType.Demolisher);
                GameIO.debug().println("Spawning a demolisher at " + action.location);
                break;
            case Interceptor:
                gameState.attemptSpawn(action.location, UnitType.Interceptor);
                GameIO.debug().println("Spawning a interceptor at " + action.location);
                break;
            case Upgrade:
                gameState.attemptUpgrade(action.location);
                GameIO.debug().println("Upgrading " + action.location);
                break;
            case Remove:
                gameState.removeStructure(action.location);
                GameIO.debug().println("Removing " + action.location);
                break;
            default:
                GameIO.debug().println("Error: Invalid Unit Type");
            }
        }
    }

    /**
     * Save process action frames. Careful there are many action frames per turn!
     */
    @Override
    public void onActionFrame(GameIO io, GameState move) {
        // Save locations that the enemy scored on against us to reactively build defenses
        for (FrameData.Events.BreachEvent breach : move.data.events.breach) {
            if (breach.unitOwner != PlayerId.Player1) {
                scoredOnLocations.add(breach.coords);
            }
        }
    }

    // Once the C1 logo is made, attempt to build some defenses.
    private void buildDefenses(GameState move) {
        // First lets protect ourselves a little with turrets.
        move.attemptSpawnMultiple(Arrays.asList(defensiveTurretLocations), UnitType.Turret);
        // Lets protect our turrets with some walls.
        move.attemptSpawnMultiple(Arrays.asList(wallProtectTurrets), UnitType.Wall);
        // Lastly, lets upgrade those important walls that protect our turrets.
        move.attemptUpgradeMultiple(Arrays.asList(wallProtectTurrets));
    }

    /**
     * Build defenses reactively based on where we got scored on
     */
    private void buildReactiveDefenses(GameState move) {
        for (Coords loc : scoredOnLocations) {
            // Build 1 space above the breach location so that it doesn't block our spawn locations
            move.attemptSpawn(new Coords(loc.x, loc.y + 1), UnitType.Turret);
        }
    }

    /**
     * Deploy offensive units.
     */
    private void deployRandomInterceptors(GameState move) {
        /*
        Lets send out Interceptors to help destroy enemy mobile units.
        A complex algo would predict where the enemy is going to send units and
        develop its strategy around that. But this algo is simple so lets just
        send out interceptors in random locations and hope for the best.

        Mobile units can only deploy on our edges. 
        So lets get a list of those locations.
         */
        List<Coords> friendlyEdges = new ArrayList<>();
        friendlyEdges.addAll(Arrays.asList(MapBounds.EDGE_LISTS[MapBounds.EDGE_BOTTOM_LEFT]));
        friendlyEdges.addAll(Arrays.asList(MapBounds.EDGE_LISTS[MapBounds.EDGE_BOTTOM_RIGHT]));

        /*
        While we have remaining bits to spend lets send out interceptors randomly.
        */
        while (move.numberAffordable(UnitType.Interceptor) >= 1) {
            Coords c = friendlyEdges.get(rand.nextInt(friendlyEdges.size()));
            move.attemptSpawn(c, UnitType.Interceptor);
            /*
            We don't have to remove the location since multiple mobile units can occupy the same space. 
            Note that if all edge locations are blocked this will infinite loop!
             */
        }
    }

    /**
     * Goes through the list of locations, gets the path taken from them,
     * and loosely calculates how much damage will be taken by traveling that path assuming speed of 1.
     *
     * @param move
     * @param locations
     * @return
     */
    private Coords leastDamageSpawnLocation(GameState move, List<Coords> locations) {
        List<Float> damages = new ArrayList<>();

        for (Coords location : locations) {
            List<Coords> path = move.pathfind(location, MapBounds.getEdgeFromStart(location));
            float totalDamage = 0;
            for (Coords dmgLoc : path) {
                List<Unit> attackers = move.getAttackers(dmgLoc);
                for (Unit unit : attackers) {
                    totalDamage += unit.unitInformation.attackDamageWalker.orElse(0);
                }
            }
            GameIO.debug().println("Got dmg:" + totalDamage + " for " + location);
            damages.add(totalDamage);
        }

        int minIndex = 0;
        float minDamage = 9999999;
        for (int i = 0; i < damages.size(); i++) {
            if (damages.get(i) <= minDamage) {
                minDamage = damages.get(i);
                minIndex = i;
            }
        }
        return locations.get(minIndex);
    }

    /**
     * Counts the number of a units found with optional parameters to specify what locations and unit types to count.
     *
     * @param move       GameState
     * @param xLocations Can be null, list of x locations to check for units
     * @param yLocations Can be null, list of y locations to check for units
     * @param units      Can be null, list of units to look for, null will check all
     * @return count of the number of units seen at the specified locations
     */
    private int detectEnemyUnits(GameState move, List<Integer> xLocations, List<Integer> yLocations, List<UnitType> units) {
        if (xLocations == null) {
            xLocations = new ArrayList<Integer>();
            for (int x = 0; x < MapBounds.BOARD_SIZE; x++) {
                xLocations.add(x);
            }
        }
        if (yLocations == null) {
            yLocations = new ArrayList<Integer>();
            for (int y = 0; y < MapBounds.BOARD_SIZE; y++) {
                yLocations.add(y);
            }
        }

        if (units == null) {
            units = new ArrayList<>();
            for (Config.UnitInformation unit : move.config.unitInformation) {
                if (unit.startHealth.isPresent()) {
                    units.add(move.unitTypeFromShorthand(unit.shorthand.get()));
                }
            }
        }

        int count = 0;
        for (int x : xLocations) {
            for (int y : yLocations) {
                Coords loc = new Coords(x, y);
                if (MapBounds.inArena(loc)) {
                    for (Unit u : move.allUnits[x][y]) {
                        if (units.contains(u.type)) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    private void demolisherLineStrategy(GameState move) {
        /*
        First lets find the cheapest structure. We could hardcode this to "Wall",
        but lets demonstrate how to use java-algo features.
         */
        Config.UnitInformation cheapestUnit = null;
        for (Config.UnitInformation uinfo : move.config.unitInformation) {
            if (uinfo.unitCategory.isPresent() && move.isStructure(uinfo.unitCategory.getAsInt())) {
                float[] costUnit = uinfo.cost();
                if ((cheapestUnit == null || costUnit[0] + costUnit[1] <= cheapestUnit.cost()[0] + cheapestUnit.cost()[1])) {
                    cheapestUnit = uinfo;
                }
            }
        }
        if (cheapestUnit == null) {
            GameIO.debug().println("There are no structures?");
        }

        for (int x = 27; x >= 5; x--) {
            move.attemptSpawn(new Coords(x, 11), move.unitTypeFromShorthand(cheapestUnit.shorthand.get()));
        }

        for (int i = 0; i < 22; i++) {
            move.attemptSpawn(new Coords(24, 10), UnitType.Demolisher);
        }
    }

}
