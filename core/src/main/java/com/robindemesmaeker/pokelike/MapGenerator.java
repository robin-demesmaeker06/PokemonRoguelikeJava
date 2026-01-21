package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.math.MathUtils;

public class MapGenerator {
    
    // --- 1. CAVE GENERATOR ---
    public static int[][] generateDungeon(int width, int height, int maxSteps) {
        int[][] map = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = 1; 
            }
        }
        int x = width / 2;
        int y = height / 2;
        map[y][x] = 0; 

        for (int i = 0; i < maxSteps; i++) {
            int dir = MathUtils.random(3);
            if (dir == 0 && y < height - 2) y++;
            else if (dir == 1 && y > 1) y--;
            else if (dir == 2 && x > 1) x--;
            else if (dir == 3 && x < width - 2) x++;
            map[y][x] = 0;
        }
        return map;
    }

    // --- 2. SURFACE GENERATOR ---
    public static int[][] generateSurface(int width, int height) {
        int[][] map = new int[height][width];

        // Fill with Grass
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = 0;
            }
        }

        // Scatter Obstacles (15%)
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (MathUtils.randomBoolean(0.15f)) {
                    map[y][x] = 1; 
                }
            }
        }

        // Clear Pathways
        int pathwayWidth = 2; 
        int centerX = width / 2;
        for (int y = 0; y < height; y++) {
            for (int i = -pathwayWidth; i <= pathwayWidth; i++) {
                if (centerX + i >= 0 && centerX + i < width) map[y][centerX + i] = 0;
            }
        }
        int centerY = height / 2;
        for (int x = 0; x < width; x++) {
            for (int i = -pathwayWidth; i <= pathwayWidth; i++) {
                if (centerY + i >= 0 && centerY + i < height) map[centerY + i][x] = 0;
            }
        }

        return map;
    }
}