package com.robindemesmaeker.pokelike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldManager {
    
    // DATA: What exists in the current world
    public int[][] currentMap;
    public List<GridEntity> currentEnemies;
    
    // HISTORY: Remembering where we have been
    private Map<String, WorldData> worldCache; 
    public int currentWorldX = 0;
    public int currentWorldY = 0;
    
    // SETTINGS
    private final int mapWidth = 50;
    private final int mapHeight = 50;

    public WorldManager() {
        worldCache = new HashMap<>();
        // Initialize the first world immediately
        loadWorld(0, 0);
    }

    // 1. Add the list variable
    public List<GridEntity> currentItems;

    // 2. Update loadWorld()
    public void loadWorld(int x, int y) {
        // Save old world (Add items to constructor)
        if (currentMap != null) {
            String key = currentWorldX + "," + currentWorldY;
            worldCache.put(key, new WorldData(currentMap, currentEnemies, currentItems));
        }

        currentWorldX = x;
        currentWorldY = y;
        String newKey = currentWorldX + "," + currentWorldY;

        if (worldCache.containsKey(newKey)) {
            WorldData data = worldCache.get(newKey);
            this.currentMap = data.map;
            this.currentEnemies = data.enemies;
            this.currentItems = data.items; // Load items
        } else {
            this.currentMap = MapGenerator.generateSurface(50, 50);
            this.currentEnemies = new ArrayList<>();
            this.currentItems = new ArrayList<>(); // New empty list
        }
    }
    
    // 3. Helper to remove items
    public void removeItem(GridEntity item) {
        currentItems.remove(item);
    }

    public void removeEnemy(Pokemon deadPokemon) {
        GridEntity toRemove = null;
        for (GridEntity entity : currentEnemies) {
            if (entity.pokemonData == deadPokemon) {
                toRemove = entity;
                break;
            }
        }
        if (toRemove != null) currentEnemies.remove(toRemove);
    }
    
    // Helper: Is this tile a wall?
    public boolean isWall(int x, int y) {
        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight) return false; 
        return currentMap[mapHeight - 1 - y][x] == 1;
    }

    public int getWidth() { return mapWidth; }
    public int getHeight() { return mapHeight; }
}