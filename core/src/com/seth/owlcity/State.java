package com.seth.owlcity;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by Seth on 8/10/15.
 */
public abstract class State {
    protected OrthographicCamera cam;
    protected Vector2 mouse;
    protected GameStateManager gsm;
    protected ArrayList<ArrayList<String>> sceneText;
    public boolean paused;
    protected boolean loss;
    public State(GameStateManager gsm){
        this.gsm = gsm;
        cam = new OrthographicCamera();
        mouse = new Vector2();
        sceneText = new ArrayList<ArrayList<String>>();
        paused = false;
        loss = false;
    }

    protected abstract void handleInput();
    public abstract void render(SpriteBatch sb);
    public abstract void update(float dt);
    public abstract void dispose();
}
