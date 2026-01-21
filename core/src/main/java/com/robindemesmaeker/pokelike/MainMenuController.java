package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class MainMenuController {
    
    private final MainGame game;
    public int selectionIndex = 0; // 0=New, 1=Continue, 2=Options, 3=Quit
    private final int MAX_OPTIONS = 4;

    public MainMenuController(MainGame game) {
        this.game = game;
    }

    public void update(float delta) {
        // Navigation
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectionIndex--;
            if (selectionIndex < 0) selectionIndex = MAX_OPTIONS - 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectionIndex++;
            if (selectionIndex >= MAX_OPTIONS) selectionIndex = 0;
        }

        // Selection
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            selectOption();
        }
    }

    private void selectOption() {
        switch (selectionIndex) {
            case 0: // NEW GAME
                game.startNewGame();
                break;
            case 1: // CONTINUE
                if (game.hasSaveFile()) {
                    game.loadGame();
                } else {
                    System.out.println("No save file found.");
                }
                break;
            case 2: // OPTIONS
                System.out.println("Options not implemented yet.");
                break;
            case 3: // QUIT
                Gdx.app.exit();
                break;
        }
    }
}