package com.seth.owlcity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Seth on 10/6/15.
 */
public class UponLoss extends State {
    private float opacity;
    private float messageOpacity;
    private ShapeRenderer sr;
    private boolean changing;
    private boolean message;
    private Texture uponLoss;
    public UponLoss(GameStateManager gsm){
        super(gsm);
        cam.setToOrtho(false, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);
        opacity = 0f;
        sr = new ShapeRenderer();
        changing = false;
        message = false;
        messageOpacity = 0;
        uponLoss = new Texture("uponLoss.png");
    }
    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()){
            changing = true;
            message = false;
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        Gdx.app.log("opacity", Float.toString(opacity));
        opacity += dt;
        if(!changing && opacity > .5f) {
            opacity = .5f;
            message = true;
        }
        else if (opacity > 1f){
            dispose();
            this.gsm.pop();
            gsm.peek().dispose();
            switch(gsm.currentState){
                case GameStateManager.BEACH:
                    gsm.setState(new Beach(gsm));
                    break;
                case GameStateManager.OCEAN:
                    gsm.setState(new Ocean(gsm));
                    break;
                case GameStateManager.SKY:
                    gsm.setState(new Sky(gsm));
                    break;
                case GameStateManager.SPACE:
                    gsm.setState(new Space(gsm));
                    break;
                case GameStateManager.CITY:
                    gsm.setState(new City(gsm));
                    break;
                case GameStateManager.GRASSLAND:
                    gsm.setState(new Grassland(gsm));
                    break;
            }
        }

        if(message){
            messageOpacity += dt;
            if(messageOpacity > 1) messageOpacity = 1;
        }
        else{
            messageOpacity -= 2*dt;
            if(messageOpacity < 0) messageOpacity = 0;
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
        sr.end();

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.setColor(1, 1, 1, messageOpacity);
        sb.draw(uponLoss, cam.viewportWidth/2 - uponLoss.getWidth()/2, cam.viewportHeight/2 - uponLoss.getHeight()/2);
        sb.end();
    }

    @Override
    public void dispose() {
        sr.dispose();
        uponLoss.dispose();
    }
}
