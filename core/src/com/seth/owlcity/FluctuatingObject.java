package com.seth.owlcity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Seth on 8/12/15.
 */
public class FluctuatingObject {
    private Vector2 position;
    public int yOffset;
    public int range;
    public float flow;
    private float speedOfFluctuation;
    public int MOVEMENT;
    public FluctuatingObject(int x, float speedOfFluctuation, int yOff, int r, int v){
        this.flow = 0;
        this.speedOfFluctuation = speedOfFluctuation;
        this.yOffset = yOff;
        this.range = r;
        this.position = new Vector2(x, 0);
        MOVEMENT = v;
    }

    public void update(float dt){
        position.add(MOVEMENT * dt, 0);
        flow+=speedOfFluctuation*dt;
        if(flow > 2*Math.PI) flow = 0;
        position.y = (int)(range * Math.sin(flow)) + yOffset;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setyOffset(int y){
        yOffset = y;
    }
}
