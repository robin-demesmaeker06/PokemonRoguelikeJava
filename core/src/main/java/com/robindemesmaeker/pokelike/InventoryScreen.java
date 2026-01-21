package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class InventoryScreen extends ScreenAdapter {
    
    final MainGame game;
    
    // MVC
    InventoryController controller;
    
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    BitmapFont font;
    OrthographicCamera camera;
    
    Texture playerTexture;
    Texture allyTexture; 

    public InventoryScreen(MainGame game) {
        this.game = game;
        this.controller = new InventoryController(game, this);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        
        playerTexture = new Texture("player.png");
        allyTexture = new Texture("placeholder-64x64.png");
    }

    @Override
    public void render(float delta) {
        // 1. UPDATE CONTROLLER
        controller.update(delta);

        // 2. CLEAR
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // 3. DRAW TEXT
        batch.begin();
        font.getData().setScale(2);
        font.setColor(Color.YELLOW);
        
        font.draw(batch, "INVENTORY", 320, 580); 
        
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
        
        int potionCount = game.inventory.getOrDefault(Item.POTION, 0);
        int ballCount = game.inventory.getOrDefault(Item.POKEBALL, 0);
        
        font.draw(batch, "Potions: " + potionCount, 50, 540);
        font.draw(batch, "Pokeballs: " + ballCount, 300, 540);
        
        // Updated Instructions
        font.draw(batch, "[Arrows] Move   [H] Heal   [I] Return", 450, 540);
        batch.end();

        // 4. DRAW TEAM SQUAD
        float startX = 50;
        float startY = 350; 
        
        float gapX = 150;   
        float gapY = 140;   
        
        for (int i = 0; i < 10; i++) {
            int row = i / 5; 
            int col = i % 5; 
            
            float x = startX + (col * gapX);
            float y = startY - (row * gapY);
            
            // Draw Box Background
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            
            // --- NEW: CURSOR HIGHLIGHT ---
            if (i == controller.cursorIndex) {
                shapeRenderer.setColor(Color.YELLOW); // Highlight
                shapeRenderer.rect(x - 15, y - 15, 140, 140); // Slightly larger border
            }
            
            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(x - 10, y - 10, 130, 130); 
            shapeRenderer.end();
            
            if (i < game.team.size()) {
                Pokemon p = game.team.get(i);
                
                batch.begin();
                batch.setColor(Color.WHITE);
                Texture tex = (i == 0) ? playerTexture : allyTexture; 
                
                batch.draw(tex, x + 23, y + 25, 64, 64);
                
                font.getData().setScale(1.2f);
                if (i == game.currentMemberIndex) font.setColor(Color.GREEN); 
                else font.setColor(Color.WHITE);
                
                font.draw(batch, p.species.name, x, y + 115);
                
                font.setColor(Color.WHITE);
                font.getData().setScale(1.0f);
                font.draw(batch, "Lvl " + p.level, x + 80, y + 115);
                batch.end();
                
                drawMiniHealthBar(x, y, p);
            } else {
                batch.begin();
                font.setColor(Color.GRAY);
                font.getData().setScale(1.0f);
                font.draw(batch, "Empty", x + 35, y + 60);
                batch.end();
            }
        }
    }
    
    private void drawMiniHealthBar(float x, float y, Pokemon p) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(x, y - 5, 110, 10);
        
        float pct = (float)p.currentHp / p.maxHp;
        if (pct < 0) pct = 0;
        
        if (pct > 0.5f) shapeRenderer.setColor(Color.GREEN);
        else if (pct > 0.2f) shapeRenderer.setColor(Color.YELLOW);
        else shapeRenderer.setColor(Color.RED);
        
        shapeRenderer.rect(x, y - 5, 110 * pct, 10);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
        playerTexture.dispose();
        allyTexture.dispose();
    }
}