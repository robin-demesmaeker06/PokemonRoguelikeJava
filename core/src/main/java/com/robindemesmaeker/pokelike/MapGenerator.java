package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.math.MathUtils;

public class MapGenerator {
    
    // 0 = Floor, 1 = Wall
    public static int[][] generateDungeon(int width, int height, int maxSteps) {
        // 1. Create a solid block of walls
        int[][] map = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = 1; 
            }
        }

        // 2. Start the "Miner" in the center
        int x = width / 2;
        int y = height / 2;
        map[y][x] = 0; // The starting point is always floor

        // 3. Walk randomly
        for (int i = 0; i < maxSteps; i++) {
            // Pick random direction: 0=Up, 1=Down, 2=Left, 3=Right
            int dir = MathUtils.random(3);

            if (dir == 0 && y < height - 2) y++;
            else if (dir == 1 && y > 1) y--;
            else if (dir == 2 && x > 1) x--;
            else if (dir == 3 && x < width - 2) x++;

            // Dig!
            map[y][x] = 0;
        }

        return map;
    }
}