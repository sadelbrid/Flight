package com.seth.owlcity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Seth on 8/18/15.
 */
public class TextBox {
    public int x, y;
    public int padding;
    public boolean boxOpened;
    public float width, height;
    private  float timePerLetter;
    private float currentTime;
    private float totalTime;
    public String firstLine, secondLine;
    public String firstLineBuffer, secondLineBuffer;
    private int bufferIndex;
    private int length;
    public int halfIndex;
    public boolean readyToUpdate;
    public boolean finished;
    private int maxWidth, maxHeight;
    public float alpha;
    public TextBox(){
        readyToUpdate = false;
        finished = false;
        boxOpened = false;
    }

    public void prepare(String text1, String text2, float timePerLetter){
        this.length = text1.length() + text2.length();
        this.firstLine = text1;
        this.secondLine = text2;
        this.boxOpened = false;
        this.timePerLetter = timePerLetter;
        this.currentTime = 0f;
        bufferIndex = 0;
        firstLineBuffer = "";
        secondLineBuffer = "";
        //Determine size
        width = 0;
        height = 0;
        readyToUpdate = true;
        totalTime = 0f;
        finished = false;
        alpha = 1f;
    }

    public void setBounds(int w, int h, int x, int y){
        maxWidth = w;
        maxHeight = h;
        this.x = x;
        this.y = y;
        padding = (int)(maxWidth*.075);
    }

    public boolean update(float dt){
        totalTime += dt;
        if(!boxOpened){
            width += 200*dt;
            height += 200*dt;
            if (width > maxWidth) width = maxWidth;
            if (height > maxHeight) height = maxHeight;
            if(width == maxWidth && height == maxHeight) boxOpened = true;
        }
        else if (bufferIndex < this.length){
            currentTime += dt;
            if(currentTime >= timePerLetter){
                currentTime = 0;
                if(bufferIndex < firstLine.length()){

                    firstLineBuffer += Character.toString(firstLine.charAt(bufferIndex));
                }
                else {
                    secondLineBuffer += Character.toString(secondLine.charAt(bufferIndex - firstLine.length()));
                }
                bufferIndex++;
            }
        }
        if(totalTime>(firstLine.length() + secondLine.length())*timePerLetter + 3){
            alpha -= dt;
        }
        if(alpha <=0){
            finished = true;
            readyToUpdate = false;
            boxOpened = false;
            firstLineBuffer = "";
            secondLineBuffer = "";
            return true;
        }
        else return false;
    }
}
