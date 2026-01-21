package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.graphics.Texture;

public class GridEntity {
    public float x, y; // Visual position
    public int gridX, gridY; // Logic position
    public Texture texture;
    public Pokemon pokemonData; // The stats (HP, Level, Species)

    public GridEntity(Pokemon pokemon, Texture texture, int startGridX, int startGridY) {
        this.pokemonData = pokemon;
        this.texture = texture;
        this.gridX = startGridX;
        this.gridY = startGridY;
        this.x = startGridX * 64f; // Assuming 64 tile size
        this.y = startGridY * 64f;
    }
}