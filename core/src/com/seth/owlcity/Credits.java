package com.seth.owlcity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Seth on 9/28/15.
 */
public class Credits extends State{
    private Texture one;
    private Texture two;
    private Texture three;
    private int count;
    private float size;
    private float opacity;
    private ShapeRenderer sr;
    public Credits(GameStateManager gsm){
        super(gsm);
        size = 1f;
        opacity = 0f;
        one = new Texture("createdBy.png");
        two = new Texture("inspiredBy.png");
        three = new Texture("featuring.png");
        sr = new ShapeRenderer();
        sr.setColor(Color.WHITE);
        count = 1;
    }
    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        size -= .025*dt;
        opacity += .6*dt;
        if(Math.sin(opacity) < 0) {
            count++;
            if(count>3){
                dispose();
                this.gsm.setState(new Menu(this.gsm));
            }
            size = 1f;
            opacity = 0f;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.rect(0, 0, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);
        sr.end();
        sb.begin();
        sb.setColor(1f, 1f, 1f, (float)Math.sin(opacity));
        switch(count){
            case 1:
                sb.draw(one, OwlCityTribute.WIDTH/2 - (one.getWidth()*size)/2, OwlCityTribute.HEIGHT/2 - (one.getHeight()*size)/2,
                        one.getWidth()*size, one.getHeight()*size);
                break;
            case 2:
                sb.draw(two, OwlCityTribute.WIDTH/2 - (two.getWidth()*size)/2, OwlCityTribute.HEIGHT/2 - (two.getHeight()*size)/2,
                        two.getWidth()*size, two.getHeight()*size);
                break;
            case 3:
                sb.draw(three, OwlCityTribute.WIDTH/2 - (three.getWidth()*size)/2, OwlCityTribute.HEIGHT/2 - (three.getHeight()*size)/2,
                        three.getWidth()*size, three.getHeight()*size);
                break;
        }
        sb.end();
    }


    @Override
    public void dispose() {
        one.dispose();
        two.dispose();
        three.dispose();
        sr.dispose();
    }

    @Override
    public void reload() {

    }
}
