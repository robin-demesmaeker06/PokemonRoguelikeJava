package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.Game;
import java.util.HashMap;
import java.util.Map;

public class MainGame extends Game {
    
    public Pokemon playerPokemon; 
    public DungeonScreen dungeonScreen; 
    
    // NEW: The Inventory
    // Key = Item Type, Value = Quantity (e.g., POTION -> 3)
    public Map<Item, Integer> inventory; 

    @Override
    public void create() {
        // 1. Initialize Inventory
        inventory = new HashMap<>();
        inventory.put(Item.POTION, 1); // Start with 1 free potion
        inventory.put(Item.POKEBALL, 5); // Start with 5 balls

        // 2. Create Player
        Species charizardSpec = new Species("Charizard", 100, 50, "Fire", "Flying");
        playerPokemon = new Pokemon(charizardSpec, 5);
        playerPokemon.currentHp += 20;
        playerPokemon.maxHp += 20;

        // 3. Start Game
        dungeonScreen = new DungeonScreen(this);
        this.setScreen(dungeonScreen);
    }
    
    // Helper to add items
    public void addItem(Item item) {
        int count = inventory.getOrDefault(item, 0);
        inventory.put(item, count + 1);
        System.out.println("Added " + item.name + ". Total: " + (count + 1));
    }
    
    // Helper to use items
    public boolean useItem(Item item) {
        int count = inventory.getOrDefault(item, 0);
        if (count > 0) {
            inventory.put(item, count - 1);
            return true;
        }
        return false;
    }

    @Override
    public void render() {
        super.render();
    }
}