package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen extends ScreenAdapter {
    
    final MainGame game;
    MainMenuController controller;
    
    SpriteBatch batch;
    BitmapFont font;
    OrthographicCamera camera;

    public MainMenuScreen(MainGame game) {
        this.game = game;
        this.controller = new MainMenuController(game);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
    }

    @Override
    public void render(float delta) {
        controller.update(delta);

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        
        // Title
        font.getData().setScale(3);
        font.setColor(Color.YELLOW);
        font.draw(batch, "POKE-ROGUE", 250, 500);

        // Options
        String[] options = {"New Game", "Continue", "Options", "Quit"};
        font.getData().setScale(2);
        
        for (int i = 0; i < options.length; i++) {
            if (i == controller.selectionIndex) font.setColor(Color.YELLOW);
            else {
                if (i == 1 && !game.hasSaveFile()) font.setColor(Color.GRAY); // Gray out Continue if no save
                else font.setColor(Color.WHITE);
            }
            
            font.draw(batch, options[i], 300, 350 - (i * 60));
        }
        
        // Instructions
        font.setColor(Color.GRAY);
        font.getData().setScale(1);
        font.draw(batch, "[UP/DOWN] Select   [ENTER] Confirm", 20, 30);
        
        batch.end();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}