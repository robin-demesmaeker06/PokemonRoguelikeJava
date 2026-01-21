package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InventoryController {
    
    private final MainGame game;
    private final InventoryScreen screen;
    
    // NAVIGATION STATE
    public int cursorIndex = 0; // 0-9
    
    public InventoryController(MainGame game, InventoryScreen screen) {
        this.game = game;
        this.screen = screen;
    }

    public void update(float delta) {
        // 1. EXIT
        if (Gdx.input.isKeyJustPressed(Input.Keys.I) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(game.dungeonScreen);
            return;
        }
        // esc returns to dungeon screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(game.dungeonScreen);
            return;
        }

        // 2. NAVIGATION (Arrow Keys)
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) moveCursor(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) moveCursor(-1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) moveCursor(-5);   // Jump up a row
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) moveCursor(5);  // Jump down a row

        // 3. INTERACTION: HEAL
        // Press [H] or [SPACE] to use a Potion on the selected Pokemon
        if (Gdx.input.isKeyJustPressed(Input.Keys.H) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            attemptHeal();
        }
    }

    private void moveCursor(int change) {
        int newIndex = cursorIndex + change;
        
        // Bounds Check (0 to 9)
        if (newIndex >= 0 && newIndex < 10) {
            cursorIndex = newIndex;
        }
    }

    private void attemptHeal() {
        // Check if slot is empty
        if (cursorIndex >= game.team.size()) {
            System.out.println("No Pokemon in this slot!");
            return;
        }

        Pokemon target = game.team.get(cursorIndex);

        // Check HP
        if (target.currentHp >= target.maxHp) {
            System.out.println("Already full HP!");
            return;
        }

        // Use Potion
        if (game.useItem(Item.POTION)) {
            target.currentHp += 20; // Heal 20
            if (target.currentHp > target.maxHp) target.currentHp = target.maxHp;
            System.out.println("Healed " + target.species.name + " to " + target.currentHp + " HP.");
        } else {
            System.out.println("No Potions left!");
        }
    }
}