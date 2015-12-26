package com.seth.owlcity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Random;

import sun.font.TrueTypeFont;
/**
 * Created by Seth on 8/10/15.
 */
public class City extends State {
    private static final int NUM_SHIMMERS = 30;
    private Player player;
    private Texture vignette;
    private Texture background;
    private Texture shadow;
    private Texture plane;
    private Texture grass;
    private Texture pauseButton;
    private Texture brush;
    private Array<FluctuatingObject> grassmoving;
    private Array<FluctuatingObject> brushmoving;
    private Array<Shimmer> shimmers;


    private TextBox textBox;
    private ShapeRenderer sr;
    private FluctuatingObject note;
    private FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("corbel.ttf"));
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private BitmapFont font;
    private Animation noteAnim;
    private  float noteRotation;
    private int textCount;
    private boolean waiting;
    private boolean zooming;
    private float whiteValue;
    private boolean readyToFade;
    private boolean boxInitialized;
    private boolean shrinking;
    private Random random;
    private float noteScale;
    public City(GameStateManager gsm){
        super(gsm);
        cam.setToOrtho(false, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);
        random = new Random(System.currentTimeMillis());
        player = new Player(OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT, OwlCityTribute.HEIGHT, OwlCityTribute.HEIGHT*.15f);
        player.gravity = -3;
        background = new Texture("cityscape.png");
        vignette = new Texture("vignette.png");
        plane = new Texture("plane.png");
        shadow = new Texture("shadow.png");
        grass = new Texture("grassrepeating.png");
        pauseButton = new Texture("pause.png");
        brush = new Texture("brush.png");
        textCount = -1;
        note = new FluctuatingObject((int)(player.getPosition().x + cam.viewportWidth), 2f, 200, 50, -100);
        noteAnim = new Animation(new TextureRegion(new Texture("paper.png")), 9, .5f);
        grassmoving = new Array<FluctuatingObject>();
        for(int i = 0; i<3; i++){
            grassmoving.add(new FluctuatingObject(i*grass.getWidth() + (int)(player.getPosition().x - cam.viewportWidth / 2 + player.xOffset), 0, -(int)(OwlCityTribute.HEIGHT*.05), 0, 0));
        }

        brushmoving = new Array<FluctuatingObject>();
        for(int i = 0; i<3; i++){
            brushmoving.add(new FluctuatingObject(i*brush.getWidth() + (int)(player.getPosition().x - cam.viewportWidth / 2 + player.xOffset), 0, -(int)(OwlCityTribute.HEIGHT*.05), 0, 0));
        }

        sr = new ShapeRenderer();
        noteRotation = 0f;

        for(int i = 0; i < 6; i++) this.sceneText.add(new ArrayList<String>());
        sceneText.get(0).add("The city sparkled in the night.\n");
        sceneText.get(0).add("How did it glow so bright?");

        sceneText.get(1).add("When you were home");
        sceneText.get(1).add("we'd sing.");

        sceneText.get(2).add("But time together wasn't");
        sceneText.get(2).add("ever quite enough.");

        sceneText.get(3).add("We knew we'd grow up");
        sceneText.get(3).add("sooner or later");

        sceneText.get(4).add("When we wasted all our");
        sceneText.get(4).add("free time alone");

        sceneText.get(5).add("So send me a postcard");
        sceneText.get(5).add("when you're away.");

        parameter.size = 20;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'.? ";

        font = generator.generateFont(parameter);
        generator.dispose();
        textBox = new TextBox();
        sr = new ShapeRenderer();
        waiting = false;
        zooming = false;
        whiteValue = 0f;
        readyToFade = false;
        boxInitialized = false;

        //Shimmers
        shimmers = new Array<Shimmer>();
        for(int i = 0; i < NUM_SHIMMERS; i++){
            shimmers.add(new Shimmer(random.nextInt(OwlCityTribute.WIDTH),
                    random.nextInt((int)(OwlCityTribute.HEIGHT * .74f - OwlCityTribute.HEIGHT * .675f)) + OwlCityTribute.HEIGHT * .875f,
                    random.nextInt((int)(OwlCityTribute.WIDTH*.0025f) + 1),
                    random.nextInt(Shimmer.MAX_LIFE) + 50));
        }

        noteScale = 1f;
        shrinking = false;
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched() && textCount < sceneText.size()-1){
            float scaleX = Gdx.graphics.getWidth()/OwlCityTribute.WIDTH;
            float scaleY = Gdx.graphics.getHeight()/OwlCityTribute.HEIGHT;
            float x = Gdx.input.getX()/scaleX;
            float y = Gdx.input.getY()/scaleY;
            if(x < cam.viewportWidth*.05f + pauseButton.getWidth() &&
                    y < cam.viewportHeight*.05f + pauseButton.getHeight()){
                gsm.push(new Pause(gsm));
                paused = true;
                Gdx.app.log("pausing", "pausing");
            }
            else player.lift();
            Gdx.app.log("event touch", Integer.toString(Gdx.input.getX()) + ", " + Integer.toString(Gdx.input.getY()));
        }
    }

    @Override
    public void update(float dt) {
        if (!paused){
            handleInput();
            player.update(dt);
            note.update(dt);
            noteAnim.update(dt);
            noteRotation += 100f * dt;
            if (noteRotation > 360) noteRotation = 0;

            //Player Offset
            if(textCount < sceneText.size()-1) {
                if (player.movement < player.maxMovement / 2) {
                    player.xOffset += 10 * dt;
                } else {
                    player.xOffset -= 20 * dt;
                }
                if (player.xOffset < OwlCityTribute.WIDTH * .4f)
                    player.xOffset = OwlCityTribute.WIDTH * .4f;
            }
            cam.position.x = player.getPosition().x + player.xOffset;
            float leftEdge = player.getPosition().x - (cam.viewportWidth / 2) + player.xOffset;

            if (!loss && player.getPosition().x < leftEdge) {
                loss = true;
                this.gsm.push(new UponLoss(this.gsm));
            }

            //Check if note is hit
            if (!shrinking && Math.sqrt(Math.pow((player.getPosition().x + plane.getWidth() * .75f) - note.getPosition().x, 2) + Math.pow((player.getPosition().y + (plane.getHeight() * .75f) / 2) - (note.getPosition().y + noteAnim.getFrame().getRegionHeight() / 2), 2)) < 40) {
                //Open textbox
                textCount++;
                textBox.prepare(sceneText.get(textCount).get(0), sceneText.get(textCount).get(1), .1f);
                boxInitialized = true;

                //Set bounds
                int w = (int) font.getBounds(sceneText.get(textCount).get(0)).width;
                //int h = (int)(font.getBounds(sceneText.get(textCount).get(0)).height*2.5);
                int h = (int) (font.getBounds(sceneText.get(textCount).get(0)).height * sceneText.get(textCount).size() * 1.5);
                textBox.setBounds(w, h, (int) (OwlCityTribute.WIDTH * .4) + w / 2, (int) (OwlCityTribute.HEIGHT * .9) - h / 2);
                waiting = true;
                shrinking = true;
            } else if (note.getPosition().x < leftEdge - noteAnim.getFrame().getRegionWidth() && !waiting && textCount < sceneText.size() - 1 && player.movement > player.maxMovement * .75) {
                note.getPosition().x = player.getPosition().x + cam.viewportWidth;
                note.setyOffset((int) (Math.random() * 200) + 200);
            }

            if (shrinking) {
                noteScale -= 2.5 * dt;
                if (noteScale < 0) {
                    shrinking = false;
                    noteScale = 1f;
                    note.getPosition().x -= cam.viewportWidth;
                }
            }

            if (textBox.readyToUpdate) {
                if (!textBox.update(dt)) waiting = true;
                else {
                    waiting = false;
                    if (textCount < sceneText.size() - 1 && player.movement > player.maxMovement * .75) {
                        note.getPosition().x = player.getPosition().x + cam.viewportWidth;
                        note.setyOffset((int) (Math.random() * 100) + 200);
                    }
                }
            }

            if (!readyToFade && boxInitialized && textCount == sceneText.size() - 1 && textBox.finished) {
                zooming = true;
            }

            //Check grass
            for (int i = 0; i < grassmoving.size; i++) {
                grassmoving.get(i).update(dt);
                if (grassmoving.get(i).getPosition().x < leftEdge - grass.getWidth() * 1.5) {
                    int index = (i == 0) ? grassmoving.size - 1 : i - 1;
                    grassmoving.get(i).getPosition().x = grassmoving.get(index).getPosition().x + grass.getWidth();
                    break;
                }
            }

            //Check brush
            for (int i = 0; i < brushmoving.size; i++) {
                brushmoving.get(i).MOVEMENT = -(player.movement/5);
                brushmoving.get(i).update(dt);
                if (brushmoving.get(i).getPosition().x < leftEdge - brush.getWidth() * 1.5) {
                    int index = (i == 0) ? brushmoving.size - 1 : i - 1;
                    brushmoving.get(i).getPosition().x = brushmoving.get(index).getPosition().x + brush.getWidth();
                    break;
                }
            }

            if (zooming) {
                cam.zoom -= .1 * dt;
                zooming = cam.zoom >= .6;
                readyToFade = !zooming;
                if (player.xOffset > player.getPlane().getWidth() / 2) player.xOffset -= 50 * dt;
                if (cam.position.y > Gdx.graphics.getHeight() * .05) cam.position.y -= 25 * dt;
            }

            if (readyToFade) {
                whiteValue = (whiteValue > 0) ? whiteValue - .2f * dt : 0f;
                if (whiteValue == 0f) {
                    dispose();
                    Preferences p = Gdx.app.getPreferences(OwlCityTribute.GAME_PREFS);
                    p.putInteger("level", 4);
                    if(p.getInteger("maxlevel") < 4){
                        p.putInteger("maxlevel", 4);
                    }
                    p.flush();
                    gsm.currentState = GameStateManager.GRASSLAND;
                    this.gsm.setState(new Grassland(this.gsm));
                }
            } else if (whiteValue < 1) {
                whiteValue += dt;
                if (whiteValue > 1f) whiteValue = 1f;
            }


            //Shimmer update
            for (int i = 0; i < NUM_SHIMMERS; i++) {
                shimmers.get(i).update(dt);
            }

            cam.update();
        }
    }

    @Override
    public void dispose() {
        font.dispose();
        background.dispose();
        vignette.dispose();
        player.getPlane().dispose();
        plane.dispose();
        shadow.dispose();
        pauseButton.dispose();
        grass.dispose();
        brush.dispose();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.setColor(whiteValue, whiteValue, whiteValue, 1f);
        sb.draw(background, player.getPosition().x - cam.viewportWidth / 2 + player.xOffset, 0, cam.viewportWidth, cam.viewportHeight);
        sb.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);

        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        //Draw sparkles
        for(int i = 0; i< NUM_SHIMMERS; i++){
            sr.setColor(whiteValue, whiteValue, whiteValue, Math.abs((float) Math.sin(Math.PI * shimmers.get(i).life / Shimmer.MAX_LIFE)));
            sr.circle(shimmers.get(i).x + player.getPosition().x - cam.viewportWidth / 2 + player.xOffset, shimmers.get(i).y,
                    (float) Math.abs(Math.sin(Math.PI * shimmers.get(i).life / Shimmer.MAX_LIFE)) * shimmers.get(i).size);
        }
        sr.end();
        sb.begin();



        //Draw pause
        sb.setColor(whiteValue, whiteValue, whiteValue, .75f);
        sb.draw(pauseButton, cam.position.x - cam.viewportWidth/2 + cam.viewportWidth*.05f, cam.viewportHeight*.95f - pauseButton.getHeight());

        //Draw text
        if(!textBox.finished) {
            sb.end();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            sr.setProjectionMatrix(cam.combined);
            sr.setAutoShapeType(true);
            sr.begin();
            sr.set(ShapeRenderer.ShapeType.Filled);
            sr.setColor(whiteValue, whiteValue, whiteValue, (textBox.alpha > .4f) ? .4f : textBox.alpha);
            float leftEdge = player.getPosition().x - cam.viewportWidth / 2 + player.xOffset;
            //Vert
            sr.rect(leftEdge + textBox.x - textBox.width / 2 - textBox.padding/2,
                    textBox.y - textBox.height / 2, textBox.width + textBox.padding, textBox.height);
            //Horiz
            sr.rect(leftEdge + textBox.x - textBox.width / 2, textBox.y + textBox.height / 2, textBox.width, textBox.padding/2);
            sr.rect(leftEdge + textBox.x - textBox.width / 2, textBox.y - textBox.height / 2, textBox.width, -textBox.padding/2);
            sr.end();

            sb.begin();
            if(textBox.boxOpened){
                font.setColor(1f, 1f, 1f, textBox.alpha);
                //First line
                font.draw(sb, textBox.firstLineBuffer, (leftEdge + textBox.x - (textBox.width / 2)), textBox.y+textBox.height/2);
                //Second line
                font.draw(sb, textBox.secondLineBuffer, (leftEdge+ textBox.x - (textBox.width / 2)), textBox.y+textBox.height/2 - font.getBounds(sceneText.get(textCount).get(0)).height*1.5f);
            }
        }



        //Grass
        sb.setColor(whiteValue, whiteValue, whiteValue, 1f);
        for (FluctuatingObject f : grassmoving){
            sb.draw(grass, f.getPosition().x, f.getPosition().y);
        }

        //draw note
        sb.setColor((200f / 255f) * whiteValue, whiteValue * (230f / 255f), whiteValue, 1f);
        sb.draw(noteAnim.getFrame(), note.getPosition().x, note.getPosition().y,
                noteAnim.getFrame().getRegionWidth() / 2, noteAnim.getFrame().getRegionHeight() / 2,
                noteAnim.getFrame().getRegionWidth(), noteAnim.getFrame().getRegionHeight(), noteScale, noteScale,
                noteRotation);

        sb.setColor((200f / 255f) * whiteValue, whiteValue, whiteValue, 1f);
        //Draw shadow
        float temp = OwlCityTribute.HEIGHT*.45f - ((player.getPosition().y - OwlCityTribute.HEIGHT*.1f) - OwlCityTribute.HEIGHT*.45f);
        sb.setColor(whiteValue, whiteValue, whiteValue, temp / OwlCityTribute.HEIGHT * .9f);
        sb.draw(shadow, player.getPosition().x - shadow.getWidth() / 2 + player.getPlane().getWidth() * .3f, OwlCityTribute.HEIGHT * .05f - shadow.getHeight() * .4f);
        //Draw player
        sb.setColor(whiteValue, whiteValue, whiteValue, 1f);
        sb.draw(plane, player.getPosition().x, player.getPosition().y, plane.getWidth() / 2, plane.getHeight() / 2,
                plane.getWidth() * .75f, plane.getHeight() * .75f, 1, 1, player.rotation, 0, 0, plane.getWidth(),
                plane.getHeight(), false, false);

        //draw brush
        for(FluctuatingObject f : brushmoving){
            sb.draw(brush, f.getPosition().x, f.getPosition().y);
        }
        sb.end();
        //HUD
        Matrix4 uiMatrix = cam.combined.cpy();
        uiMatrix.setToOrtho2D(0, 0, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);

        sb.setProjectionMatrix(uiMatrix);
        sb.begin();
        sb.draw(vignette, 0, 0, cam.viewportWidth, cam.viewportHeight);
        sb.end();
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
            Shimmer.this.life += 15*dt;
        }
    }
}
