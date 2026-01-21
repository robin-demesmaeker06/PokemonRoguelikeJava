package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PauseMenuController {
    
    private final MainGame game;
    public int selectionIndex = 0; // 0=Return, 1=Save, 2=Load, 3=Quit Main
    private final int MAX_OPTIONS = 4;

    public PauseMenuController(MainGame game) {
        this.game = game;
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectionIndex--;
            if (selectionIndex < 0) selectionIndex = MAX_OPTIONS - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectionIndex++;
            if (selectionIndex >= MAX_OPTIONS) selectionIndex = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            selectOption();
        }
        
        // ESC to Return immediately
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(game.dungeonScreen);
        }
    }

    private void selectOption() {
        switch (selectionIndex) {
            case 0: // RETURN
                // FIX: Go back to where we came from
                if (game.previousScreen != null) {
                    game.setScreen(game.previousScreen);
                } else {
                    game.setScreen(game.dungeonScreen); // Fallback
                }
                break;
            // ... other cases ...
            case 1: // SAVE
                game.saveGame();
                System.out.println("Game Saved.");
                game.setScreen(game.dungeonScreen); // Return after save
                break;
            case 2: // LOAD
                if (game.hasSaveFile()) {
                    game.loadGame();
                }
                break;
            case 3: // QUIT TO MAIN
                game.setScreen(new MainMenuScreen(game));
                break;
        }
    }
}