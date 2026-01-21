package com.robindemesmaeker.pokelike;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class PauseMenuScreen extends ScreenAdapter {
    
    final MainGame game;
    PauseMenuController controller;
    
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    BitmapFont font;
    OrthographicCamera camera;

    public PauseMenuScreen(MainGame game) {
        this.game = game;
        this.controller = new PauseMenuController(game);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
    }

    @Override
    public void render(float delta) {
        controller.update(delta);
        
        // Semi-transparent overlay feel?
        // Actually, we just clear to black for now to be safe.
        ScreenUtils.clear(0, 0, 0, 1);
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.getData().setScale(2.5f);
        font.setColor(Color.WHITE);
        font.draw(batch, "PAUSED", 320, 500);

        String[] options = {"Return to Game", "Save Game", "Load Game", "Quit to Title"};
        font.getData().setScale(2);
        
        for (int i = 0; i < options.length; i++) {
            if (i == controller.selectionIndex) font.setColor(Color.YELLOW);
            else font.setColor(Color.WHITE);
            font.draw(batch, options[i], 250, 350 - (i * 60));
        }
        batch.end();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}