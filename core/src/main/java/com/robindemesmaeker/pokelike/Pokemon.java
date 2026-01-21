package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.math.MathUtils;

public class Pokemon {
    public Species species;
    public int level;
    
    // Battle Stats
    public int currentHp;
    public int maxHp;
    public int attack;
    
    // Growth Stats
    public int currentXp;
    public int xpToNextLevel;

    public Pokemon(Species species, int level) {
        this.species = species;
        this.level = level;
        
        // Initial Stat Calculation (Simplified Gen 1 Formula)
        recalculateStats();
        
        // Heal to full on creation
        this.currentHp = this.maxHp;
        
        this.currentXp = 0;
        this.xpToNextLevel = level * 100; // e.g. Level 5 needs 500 XP
    }
    
    public void recalculateStats() {
        // HP = Base * 2 * Level / 100 + Level + 10
        this.maxHp = (species.maxHp * 2 * level / 100) + level + 10;
        
        // Attack = Base * 2 * Level / 100 + 5
        this.attack = (species.attack * 2 * level / 100) + 5;
    }

    public boolean gainXp(int amount) {
        currentXp += amount;
        boolean leveledUp = false;
        
        while (currentXp >= xpToNextLevel) {
            currentXp -= xpToNextLevel;
            level++;
            xpToNextLevel = level * 100; // Next level is harder
            
            // Increase Stats
            int oldMaxHp = maxHp;
            recalculateStats();
            
            // Heal the difference (so you get the new HP capacity immediately)
            currentHp += (maxHp - oldMaxHp);
            
            leveledUp = true;
        }
        return leveledUp;
    }
}