package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

public class PlayerController {
    
    private final MainGame game;
    private final WorldManager worldManager;
    private final DungeonScreen screen; 

    // MOVEMENT STATE
    public float currentX, currentY; 
    public float targetX, targetY;   
    private float startX, startY;
    
    private boolean isMoving = false;
    private float moveTimer = 0f;
    private final float MOVE_TIME = 0.2f;
    private float stepsTaken = 0;
    private final float TILE_SIZE = 64f;

    // Assets
    private Texture enemyTexture;
    private Texture itemTexture;

    public PlayerController(MainGame game, DungeonScreen screen, WorldManager worldManager) {
        this.game = game;
        this.screen = screen;
        this.worldManager = worldManager;
        
        // Load your new placeholder for enemies!
        this.enemyTexture = new Texture("placeholder-64x64.png");
        this.itemTexture = new Texture("placeholder-64x64.png");
    }

    public void update(float delta) {
        // 1. Handle Animation (Interpolation)
        if (isMoving) {
            moveTimer += delta;
            float alpha = moveTimer / MOVE_TIME;
            if (alpha >= 1f) {
                currentX = targetX;
                currentY = targetY;
                isMoving = false;
            } else {
                currentX = Interpolation.linear.apply(startX, targetX, alpha);
                currentY = Interpolation.linear.apply(startY, targetY, alpha);
            }
            return; // Don't accept input while moving

            
        }

        // Inside update()
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.previousScreen = game.dungeonScreen; // Remember we were in Dungeon
            game.setScreen(new PauseMenuScreen(game));
            return;
        }
        
        // KEEP: 'I' for Inventory
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            game.setScreen(new InventoryScreen(game));
            return;
        }

        // 2. Handle Input
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) attemptMove(1, 0);
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) attemptMove(-1, 0);
        else if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) attemptMove(0, 1);
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) attemptMove(0, -1);
        
    }

    private void attemptMove(int dx, int dy) {
        int currentGridX = Math.round(currentX / TILE_SIZE);
        int currentGridY = Math.round(currentY / TILE_SIZE);
        int nextGridX = currentGridX + dx;
        int nextGridY = currentGridY + dy;

        // A. SCREEN TRANSITION LOGIC
        if (checkScreenTransition(nextGridX, nextGridY)) return;

        // B. WALL CHECK
        if (worldManager.isWall(nextGridX, nextGridY)) return;

        // C. ENEMY CHECK
        for (GridEntity enemy : worldManager.currentEnemies) {
            if (enemy.gridX == nextGridX && enemy.gridY == nextGridY) {
                triggerBattle(enemy);
                return;
            }
        }

        // D. ITEM CHECK
        GridEntity foundItem = null;
        for (GridEntity item : worldManager.currentItems) {
            if (item.gridX == nextGridX && item.gridY == nextGridY) {
                foundItem = item;
                break;
            }
        }
        
        if (foundItem != null) {
            // 1. Remove from world
            worldManager.removeItem(foundItem);
            
            // 2. Add to Inventory (NEW)
            game.addItem(foundItem.itemData);
            
            // Note: We REMOVED the auto-heal logic here.
        }

        // E. VALID MOVE ...
        finalizeMove(nextGridX, nextGridY);
    }

    private boolean checkScreenTransition(int nextGridX, int nextGridY) {
        int nextWorldX = worldManager.currentWorldX;
        int nextWorldY = worldManager.currentWorldY;
        boolean switched = false;
        
        float newPlayerX = currentX;
        float newPlayerY = currentY;

        if (nextGridX < 0) {
            nextWorldX--; newPlayerX = (worldManager.getWidth() - 1) * TILE_SIZE; switched = true;
        } else if (nextGridX >= worldManager.getWidth()) {
            nextWorldX++; newPlayerX = 0; switched = true;
        } else if (nextGridY < 0) {
            nextWorldY--; newPlayerY = (worldManager.getHeight() - 1) * TILE_SIZE; switched = true;
        } else if (nextGridY >= worldManager.getHeight()) {
            nextWorldY++; newPlayerY = 0; switched = true;
        }

        if (switched) {
            worldManager.loadWorld(nextWorldX, nextWorldY);
            
            // If this is a fresh world (empty), spawn stuff
            if (worldManager.currentEnemies.isEmpty()) {
               spawnEnemiesForNewWorld();
               spawnItemsForNewWorld();
            }

            snapToPosition(newPlayerX, newPlayerY);
            return true;
        }
        return false;
    }

    private void finalizeMove(int nextGridX, int nextGridY) {
        startX = currentX;
        startY = currentY;
        targetX = nextGridX * TILE_SIZE;
        targetY = nextGridY * TILE_SIZE;
        isMoving = true;
        moveTimer = 0f;

        // Regen Logic (Heal while walking)
        stepsTaken++;
        if (stepsTaken >= 5) {
            Pokemon active = game.getActivePokemon();
            if (active.currentHp < active.maxHp) {
                active.currentHp += 1;
            }
            stepsTaken = 0;
        }
    }

    private void triggerBattle(GridEntity enemy) {
        System.out.println("BATTLE START!");
        isMoving = true;
        moveTimer = 0f;
        targetX = currentX;
        targetY = currentY;
        game.setScreen(new BattleScreen(game, enemy.pokemonData)); 
    }

    public void snapToPosition(float x, float y) {
        currentX = x;
        currentY = y;
        targetX = x;
        targetY = y;
    }
    
    private void spawnEnemiesForNewWorld() {
         Species ratSpec = new Species("Rattata", 30, 10, "Normal");
         
         for(int i = 0; i < 8; i++) {
            int ex, ey;
            while(true) {
                ex = MathUtils.random(0, worldManager.getWidth() - 1);
                ey = MathUtils.random(0, worldManager.getHeight() - 1);
                if (!worldManager.isWall(ex, ey)) break;
            }
            Pokemon wildMon = new Pokemon(ratSpec, 3);
            worldManager.currentEnemies.add(new GridEntity(wildMon, enemyTexture, ex, ey));
         }
    }

    private void spawnItemsForNewWorld() {
        for(int i = 0; i < 5; i++) { // Spawn 5 items
            int ix, iy;
            while(true) {
                ix = MathUtils.random(0, worldManager.getWidth() - 1);
                iy = MathUtils.random(0, worldManager.getHeight() - 1);
                if (!worldManager.isWall(ix, iy)) break;
            }
            Item type = MathUtils.randomBoolean() ? Item.POTION : Item.POKEBALL;
            worldManager.currentItems.add(new GridEntity(type, itemTexture, ix, iy));
        }
    }
    
    public void spawnPlayerSafe() {
        int attempts = 0;
        while (attempts < 1000) {
            int tx = MathUtils.random(0, worldManager.getWidth() - 1);
            int ty = MathUtils.random(0, worldManager.getHeight() - 1);
            if (!worldManager.isWall(tx, ty)) {
                snapToPosition(tx * TILE_SIZE, ty * TILE_SIZE);
                return;
            }
            attempts++;
        }
    }
}