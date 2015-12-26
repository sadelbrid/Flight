package com.seth.owlcity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Seth on 9/1/15.
 */
public class Menu extends State {
    private int state;
    private static final int NUM_SHIMMERS = 30;
    private Array<Shimmer> shimmers;
    private Random random;
    private Texture bg;
    private Texture vignette;
    private Texture cloud;
    private Texture title;
    private Texture plane;
    private Texture shadow;
    private Texture menuitems;
    private Texture scenePreviews;
    private ShapeRenderer sr;
    private float whitevalue;
    private float whiteOverlay;
    private boolean fadingIn;
    private boolean fadingOut;
    private Array<FluctuatingObject> clouds;
    private FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("corbel.ttf"));
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private BitmapFont font;
    private Array<TextureRegion> scenes;
    private int selection;
    private float menuItemsOpac, aboutOpac, scenesOpac;
    private final float fadeRate = 2f;
    public Menu(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);
        bg = new Texture("menubg.png");
        vignette = new Texture("vignette.png");
        cloud = new Texture("cloud.png");
        title = new Texture("title.png");
        plane = new Texture("plane.png");
        shadow = new Texture("shadow.png");
        menuitems = new Texture("menuitems.png");
        scenePreviews = new Texture("scenepreviews.png");
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
        parameter.size = 20;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'.";

        font = generator.generateFont(parameter);
        generator.dispose();
        state = 2;
        scenes = new Array<>();
        TextureRegion s = new TextureRegion(scenePreviews);
        for(int i = 0; i<6; i++){
            scenes.add(new TextureRegion(s, i*100, 0, 100, s.getRegionHeight()));
        }
        selection = -1;
        menuItemsOpac = 0;
        aboutOpac = 1;
        scenesOpac = 0;
    }

    @Override
    protected void handleInput() {
        if(!fadingIn && Gdx.input.justTouched()){
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();
            float ratioX = (float)Gdx.graphics.getWidth()/(float)OwlCityTribute.WIDTH;
            float ratioY = (float)Gdx.graphics.getHeight()/(float)OwlCityTribute.HEIGHT;
            x /= ratioX;
            y /= ratioY;
            y = OwlCityTribute.HEIGHT - y;
            if(state == 0 && x > OwlCityTribute.WIDTH*.4f && x < OwlCityTribute.WIDTH*.6f
                    && y < OwlCityTribute.HEIGHT * .45f && y > OwlCityTribute.HEIGHT*.18f){

                if(y < OwlCityTribute.HEIGHT*.28)
                {
                    //about
                    state = 2;
                }
                else if(y < OwlCityTribute.HEIGHT * .35){
                    //scenes
                    state = 1;
                }
                else{
                    //play
                    fadingOut = true;
                }
            }
            else if(state == 1){
                //check scene
                if(x > 50 && x < 750 && y > OwlCityTribute.HEIGHT*.275
                        && y < OwlCityTribute.HEIGHT*.275 + scenePreviews.getHeight()){
                    selection = ((int)x - 50)/120 + 1;
                    Gdx.app.log("selection", "" + selection);
                    fadingOut = true;
                }
                else
                    state = 0;
            }
            else{
                state = 0;
            }

        }

    }

    @Override
    public void update(float dt) {
        handleInput();
        if(fadingIn){
            whiteOverlay -= dt;
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
                int level = (selection != -1) ? selection
                        : Gdx.app.getPreferences(OwlCityTribute.GAME_PREFS).getInteger("maxlevel");
                Gdx.app.log("levelchoice", "" + level);
                switch (level) {
                    case 1:
                        gsm.currentState = GameStateManager.BEACH;
                        gsm.setState(new Beach(gsm));
                        break;
                    case 2:
                        gsm.currentState = GameStateManager.OCEAN;
                        gsm.setState(new Ocean(gsm));
                        break;
                    case 3:
                        gsm.currentState = GameStateManager.CITY;
                        gsm.setState(new City(gsm));
                        break;
                    case 4:
                        gsm.currentState = GameStateManager.GRASSLAND;
                        gsm.setState(new Grassland(gsm));
                        break;
                    case 5:
                        gsm.currentState = GameStateManager.SKY;
                        gsm.setState(new Sky(gsm));
                        break;
                    default:
                        gsm.currentState = GameStateManager.SPACE;
                        gsm.setState(new Space(gsm));
                }
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
        switch (state){
            case 0:
                menuItemsOpac += fadeRate*dt;
                aboutOpac -= fadeRate*dt;
                scenesOpac -= fadeRate*dt;
                if(menuItemsOpac > 1) menuItemsOpac = 1;
                if(aboutOpac < 0) aboutOpac = 0;
                if(scenesOpac <0) scenesOpac = 0;
                break;
            case 1:
                scenesOpac += fadeRate*dt;
                aboutOpac -= fadeRate*dt;
                menuItemsOpac -= fadeRate*dt;
                if(scenesOpac > 1) scenesOpac = 1;
                if(aboutOpac < 0) aboutOpac = 0;
                if(menuItemsOpac <0) menuItemsOpac = 0;
                break;
            case 2:
                aboutOpac += fadeRate*dt;
                scenesOpac -= fadeRate*dt;
                menuItemsOpac -= fadeRate*dt;
                if(aboutOpac > 1) aboutOpac = 1;
                if(scenesOpac < 0) scenesOpac = 0;
                if(menuItemsOpac <0) menuItemsOpac = 0;
                break;
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
        sb.draw(shadow, OwlCityTribute.WIDTH /2 - shadow.getWidth()/2 - plane.getWidth()*.1f, OwlCityTribute.HEIGHT*.12f - shadow.getHeight()/2);
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

        sr.end();
        //Vignette;
        sb.begin();
        if(menuItemsOpac != 0) {
            sb.setColor(whitevalue, whitevalue, whitevalue, menuItemsOpac);
            sb.draw(menuitems, OwlCityTribute.WIDTH / 2 - menuitems.getWidth() / 2, OwlCityTribute.HEIGHT * .21f);
        }

        if(scenesOpac != 0) {
            int level = Gdx.app.getPreferences(OwlCityTribute.GAME_PREFS).getInteger("level");
            for (int i = 0; i < 6; i++) {
                if (level <= i) sb.setColor(whitevalue/2, whitevalue/2, whitevalue/2, scenesOpac);
                else sb.setColor(whitevalue, whitevalue, whitevalue, scenesOpac);
                sb.draw(scenes.get(i), 50 + i * 120, OwlCityTribute.HEIGHT * .275f);
            }
        }

        if(aboutOpac != 0) {
            font.setColor(whitevalue, whitevalue, whitevalue, aboutOpac);
            font.drawMultiLine(sb, "  This is not a game, but rather an environment.\n"
                    + "Collect the notes and let them tell a story. Relax.", 200, OwlCityTribute.HEIGHT * .4f);
        }

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
        shadow.dispose();
        font.dispose();
        menuitems.dispose();
        scenePreviews.dispose();
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
}
