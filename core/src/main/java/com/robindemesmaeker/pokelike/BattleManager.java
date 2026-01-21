package com.robindemesmaeker.pokelike;

public class BattleManager {
    
    // THE ACTORS
    public Pokemon player;
    public Pokemon enemy;

    // THE STATE
    // 0 = Player Turn, 1 = Enemy Turn, 2 = Win, 3 = Loss
    public int battleState = 0; 
    public float turnTimer = 0f;
    public String combatMessage;
    public boolean rewardsGiven = false;

    public BattleManager(Pokemon player, Pokemon enemy) {
        this.player = player;
        this.enemy = enemy;
        this.combatMessage = "Wild " + enemy.species.name + " appeared!";
        this.battleState = 0;
        this.rewardsGiven = false;
    }
    
    public void setMessage(String msg) {
        this.combatMessage = msg;
    }
}