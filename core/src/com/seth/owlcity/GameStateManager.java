package com.seth.owlcity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Created by Seth on 8/10/15.
 */
public class GameStateManager {
    private Stack<State> states;
    public GameStateManager(State s){
        states = new Stack<>();
        states.push(s);
    }

    public void pop(){
        states.pop();
    }

    public void push(State s){
        states.push(s);
    }

    public void setState(State s){
        states.pop();
        states.push(s);
    }

    public void update(float dt){
        states.peek().update(dt);
    }

    public void render(SpriteBatch sb){
        states.peek().render(sb);
    }
}
