package com.robindemesmaeker.pokelike;

public enum Item {
    POTION("Potion"),
    POKEBALL("Pokeball"); 

    public final String name;

    Item(String name) {
        this.name = name;
    }
}