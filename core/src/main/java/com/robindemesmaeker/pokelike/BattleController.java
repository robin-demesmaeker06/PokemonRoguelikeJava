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
        if (model.battleState == 0) handlePlayerInput();
        else if (model.battleState == 1) handleEnemyAi(delta);
        else if (model.battleState == 2) handleVictory();
        else if (model.battleState == 3) handleLoss(); // New Handler

        if (model.battleState == 0 && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
             game.setScreen(game.dungeonScreen);
        }
    }

    private void handlePlayerInput() {
        // Inside update(float delta)

        // CHANGE: ESC during battle -> Pause Menu (or Run Away?)
        // Usually ESC is "Run Away" in battles. 
        // Let's make "P" the Pause key for battles, or keep ESC as Run.
        
        // Decision: Let's keep ESC as "Run Away" in battle for gameplay flow.
        // But if you want a menu, add:
        // Inside update()
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) { // Or ESC if you changed it
             game.previousScreen = game.getScreen(); // Remember this specific Battle instance
             game.setScreen(new PauseMenuScreen(game));
        }
        // [SPACE] ATTACK
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
        
        // [H] HEAL
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            if (game.useItem(Item.POTION)) {
                model.player.currentHp += 25;
                if (model.player.currentHp > model.player.maxHp) model.player.currentHp = model.player.maxHp;
                model.setMessage("Used Potion! HP is " + model.player.currentHp);
                model.battleState = 1; 
                model.turnTimer = 0f;
            } else {
                model.setMessage("No Potions left!");
            }
        }
        
        // [C] CATCH (Now adds to team!)
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            if (game.useItem(Item.POKEBALL)) {
                float hpPercent = (float)model.enemy.currentHp / model.enemy.maxHp;
                
                if (hpPercent < 0.3f) {
                    model.setMessage("Gotcha! Added to team!");
                    model.battleState = 2; // Win
                    
                    // --- NEW: ADD TO TEAM ---
                    // Heal slightly so it's usable
                    model.enemy.currentHp = 10; 
                    game.team.add(model.enemy);
                    System.out.println("Team size: " + game.team.size());
                    
                } else {
                    model.setMessage("It broke free!");
                    model.battleState = 1; 
                    model.turnTimer = 0f;
                }
            } else {
                model.setMessage("No Pokeballs left!");
            }
        }
        
        // [S] SWITCH (New!)
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            // Simple cycle: Move to next index wrapping around
            game.currentMemberIndex = (game.currentMemberIndex + 1) % game.team.size();
            model.player = game.getActivePokemon(); // Update the model reference
            model.setMessage("Go! " + model.player.species.name + "!");
            // Switching costs a turn!
            model.battleState = 1;
            model.turnTimer = 0f;
        }
    }

    private void handleEnemyAi(float delta) {
        model.turnTimer += delta;
        if (model.turnTimer > 1.0f) {
            int damage = 3 + (model.enemy.level); 
            model.player.currentHp -= damage;
            model.setMessage("Enemy hit you for " + damage + " dmg!");
            
            // CHECK FAINT
            if (model.player.currentHp <= 0) {
                model.player.currentHp = 0;
                
                // --- NEW: CHECK TEAM ALIVE ---
                if (game.swapToNextAlive()) {
                    // Someone else is alive! Swap them in.
                    model.player = game.getActivePokemon();
                    model.setMessage("Fainted! Go " + model.player.species.name + "!");
                    model.battleState = 0; // Player gets to act immediately
                } else {
                    // Everyone dead. Game Over.
                    model.battleState = 3; 
                    model.setMessage("Team wiped out... Press [SPACE].");
                }
            } else {
                model.battleState = 0; // Player Turn
            }
        }
    }

    private void handleVictory() {
        if (!model.rewardsGiven) {
            int xpGain = model.enemy.level * 20; 
            boolean leveledUp = model.player.gainXp(xpGain);
            
            // Only give message if NOT caught (Caught logic handles its own msg)
            if (!model.combatMessage.contains("Gotcha")) {
                if (leveledUp) model.setMessage("Won! +" + xpGain + "XP. Lvl Up!");
                else model.setMessage("Won! +" + xpGain + "XP.");
            }
            model.rewardsGiven = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.dungeonScreen.removeEnemy(model.enemy); 
            game.setScreen(game.dungeonScreen); 
        }
    }
    
    private void handleLoss() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Gdx.app.exit(); 
        }
    }
}