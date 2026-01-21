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
    int[][] worldMap = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 1, 1, 1, 0, 0, 0, 1}, // Obstacle in middle
        {1, 0, 0, 1, 0, 1, 0, 0, 0, 1},
        {1, 0, 0, 1, 0, 1, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    int mapWidth = 10;
    int mapHeight = 9;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer(); 
        
        try {
            playerTexture = new Texture("player.png"); 
        } catch (Exception e) {}

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // Start at index [2, 2] (Safe spot inside walls)
        targetX = 2 * TILE_SIZE;
        targetY = 2 * TILE_SIZE;
        currentX = targetX;
        currentY = targetY;
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
                // Read the array reversed: map[y][x] 
                // (Because visual Y goes UP, but array index goes DOWN)
                // We flip y index: (mapHeight - 1 - y) so the map looks like the code
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

        // Draw Player
        batch.begin();
        if (playerTexture != null) {
            batch.draw(playerTexture, currentX, currentY, TILE_SIZE, TILE_SIZE);
        }
        batch.end();
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
        // Calculate Grid Coordinates of where we WANT to go
        // (Current Position / 64) = Grid Index
        int currentGridX = Math.round(currentX / TILE_SIZE);
        int currentGridY = Math.round(currentY / TILE_SIZE);

        int nextGridX = currentGridX + dx;
        int nextGridY = currentGridY + dy;

        // --- COLLISION CHECK ---
        
        // 1. Check Bounds (Don't walk off the array)
        if (nextGridX < 0 || nextGridX >= mapWidth || nextGridY < 0 || nextGridY >= mapHeight) {
            return;
        }

        // 2. Check Wall (Is it a '1'?)
        // Remember the Y-flip: (mapHeight - 1 - y)
        int tileType = worldMap[mapHeight - 1 - nextGridY][nextGridX];
        if (tileType == 1) {
            return; // STOP! It's a wall.
        }

        // If we survived the checks, MOVE!
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
}