package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.graphics.Texture;

public class GridEntity {
    public float x, y;
    public int gridX, gridY;
    public Texture texture;
    
    // Can hold ONE of these:
    public Pokemon pokemonData; 
    public Item itemData; 

    // Constructor for Enemies
    public GridEntity(Pokemon pokemon, Texture texture, int x, int y) {
        this.pokemonData = pokemon;
        this.texture = texture;
        this.gridX = x;
        this.gridY = y;
        this.x = x * 64f; 
        this.y = y * 64f;
    }
    
    // Constructor for Items
    public GridEntity(Item item, Texture texture, int x, int y) {
        this.itemData = item;
        this.texture = texture;
        this.gridX = x;
        this.gridY = y;
        this.x = x * 64f; 
        this.y = y * 64f;
    }
}