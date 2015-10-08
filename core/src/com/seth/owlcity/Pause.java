package com.seth.owlcity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Seth on 10/6/15.
 */
public class Pause extends State{
    private float opacity;
    private float overlayOpacity;
    private float whiteOpacity;
    private float screenHeight, screenWidth;
    private ShapeRenderer sr;
    private boolean fadeIn, fadeOut, fadeToWhite;
    private Texture dreams;
    private Texture playButton;
    private Texture doorButton;
    public Pause(GameStateManager gsm){
        super(gsm);
        cam.setToOrtho(false, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);
        opacity = 0f;
        overlayOpacity = 0f;
        whiteOpacity = 0f;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight= Gdx.graphics.getHeight();
        sr = new ShapeRenderer();
        fadeIn = true;
        fadeOut = false;
        fadeToWhite = false;
        dreams = new Texture("dreams.png");
        playButton = new Texture("play.png");
        doorButton = new Texture("door.png");
    }
    @Override
    protected void handleInput() {
        if(!fadeIn && Gdx.input.justTouched()){
            //if play touched
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            if(x > screenWidth*.4f - playButton.getWidth()*Gdx.graphics.getDensity() / 2
                    && x < screenWidth*.4f + playButton.getWidth()*Gdx.graphics.getDensity() / 2
                    && y > screenHeight*.7f - playButton.getHeight()*Gdx.graphics.getDensity() / 2
                    && y < screenHeight*.7f + playButton.getHeight()*Gdx.graphics.getDensity() / 2)
                        fadeOut = true;
                        //if exit touched
            else if(x > screenWidth*.6f - doorButton.getWidth()*Gdx.graphics.getDensity() / 2
                    && x < screenWidth*.6f + doorButton.getWidth()*Gdx.graphics.getDensity() / 2
                    && y > screenHeight*.7f - doorButton.getHeight()*Gdx.graphics.getDensity() / 2
                    && y < screenHeight*.7f + doorButton.getHeight()*Gdx.graphics.getDensity() / 2){
                fadeOut = true;
                fadeToWhite = true;
            }


        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        if(fadeIn){
            opacity += dt;
            overlayOpacity += dt;
            if(opacity > .5f) {
                opacity = .5f;
                overlayOpacity = .5f;
                fadeIn = false;
            }
        }

        if(fadeOut){
            //overlayOpacity -= dt;
            opacity-=dt;
            if(fadeToWhite){
                whiteOpacity += dt;
                if(whiteOpacity > 1){
                    //switch
                    dispose();
                    gsm.pop();
                    gsm.setState(new Menu(gsm));
                }
            }
            else{
                overlayOpacity -= dt;
                if(opacity < 0){
                    //pop
                    dispose();
                    gsm.pop();
                    gsm.peek().paused = false;
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        sr.setAutoShapeType(true);
        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0, 0, 0, opacity);
        sr.rect(cam.position.x - cam.viewportWidth / 2, 0, cam.viewportWidth, cam.viewportHeight);
        if(fadeToWhite){
            sr.setColor(1, 1, 1, whiteOpacity);
            sr.rect(cam.position.x - cam.viewportWidth / 2, 0, cam.viewportWidth, cam.viewportHeight);
        }
        sr.end();

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.setColor(1, 1, 1, overlayOpacity);
        sb.draw(dreams, cam.position.x - dreams.getWidth() / 2, cam.viewportHeight / 2 - dreams.getHeight() / 2);
        sb.draw(playButton, cam.position.x - cam.viewportWidth*.1f - playButton.getWidth() / 2, cam.viewportHeight *.3f - playButton.getHeight() / 2);
        sb.draw(doorButton, cam.position.x + cam.viewportWidth*.1f - doorButton.getWidth() / 2, cam.viewportHeight *.3f - doorButton.getHeight() / 2);
        sb.end();
    }

    @Override
    public void dispose() {
        sr.dispose();
        dreams.dispose();
        playButton.dispose();
        doorButton.dispose();
    }
}
