package com.seth.owlcity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Seth on 8/10/15.
 */
public class Player {
    public int gravity;
    public int maxMovement = 100;
    public int movement;
    private Vector2 position;
    private Vector2 velocity;
    private Texture plane;
    public float rotation;
    private float upperLimit;
    private float lowerLimit;
    public float xOffset;
    public boolean blown;
    public Player(int w, int h, float ul, float ll){
        position = new Vector2(w/2, h/2);
        velocity = new Vector2(0, 0);
        plane = new Texture("plane.png");
        rotation = 0f;
        movement = 0;
        position.y = OwlCityTribute.HEIGHT*.1f;
        upperLimit = ul;
        lowerLimit = ll;
        blown = false;
        gravity = -5;
        xOffset = OwlCityTribute.WIDTH*.4f;
    }

    public void update(float dt){
        if(position.y > 0){
            velocity.add(0, gravity);
        }
        velocity.scl(dt);
        position.add(movement * dt, velocity.y);
        if(position.y < lowerLimit){
            position.y = lowerLimit;
            velocity.y = 0;
            movement -= 2;
            if(movement < 0) movement = 0;
        }
        else if(position.y > upperLimit){
            position.y = upperLimit;
            velocity.y /= 2f;
        }
        velocity.scl(1 / dt);
        //Estimate angle
        float temp = normalize(-450, 200, (float)-Math.PI/4f, (float)Math.PI/4f, velocity.y);
        rotation = 25*(float)Math.sin(temp);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Texture getPlane() {
        return plane;
    }

    public void lift(){
        movement = movement > maxMovement ? maxMovement : movement +10;
        velocity.y = 200;
    }

    public float normalize(float origMin, float origMax, float finalMin, float finalMax, float x){
        return finalMin + (x-origMin)*(finalMax - finalMin)/(origMax-origMin);
    }

    public void resetPos(float x, float y){
        position = new Vector2(x, y);
    }

    public void resetVel(float x, float y){
        velocity = new Vector2(x, y);
    }

    public String toString(){
        return Integer.toString(gravity) + ", " +
                Integer.toString(maxMovement) + ", " +
                Integer.toString(movement) + ", " +
                position.toString() + ", " +
                velocity.toString() + ", " +
                plane.toString() + ", " +
                Float.toString(rotation) + ", " +
                Float.toString(upperLimit) + ", " +
                Float.toString(lowerLimit) + ", " +
                Boolean.toString(blown);
    }
}
