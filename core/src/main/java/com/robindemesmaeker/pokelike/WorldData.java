package com.robindemesmaeker.pokelike;

import java.util.List;

public class WorldData {
    public int[][] map;
    public List<GridEntity> enemies;
    public List<GridEntity> items; // NEW

    public WorldData(int[][] map, List<GridEntity> enemies, List<GridEntity> items) {
        this.map = map;
        this.enemies = enemies;
        this.items = items;
    }
}