package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    Pokemon player;
    Pokemon enemy;

    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    BitmapFont font;
    OrthographicCamera camera;
    
    Texture playerTexture;
    Texture enemyTexture;

    // --- BATTLE STATE ---
    // 0 = Player Input, 1 = Enemy Turn, 2 = Win, 3 = Loss
    int battleState = 0; 
    float turnTimer = 0f;
    String combatMessage = "What will you do?";

    public BattleScreen(MainGame game, Pokemon enemy) {
        this.game = game;
        this.enemy = enemy;
        this.player = game.playerPokemon; 
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont(); 
        font.getData().setScale(2); 

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        if (playerTexture == null) playerTexture = new Texture("player.png"); 
        if (enemyTexture == null) enemyTexture = new Texture("player.png"); 
        
        // Reset state on start
        battleState = 0;
        combatMessage = "Wild " + enemy.species.name + " appeared!";
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.9f, 0.9f, 0.9f, 1); 
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // --- BATTLE LOGIC ---
        if (battleState == 0) {
            // PLAYER TURN: Wait for Input
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                // 1. Player Attacks
                int damage = 5 + (player.level); // Simple formula
                enemy.currentHp -= damage;
                combatMessage = "You hit for " + damage + " dmg!";
                
                // 2. Check Win
                if (enemy.currentHp <= 0) {
                    enemy.currentHp = 0;
                    battleState = 2; // Win State
                    combatMessage = "Enemy fainted! Press [SPACE].";
                } else {
                    // 3. Switch to Enemy Turn
                    battleState = 1;
                    turnTimer = 0f; // Reset timer
                }
            }
        } 
        else if (battleState == 1) {
            // ENEMY TURN: Wait 1 second, then attack
            turnTimer += delta;
            if (turnTimer > 1.0f) {
                int damage = 3 + (enemy.level); 
                player.currentHp -= damage;
                combatMessage = "Enemy hit you for " + damage + " dmg!";
                
                // Check Loss
                if (player.currentHp <= 0) {
                    player.currentHp = 0;
                    battleState = 3; // Loss State
                    combatMessage = "You fainted... Press [SPACE].";
                } else {
                    // Back to Player
                    battleState = 0; 
                }
            }
        }
        else if (battleState == 2) {
            // WIN SCREEN: Wait for one more press
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                game.dungeonScreen.removeEnemy(enemy); 
                game.setScreen(game.dungeonScreen); 
            }
        }
        else if (battleState == 3) {
            // LOSS SCREEN (Restart game? Respawn? For now, just exit)
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                Gdx.app.exit(); 
            }
        }
        
        // Run Away (Only allowed on Player Turn)
        if (battleState == 0 && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
             game.setScreen(game.dungeonScreen);
        }

        // --- DRAWING ---
        drawUI();
    }

    private void drawUI() {
        // SHAPES
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Bottom Box
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(0, 0, 800, 150);
        
        // HP Bars
        drawHealthBar(50, 320, player);
        drawHealthBar(520, 520, enemy);
        
        shapeRenderer.end();

        // TEXTURES & FONT
        batch.begin();
        
        batch.setColor(0.5f, 0.5f, 1f, 1); 
        batch.draw(playerTexture, 100, 160, 96, 96); 
        
        batch.setColor(1f, 0.5f, 0.5f, 1);
        batch.draw(enemyTexture, 570, 400, 96, 96); 
        
        batch.setColor(1, 1, 1, 1);

        font.setColor(Color.BLACK);
        font.draw(batch, "YOU: " + player.species.name, 50, 380);
        font.draw(batch, "ENEMY: " + enemy.species.name, 520, 580); 
        
        // DYNAMIC COMBAT MESSAGE
        font.setColor(Color.WHITE);
        font.draw(batch, combatMessage, 50, 100);
        
        // Sub-text
        if (battleState == 0) {
            font.getData().setScale(1.5f);
            font.draw(batch, "[SPACE] Attack    [ESC] Run", 50, 50);
            font.getData().setScale(2f);
        }
        
        batch.end();
    }
    
    private void drawHealthBar(float x, float y, Pokemon p) {
        // Background
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(x, y, 200, 25);
        // Foreground
        shapeRenderer.setColor(Color.GREEN);
        float pct = (float)p.currentHp / p.species.maxHp;
        if (pct < 0) pct = 0;
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