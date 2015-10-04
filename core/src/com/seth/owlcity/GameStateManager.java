package com.seth.owlcity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Created by Seth on 8/10/15.
 */
public class GameStateManager {
    private Stack<State> states;
    public GameStateManager(){
        states = new Stack<>();
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
        //Music
        if(OwlCityTribute.loops.get(0).isPlaying()){
            OwlCityTribute.loopOneTime += dt;
            if(OwlCityTribute.loops.get(1).isPlaying() && OwlCityTribute.loopOneTime > 5){ //means its time to fade
                OwlCityTribute.loops.get(0).setVolume(OwlCityTribute.loops.get(0).getVolume() - ((1f/.17f))*dt);
            }
            if(OwlCityTribute.loopOneTime > 68.1){
                OwlCityTribute.loopOneTime = 0f;
                OwlCityTribute.loops.get(1).setVolume(1f);
                OwlCityTribute.loops.get(1).play();
            }
        }
        if(OwlCityTribute.loops.get(1).isPlaying()){
            OwlCityTribute.loopTwoTime += dt;
            if(OwlCityTribute.loops.get(0).isPlaying() && OwlCityTribute.loopTwoTime > 5){
                OwlCityTribute.loops.get(1).setVolume(OwlCityTribute.loops.get(1).getVolume() - (1f/.17f)*dt);
            }
            if(OwlCityTribute.loopTwoTime > 68.1){
                OwlCityTribute.loopTwoTime = 0f;
                OwlCityTribute.loops.get(0).setVolume(1f);
                OwlCityTribute.loops.get(0).play();
            }
        }
        if(OwlCityTribute.intro.isPlaying()){
            OwlCityTribute.introTime += dt;
            //Gdx.app.log("", Float.toString(OwlCityTribute.introTime));
            if(OwlCityTribute.introTime > 75f) {
                OwlCityTribute.loops.get(0).play();
            }

        }
        states.peek().update(dt);
    }

    public void render(SpriteBatch sb){
        if(states.size() > 1){
            State top = states.pop();
            render(sb);
            states.push(top);
        }
        states.peek().render(sb);
    }
    public void reload(){
        this.states.peek().reload();
    }
}
