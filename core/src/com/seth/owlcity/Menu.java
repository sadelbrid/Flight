package com.seth.owlcity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Random;

/**
 * Created by Seth on 9/1/15.
 */
public class Menu extends State {
    private static final int NUM_SHIMMERS = 30;
    private Array<Shimmer> shimmers;
    private Random random;
    private Texture bg;
    private Texture vignette;
    private Texture cloud;
    private Texture title;
    private Texture plane;
    private ShapeRenderer sr;
    private float whitevalue;
    private float whiteOverlay;
    private boolean fadingIn;
    private boolean fadingOut;
    private Array<FluctuatingObject> clouds;
    public Menu(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);
        bg = new Texture("menubg.png");
        vignette = new Texture("vignette.png");
        cloud = new Texture("cloud.png");
        title = new Texture("title.png");
        plane = new Texture("plane.png");

        random = new Random(System.currentTimeMillis());
        shimmers = new Array<Shimmer>();
        for(int i = 0; i < NUM_SHIMMERS; i++){
            shimmers.add(new Shimmer(random.nextInt(OwlCityTribute.WIDTH),
                    random.nextInt((int)(OwlCityTribute.HEIGHT * .43f - OwlCityTribute.HEIGHT * .3f)) + OwlCityTribute.HEIGHT * .3f,
                    random.nextInt((int)(OwlCityTribute.WIDTH*.0025f) + 1),
                    random.nextInt(Shimmer.MAX_LIFE) + 50));
        }

        sr = new ShapeRenderer();
        clouds = new Array<FluctuatingObject>();
        clouds.add(new FluctuatingObject((int)(OwlCityTribute.WIDTH*.6f), 0f, (int)(OwlCityTribute.HEIGHT*.6f), 0, -75));
        clouds.add(new FluctuatingObject((int)clouds.get(0).getPosition().x + (int)(OwlCityTribute.WIDTH*.8f), 0f, (int)(OwlCityTribute.HEIGHT*.6f), 0, -75));
        whitevalue = 1f;
        fadingIn = true;
        fadingOut = false;
        whiteOverlay = 1;
    }

    @Override
    protected void handleInput() {
        if(!fadingIn && Gdx.input.justTouched()){
            fadingOut = true;
        }

    }

    @Override
    public void update(float dt) {
        handleInput();
        if(fadingIn){
            whiteOverlay -= dt;
            Gdx.app.log("whiteoverlay", Float.toString(whiteOverlay));
            if(whiteOverlay < 0) {
                whiteOverlay = 0f;
                fadingIn = false;
            }
        }
        else if (fadingOut) {
            whitevalue -= .75f*dt;
            if(whitevalue < 0) whitevalue = 0;
            if(whitevalue == 0) {
                dispose();
                gsm.setState(new Beach(gsm));
            }
        }

        //Shimmer update
        for(int i = 0; i < NUM_SHIMMERS; i++){
            shimmers.get(i).update(dt);
        }

        //clouds
        for(int i= 0; i<clouds.size; i++){
            clouds.get(i).update(dt);
            if(clouds.get(i).getPosition().x < -cloud.getWidth()) {
                int index = (i == 0) ? clouds.size-1 : i-1;
                clouds.get(i).getPosition().x = clouds.get(index).getPosition().x + OwlCityTribute.WIDTH *.8f;
                clouds.get(i).yOffset = random.nextInt((int)(OwlCityTribute.HEIGHT*.1f)) + (int)(OwlCityTribute.HEIGHT*.5f);
            }
        }
    }



    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.setColor(whitevalue, whitevalue, whitevalue, 1f);
        sb.begin();
        sb.draw(bg, 0, 0, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);
        sb.draw(cloud, clouds.get(0).getPosition().x, clouds.get(0).getPosition().y);
        sb.draw(cloud, clouds.get(1).getPosition().x, clouds.get(1).getPosition().y);
        sb.draw(title, OwlCityTribute.WIDTH/2 - title.getWidth()/2, OwlCityTribute.HEIGHT*.6f - title.getHeight()/2);
        sb.draw(plane, OwlCityTribute.WIDTH*.5f - plane.getWidth()/2, OwlCityTribute.HEIGHT*.09f, 0, 0, plane.getWidth(), plane.getHeight(),
                1, 1, 5, 0, 0, plane.getWidth(), plane.getHeight(), false, false);
        sb.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setProjectionMatrix(cam.combined);
        //Draw sparkles
        for(int i = 0; i< NUM_SHIMMERS; i++){
            sr.setColor(whitevalue, whitevalue, whitevalue, Math.abs((float) Math.sin(Math.PI * shimmers.get(i).life / Shimmer.MAX_LIFE))*whitevalue);
            sr.circle(shimmers.get(i).x, shimmers.get(i).y,
                    (float) Math.abs(Math.sin(Math.PI * shimmers.get(i).life / Shimmer.MAX_LIFE)) * shimmers.get(i).size);
        }

        //draw ring
        //sr.setColor((float)Math.sin(ringAlpha), (float)Math.sin(ringAlpha), (float)Math.sin(ringAlpha), (float)Math.sin(ringAlpha));
        sr.end();
        //Vignette;
        sb.begin();
        sb.setColor(whitevalue, whitevalue, whitevalue, .5f);
        sb.draw(vignette, 0, 0, OwlCityTribute.WIDTH , OwlCityTribute.HEIGHT);
        sb.end();
        if(fadingIn) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setProjectionMatrix(cam.combined);
            sr.setColor(1f, 1f, 1f, whiteOverlay);
            sr.rect(0, 0, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);
            sr.end();
        }
    }

    @Override
    public void dispose() {
        bg.dispose();
        vignette.dispose();
        sr.dispose();
        cloud.dispose();
        title.dispose();
        plane.dispose();
    }

    private class Shimmer{
        public static final int MAX_LIFE = 100;
        public float x, y;
        public float size;
        private float life;

        public Shimmer(float x, float y, float s, float l){
            Shimmer.this.x = x;
            Shimmer.this.y = y;
            Shimmer.this.size = s;
            Shimmer.this.life = l;
        }

        public void update(float dt){
            Shimmer.this.life += 75*dt;
        }
    }
    @Override
    public void reload() {
    }
}
