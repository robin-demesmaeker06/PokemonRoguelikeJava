package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class BattleScreen extends ScreenAdapter {
    final MainGame game;
    
    // MVC Components
    BattleManager model;
    BattleController controller;

    // Visuals
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    BitmapFont font;
    OrthographicCamera camera;
    Texture playerTexture;
    Texture enemyTexture;

    public BattleScreen(MainGame game, Pokemon enemy) {
        this.game = game;
        
        // Initialize MVC
        // Note: game.playerPokemon is our global player state
        this.model = new BattleManager(game.playerPokemon, enemy);
        this.controller = new BattleController(game, model);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont(); 
        font.getData().setScale(2); 

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // Load Textures
        if (playerTexture == null) playerTexture = new Texture("player.png"); 
        
        // Use the placeholder for enemies now so they look different!
        if (enemyTexture == null) enemyTexture = new Texture("placeholder-64x64.png"); 
    }

    @Override
    public void render(float delta) {
        // 1. UPDATE LOGIC
        controller.update(delta);
        
        // 2. RENDER
        ScreenUtils.clear(0.9f, 0.9f, 0.9f, 1); 
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        drawUI();
    }

    private void drawUI() {
        // SHAPES
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Bottom Box
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(0, 0, 800, 150);
        
        // HP Bars (Read from Model)
        drawHealthBar(50, 320, model.player);
        drawHealthBar(520, 520, model.enemy);
        
        shapeRenderer.end();

        // TEXTURES & TEXT
        batch.begin();
        
        // Player (Blue Tint)
        batch.setColor(0.5f, 0.5f, 1f, 1); 
        batch.draw(playerTexture, 100, 160, 96, 96); 
        
        // Enemy (Red Tint)
        batch.setColor(1f, 0.5f, 0.5f, 1);
        batch.draw(enemyTexture, 570, 400, 96, 96); 
        
        batch.setColor(1, 1, 1, 1);

        font.setColor(Color.BLACK);
        font.draw(batch, "YOU: " + model.player.species.name, 50, 380);
        font.draw(batch, "ENEMY: " + model.enemy.species.name, 520, 580); 
        
        // Combat Message from Model
        font.setColor(Color.WHITE);
        font.draw(batch, model.combatMessage, 50, 100);
        
        // Show Controls only on Player Turn
        if (model.battleState == 0) {
            font.getData().setScale(1.5f);
            font.draw(batch, "[SPACE] Atk  [H] Potion  [C] Catch  [ESC] Run", 50, 50);
            font.getData().setScale(2f);
        }
        
        batch.end();
    }
    
    private void drawHealthBar(float x, float y, Pokemon p) {
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(x, y, 200, 25);
        
        shapeRenderer.setColor(Color.GREEN);
        float pct = (float)p.currentHp / p.maxHp; 
        if (pct < 0) pct = 0;
        if (pct > 1) pct = 1; 
        shapeRenderer.rect(x, y, 200 * pct, 25);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
        if(playerTexture != null) playerTexture.dispose();
        if(enemyTexture != null) enemyTexture.dispose();
    }
}