package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class DungeonScreen extends ScreenAdapter {
    
    final MainGame game;

    // Sub-systems
    WorldManager worldManager;
    PlayerController controller;

    // Visuals
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    Texture playerTexture;
    OrthographicCamera camera;
    final float TILE_SIZE = 64f;

    public DungeonScreen(MainGame game) {
        this.game = game;
        
        // Initialize Sub-systems
        this.worldManager = new WorldManager();
        this.controller = new PlayerController(game, this, worldManager);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        playerTexture = new Texture("player.png");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // Initial Spawn if at 0,0
        if (controller.currentX == 0 && controller.currentY == 0) {
            controller.spawnPlayerSafe();
        }
    }

    @Override
    public void render(float delta) {
        // 1. UPDATE CONTROLLER
        controller.update(delta);

        // 2. UPDATE CAMERA
        camera.position.set(controller.currentX + TILE_SIZE/2, controller.currentY + TILE_SIZE/2, 0);
        camera.update();

        // 3. RENDER
        ScreenUtils.clear(0, 0, 0, 1);
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw Map
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < worldManager.getWidth(); x++) {
            for (int y = 0; y < worldManager.getHeight(); y++) {
                int tileType = worldManager.currentMap[worldManager.getHeight() - 1 - y][x];
                if (tileType == 1) shapeRenderer.setColor(0.1f, 0.4f, 0.1f, 1); 
                else shapeRenderer.setColor(0.3f, 0.7f, 0.3f, 1); 
                shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        shapeRenderer.end();

        // Draw Entities
        batch.begin();
        for (GridEntity enemy : worldManager.currentEnemies) {
            batch.setColor(1, 0.5f, 0.5f, 1); // Tint red just in case
            batch.draw(enemy.texture, enemy.x, enemy.y, TILE_SIZE, TILE_SIZE);
        }

        // Draw Items (Yellow Tint)
        for (GridEntity item : worldManager.currentItems) {
            batch.setColor(1, 1, 0, 1); 
            batch.draw(item.texture, item.x, item.y, TILE_SIZE, TILE_SIZE);
        }
        
        // Draw Player
        batch.setColor(1, 1, 1, 1);
        batch.draw(playerTexture, controller.currentX, controller.currentY, TILE_SIZE, TILE_SIZE);
        batch.end();
    }
    
    // Pass-through for BattleScreen
    public void removeEnemy(Pokemon deadPokemon) {
        worldManager.removeEnemy(deadPokemon);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        if (playerTexture != null) playerTexture.dispose();
    }
}