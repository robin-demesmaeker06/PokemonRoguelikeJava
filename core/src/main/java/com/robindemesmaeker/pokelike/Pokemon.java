package com.robindemesmaeker.pokelike;

public class Pokemon {
    public Species species;
    public int currentHp;
    public int level;

    public Pokemon(Species species, int level) {
        this.species = species;
        this.level = level;
        // Simple calculation: Base HP + Level (Just for testing)
        this.currentHp = species.maxHp + level;
    }
}