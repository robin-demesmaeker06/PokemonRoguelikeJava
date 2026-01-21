package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.Game;

public class MainGame extends Game {
    
    public Pokemon playerPokemon; 
    
    // NEW: We save the screen here so it doesn't get deleted
    public DungeonScreen dungeonScreen; 

    @Override
    public void create() {
        Species charizardSpec = new Species("Charizard", 100, 50, "Fire", "Flying");
        playerPokemon = new Pokemon(charizardSpec, 5);

        // Initialize it ONCE
        dungeonScreen = new DungeonScreen(this);
        
        // Use it
        this.setScreen(dungeonScreen);
    }

    @Override
    public void render() {
        super.render();
    }
}