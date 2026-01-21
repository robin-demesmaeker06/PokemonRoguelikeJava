package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture playerTexture;
    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;

    final float TILE_SIZE = 64f; 
    
    // Position Tracking
    float currentX, currentY; 
    float targetX, targetY;   
    float startX, startY;     
    
    // Movement State
    boolean isMoving = false;
    float moveTimer = 0f;
    final float MOVE_TIME = 0.2f; // Slightly faster for snappier feel

    // --- NEW: THE MAP DATA ---
    // 0 = Empty Floor
    // 1 = Wall
    int[][] worldMap;
    int mapWidth = 50;
    int mapHeight = 50;

    java.util.List<GridEntity> wildPokemonList;
    Texture enemyTexture;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer(); 
        
        try {
            playerTexture = new Texture("player.png"); 
        } catch (Exception e) {}

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        enemyTexture = new Texture("player.png");
        wildPokemonList = new java.util.ArrayList<>();

        worldMap = MapGenerator.generateDungeon(mapWidth, mapHeight, 1000);

        placePlayerOnFloor();

        spawnEnemies(10);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        // 1. MOVEMENT LOGIC
        if (isMoving) {
            moveTimer += deltaTime;
            float alpha = moveTimer / MOVE_TIME;
            
            if (alpha >= 1f) {
                currentX = targetX;
                currentY = targetY;
                isMoving = false;
            } else {
                currentX = Interpolation.linear.apply(startX, targetX, alpha);
                currentY = Interpolation.linear.apply(startY, targetY, alpha);
            }
        } else {
            handleInput();
        }

        // Camera Follow
        camera.position.set(currentX + TILE_SIZE/2, currentY + TILE_SIZE/2, 0);
        camera.update();

        ScreenUtils.clear(0, 0, 0, 1); 
        
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // --- 2. DRAW MAP ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                int tileType = worldMap[mapHeight - 1 - y][x];

                if (tileType == 1) {
                    shapeRenderer.setColor(0.5f, 0.5f, 0.8f, 1); // Blue Walls
                    shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                } else {
                    shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1); // Dark Floor
                    shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        shapeRenderer.end();

        // --- 3. DRAW SPRITES ---
        batch.begin();

        // A. DRAW ENEMIES
        for (GridEntity enemy : wildPokemonList) {
            batch.setColor(1, 0.5f, 0.5f, 1); // Tint Red
            batch.draw(enemy.texture, enemy.x, enemy.y, 64f, 64f);
        }
        
        // Reset color before drawing player!
        batch.setColor(1, 1, 1, 1); 

        // B. DRAW PLAYER (Once, after enemies)
        if (playerTexture != null) {
            batch.draw(playerTexture, currentX, currentY, TILE_SIZE, TILE_SIZE);
        }       
        
        batch.end(); // <--- Closed ONLY after everything is drawn
    }

    private void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            movePlayer(1, 0);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            movePlayer(-1, 0);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            movePlayer(0, 1);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            movePlayer(0, -1);
        }
    }

    private void movePlayer(int dx, int dy) {
        // Calculate where we WANT to go
        int currentGridX = Math.round(currentX / TILE_SIZE);
        int currentGridY = Math.round(currentY / TILE_SIZE);

        int nextGridX = currentGridX + dx;
        int nextGridY = currentGridY + dy;

        // --- 1. BOUNDS CHECK ---
        if (nextGridX < 0 || nextGridX >= mapWidth || nextGridY < 0 || nextGridY >= mapHeight) {
            return;
        }

        // --- 2. WALL CHECK ---
        // Remember the Y-flip: (mapHeight - 1 - y)
        int tileType = worldMap[mapHeight - 1 - nextGridY][nextGridX];
        if (tileType == 1) {
            return; // Hit a wall
        }

        // --- 3. ENTITY CHECK (NEW!) ---
        for (GridEntity enemy : wildPokemonList) {
            if (enemy.gridX == nextGridX && enemy.gridY == nextGridY) {
                // WE HIT AN ENEMY!
                System.out.println("BATTLE STARTED with " + enemy.pokemonData.species.name + "!");
                
                // Optional: Knockback animation or visual flair could go here later
                return; // STOP moving. Do not walk onto the enemy tile.
            }
        }

        // If we passed all checks, MOVE!
        startX = currentX;
        startY = currentY;
        targetX = nextGridX * TILE_SIZE;
        targetY = nextGridY * TILE_SIZE;
        
        isMoving = true;
        moveTimer = 0f;
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        if (playerTexture != null) playerTexture.dispose();
    }

    private void placePlayerOnFloor() {
    int attempts = 0;
    while (attempts < 1000) {
        // Pick a random spot
        int testX = com.badlogic.gdx.math.MathUtils.random(0, mapWidth - 1);
        int testY = com.badlogic.gdx.math.MathUtils.random(0, mapHeight - 1);

        // Check if it is a Floor (0)
        // Remember the Y-flip logic: worldMap[height - 1 - y][x]
        if (worldMap[mapHeight - 1 - testY][testX] == 0) {
            // Found a valid spot! Set and break.
            currentX = testX * TILE_SIZE;
            currentY = testY * TILE_SIZE;
            targetX = currentX;
            targetY = currentY;
            
            // Also update the camera immediately so we don't see a "jump"
            camera.position.set(currentX, currentY, 0);
            return;
            }
        attempts++;
        }
    System.err.println("Could not find a floor tile!");
    }

    private void spawnEnemies(int count) {
    Species ratSpec = new Species("Rattata", 30, 10, "Normal");
    
    for(int i = 0; i < count; i++) {
        // Find a random floor tile (reuse your spawn logic or copy-paste it)
        int ex = 0, ey = 0;
        while(true) {
            ex = com.badlogic.gdx.math.MathUtils.random(0, mapWidth - 1);
            ey = com.badlogic.gdx.math.MathUtils.random(0, mapHeight - 1);
            if (worldMap[mapHeight - 1 - ey][ex] == 0) break;
        }
        
        // Create the Data
        Pokemon wildMon = new Pokemon(ratSpec, 3);
        
        // Create the Visual Entity
        GridEntity enemy = new GridEntity(wildMon, enemyTexture, ex, ey);
        wildPokemonList.add(enemy);
        }
    }
}