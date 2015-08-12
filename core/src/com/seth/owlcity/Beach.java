package com.seth.owlcity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Seth on 8/10/15.
 */
public class Beach extends State {
    private Texture plane;
    public Beach(GameStateManager gsm){
        super(gsm);
        plane = new Texture("plane.png");
        cam.setToOrtho(false, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(plane, OwlCityTribute.WIDTH/2, OwlCityTribute.HEIGHT/2);
        sb.end();
    }
}
