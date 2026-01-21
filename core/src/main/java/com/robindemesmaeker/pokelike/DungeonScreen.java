package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

public class DungeonScreen extends ScreenAdapter {
    
    final MainGame game;

    // Rendering Tools
    SpriteBatch batch;
    Texture playerTexture;
    Texture enemyTexture;
    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;
    
    // Movement & World Settings
    final float TILE_SIZE = 64f;
    float currentX, currentY;
    float targetX, targetY;
    float startX, startY;
    
    boolean isMoving = false;
    float moveTimer = 0f;
    final float MOVE_TIME = 0.2f;

    // Dungeon Data
    int[][] worldMap;
    int mapWidth = 50;
    int mapHeight = 50;
    List<GridEntity> wildPokemonList;

    // --- 1. CONSTRUCTOR (Runs ONCE when game starts) ---
    public DungeonScreen(MainGame game) {
        this.game = game;
        
        // Initialize the list so it's ready
        wildPokemonList = new ArrayList<>();

        // Generate the Map immediately so it persists forever
        worldMap = MapGenerator.generateDungeon(mapWidth, mapHeight, 1000);
        
        // We defer player placement to show() to ensure assets are loaded first,
        // but the MAP data is safe here.
    }

    // --- 2. SHOW (Runs every time you switch to this screen) ---
    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Load Textures (Only if they aren't loaded yet)
        if (playerTexture == null) {
            try { playerTexture = new Texture("player.png"); } catch (Exception e) { System.err.println("Player texture missing"); }
        }
        if (enemyTexture == null) {
            try { enemyTexture = new Texture("player.png"); } catch (Exception e) {}
        }

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // --- SPAWN LOGIC (Only runs if we haven't started yet) ---
        
        // 1. If player is at 0,0 (default), find a spawn point
        if (currentX == 0 && currentY == 0) {
            placePlayerOnFloor();
        }

        // 2. If no enemies exist yet, spawn them
        if (wildPokemonList.isEmpty()) {
            spawnEnemies(10);
        }
    }

    @Override
    public void render(float delta) {
        // --- MOVEMENT INTERPOLATION ---
        if (isMoving) {
            moveTimer += delta;
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

        // --- DRAW MAP ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                // Flip Y axis to match visual direction
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

        // --- DRAW SPRITES ---
        batch.begin();
        
        // Draw Enemies
        for (GridEntity enemy : wildPokemonList) {
            batch.setColor(1, 0.5f, 0.5f, 1); // Red Tint
            batch.draw(enemy.texture, enemy.x, enemy.y, TILE_SIZE, TILE_SIZE);
        }
        
        // Draw Player
        batch.setColor(1, 1, 1, 1);
        if (playerTexture != null) {
            batch.draw(playerTexture, currentX, currentY, TILE_SIZE, TILE_SIZE);
        }
        batch.end();
    }

    private void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) movePlayer(1, 0);
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) movePlayer(-1, 0);
        else if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) movePlayer(0, 1);
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) movePlayer(0, -1);
    }

    private void movePlayer(int dx, int dy) {
        int currentGridX = Math.round(currentX / TILE_SIZE);
        int currentGridY = Math.round(currentY / TILE_SIZE);
        int nextGridX = currentGridX + dx;
        int nextGridY = currentGridY + dy;

        // 1. Bounds Check
        if (nextGridX < 0 || nextGridX >= mapWidth || nextGridY < 0 || nextGridY >= mapHeight) return;

        // 2. Wall Check
        if (worldMap[mapHeight - 1 - nextGridY][nextGridX] == 1) return;

        // 3. Enemy Collision Check
        for (GridEntity enemy : wildPokemonList) {
            if (enemy.gridX == nextGridX && enemy.gridY == nextGridY) {
                System.out.println("BATTLE START!");
                
                // Recoil / Wait logic
                isMoving = true;
                moveTimer = 0f;
                targetX = currentX;
                targetY = currentY;
                
                // Switch Screen
                game.setScreen(new BattleScreen(game, enemy.pokemonData)); 
                return;
            }
        }

        // Move
        startX = currentX;
        startY = currentY;
        targetX = nextGridX * TILE_SIZE;
        targetY = nextGridY * TILE_SIZE;
        isMoving = true;
        moveTimer = 0f;
    }

    // Helper: Find a safe spawn
    private void placePlayerOnFloor() {
        int attempts = 0;
        while (attempts < 1000) {
            int testX = MathUtils.random(0, mapWidth - 1);
            int testY = MathUtils.random(0, mapHeight - 1);
            if (worldMap[mapHeight - 1 - testY][testX] == 0) {
                currentX = testX * TILE_SIZE;
                currentY = testY * TILE_SIZE;
                targetX = currentX;
                targetY = currentY;
                camera.position.set(currentX, currentY, 0);
                return;
            }
            attempts++;
        }
    }

    // Helper: Spawn Enemies
    private void spawnEnemies(int count) {
        Species ratSpec = new Species("Rattata", 30, 10, "Normal");
        for(int i = 0; i < count; i++) {
            int ex = 0, ey = 0;
            while(true) {
                ex = MathUtils.random(0, mapWidth - 1);
                ey = MathUtils.random(0, mapHeight - 1);
                if (worldMap[mapHeight - 1 - ey][ex] == 0) break;
            }
            Pokemon wildMon = new Pokemon(ratSpec, 3);
            GridEntity enemy = new GridEntity(wildMon, enemyTexture, ex, ey);
            wildPokemonList.add(enemy);
        }
    }

    // Helper: Remove dead enemies after battle
    public void removeEnemy(Pokemon deadPokemon) {
        GridEntity toRemove = null;
        for (GridEntity entity : wildPokemonList) {
            if (entity.pokemonData == deadPokemon) {
                toRemove = entity;
                break;
            }
        }
        if (toRemove != null) {
            wildPokemonList.remove(toRemove);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        if (playerTexture != null) playerTexture.dispose();
        if (enemyTexture != null) enemyTexture.dispose();
    }
}