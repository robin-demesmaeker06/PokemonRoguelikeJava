package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainGame extends Game {
    
    public List<Pokemon> team; 
    public int currentMemberIndex = 0;
    public Map<Item, Integer> inventory; 
    public com.badlogic.gdx.Screen previousScreen;
    
    // Screens
    public DungeonScreen dungeonScreen; 

    @Override
    public void create() {
        // Start at MAIN MENU, do NOT create data yet
        this.setScreen(new MainMenuScreen(this));
    }
    
    public void startNewGame() {
        inventory = new HashMap<>();
        inventory.put(Item.POTION, 1);
        inventory.put(Item.POKEBALL, 5);

        team = new ArrayList<>();
        Species charizardSpec = new Species("Charizard", 100, 50, "Fire", "Flying");
        Pokemon starter = new Pokemon(charizardSpec, 5);
        starter.currentHp += 20; 
        starter.maxHp += 20;
        team.add(starter);

        // Create the world
        dungeonScreen = new DungeonScreen(this);
        this.setScreen(dungeonScreen);
    }

    // --- SAVE / LOAD STUBS ---
    // We will implement JSON/Serialization later, for now we just log it.
    
    public boolean hasSaveFile() {
        return false; // Toggle to true if you implement saving
    }
    
    public void saveGame() {
        System.out.println("Saving game state... (To be implemented)");
    }
    
    public void loadGame() {
        System.out.println("Loading game state... (To be implemented)");
        // If loaded successfully:
        // this.setScreen(dungeonScreen);
    }

    // ... existing helpers (getActivePokemon, swapToNextAlive, addItem, useItem) ...
    public Pokemon getActivePokemon() {
        if (team == null || team.isEmpty()) return null;
        return team.get(currentMemberIndex);
    }
    
    public boolean swapToNextAlive() {
        for (int i = 0; i < team.size(); i++) {
            if (team.get(i).currentHp > 0) {
                currentMemberIndex = i;
                return true;
            }
        }
        return false; 
    }

    public void addItem(Item item) {
        inventory.put(item, inventory.getOrDefault(item, 0) + 1);
    }
    
    public boolean useItem(Item item) {
        int count = inventory.getOrDefault(item, 0);
        if (count > 0) {
            inventory.put(item, count - 1);
            return true;
        }
        return false;
    }
}