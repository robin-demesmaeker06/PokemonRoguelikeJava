package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class BattleController {
    
    private final MainGame game;
    private final BattleManager model;
    
    public BattleController(MainGame game, BattleManager model) {
        this.game = game;
        this.model = model;
    }

    public void update(float delta) {
        
        // --- STATE 0: PLAYER TURN ---
        if (model.battleState == 0) {
            handlePlayerInput();
        } 
        
        // --- STATE 1: ENEMY TURN ---
        else if (model.battleState == 1) {
            handleEnemyAi(delta);
        }
        
        // --- STATE 2: VICTORY ---
        else if (model.battleState == 2) {
            handleVictory();
        }
        
        // --- STATE 3: LOSS ---
        else if (model.battleState == 3) {
             if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                Gdx.app.exit(); // Or restart logic
            }
        }

        // Global: Run Away
        if (model.battleState == 0 && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
             game.setScreen(game.dungeonScreen);
        }
    }

    private void handlePlayerInput() {
        // 1. ATTACK
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            int damage = 5 + (model.player.level); 
            model.enemy.currentHp -= damage;
            model.setMessage("You hit for " + damage + " dmg!");
            
            if (model.enemy.currentHp <= 0) {
                model.enemy.currentHp = 0;
                model.battleState = 2; // Win
                model.setMessage("Enemy fainted! Press [SPACE].");
            } else {
                model.battleState = 1; // Enemy Turn
                model.turnTimer = 0f;
            }
        }
        
        // 2. HEAL (Potion)
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            if (game.useItem(Item.POTION)) {
                model.player.currentHp += 25;
                if (model.player.currentHp > model.player.maxHp) model.player.currentHp = model.player.maxHp;
                
                model.setMessage("Used Potion! HP is " + model.player.currentHp);
                model.battleState = 1; // End Turn
                model.turnTimer = 0f;
            } else {
                model.setMessage("No Potions left!");
            }
        }
        
        // 3. CATCH (Pokeball)
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            if (game.useItem(Item.POKEBALL)) {
                float hpPercent = (float)model.enemy.currentHp / model.enemy.maxHp;
                
                if (hpPercent < 0.3f) {
                    model.setMessage("Gotcha! " + model.enemy.species.name + " was caught!");
                    model.battleState = 2; // Win
                } else {
                    model.setMessage("It broke free! (Weaken it more!)");
                    model.battleState = 1; // Enemy Turn
                    model.turnTimer = 0f;
                }
            } else {
                model.setMessage("No Pokeballs left!");
            }
        }
    }

    private void handleEnemyAi(float delta) {
        model.turnTimer += delta;
        if (model.turnTimer > 1.0f) {
            int damage = 3 + (model.enemy.level); 
            model.player.currentHp -= damage;
            model.setMessage("Enemy hit you for " + damage + " dmg!");
            
            if (model.player.currentHp <= 0) {
                model.player.currentHp = 0;
                model.battleState = 3; // Loss
                model.setMessage("You fainted... Press [SPACE].");
            } else {
                model.battleState = 0; // Player Turn
            }
        }
    }

    private void handleVictory() {
        // Calculate XP ONCE
        if (!model.rewardsGiven) {
            int xpGain = model.enemy.level * 20; 
            boolean leveledUp = model.player.gainXp(xpGain);
            
            if (leveledUp) {
                model.setMessage("Won! +" + xpGain + "XP. Leveled up to " + model.player.level + "!");
            } else {
                model.setMessage("Won! +" + xpGain + "XP.");
            }
            model.rewardsGiven = true;
        }

        // Wait for exit input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.dungeonScreen.removeEnemy(model.enemy); 
            game.setScreen(game.dungeonScreen); 
        }
    }
}