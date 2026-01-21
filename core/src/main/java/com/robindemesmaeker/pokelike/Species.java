package com.robindemesmaeker.pokelike;

public class Species {
    public String name;
    public int maxHp;
    public int attack;
    public String[] types;

    // Constructor (This is how you build a new Species)
    public Species(String name, int maxHp, int attack, String... types) {
        this.name = name;
        this.maxHp = maxHp;
        this.attack = attack;
        this.types = types;
    }
}