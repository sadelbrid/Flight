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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Seth on 8/27/15.
 */
public class Sky extends State {
    private static final int NUM_SHIMMERS = 30;
    private Player player;
    private Texture vignette;
    private Texture background;
    private Texture cloud;
    private float playerXOffset;
    private Array<FluctuatingObject> clouds;
    private Array<Shimmer> shimmers;

    private TextBox textBox;
    private ShapeRenderer sr;
    private TextureRegion planeRegion;
    private FluctuatingObject note;
    private FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("corbel.ttf"));
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    private BitmapFont font;
    private Animation noteAnim;
    private float noteRotation;
    private int textCount;
    private boolean waiting;
    private float whiteValue;
    private boolean readyToFadeBlack;
    private boolean readyToFadeWhite;
    private boolean boxInitialized;
    private boolean fallen;
    private Random random;
    private float whiteOverlay = 0f;
    private float noteScale;
    private boolean shrinking;
    public Sky(GameStateManager gsm){
        super(gsm);
        cam.setToOrtho(false, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);
        player = new Player(OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT, OwlCityTribute.HEIGHT, -OwlCityTribute.HEIGHT*.1f);
        player.getPosition().y = OwlCityTribute.HEIGHT;
        background = new Texture("skybg.png");
        vignette = new Texture("vignette.png");
        cloud = new Texture("cloud.png");
        playerXOffset = OwlCityTribute.WIDTH*.4f;
        textCount = -1;
        note = new FluctuatingObject((int)(player.getPosition().x + cam.viewportWidth), 2f, 200, 50, -250);
        noteAnim = new Animation(new TextureRegion(new Texture("paper.png")), 9, .5f);
        planeRegion = new TextureRegion(player.getPlane());

        clouds = new Array<FluctuatingObject>();
        clouds.add(new FluctuatingObject((int)(player.getPosition().x - cam.viewportWidth*.1), 0f, -(int)(OwlCityTribute.HEIGHT*.1f), 0, -1));
        clouds.add(new FluctuatingObject((int)(player.getPosition().x + cam.viewportWidth*.7), 0f, -(int)(OwlCityTribute.HEIGHT*.1f), 0, -1));
        sr = new ShapeRenderer();
        noteRotation = 0f;
        for(int i = 0; i < 5; i++) this.sceneText.add(new ArrayList<String>());
        sceneText.get(0).add("I dreamt we were");
        sceneText.get(0).add("above the blue.");

        sceneText.get(1).add("and I was right beside you");
        sceneText.get(1).add("floating away.");

        sceneText.get(2).add("The Earth didn't care");
        sceneText.get(2).add("that we were up there.");

        sceneText.get(3).add("The world looked brighter");
        sceneText.get(3).add("from that high altitude.");

        sceneText.get(4).add("We could go anywhere.");

        parameter.size = 20;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'.";

        font = generator.generateFont(parameter);
        generator.dispose();
        textBox = new TextBox();
        sr = new ShapeRenderer();
        waiting = false;
        whiteValue = 0f;
        readyToFadeBlack = false;
        boxInitialized = false;

        random = new Random(System.currentTimeMillis());
        //Shimmers
        shimmers = new Array<Shimmer>();
        for(int i = 0; i < NUM_SHIMMERS; i++){
            shimmers.add(new Shimmer(random.nextInt(OwlCityTribute.WIDTH),
                    random.nextInt((int)(OwlCityTribute.HEIGHT * .5f - OwlCityTribute.HEIGHT * .3f)) + OwlCityTribute.HEIGHT * .3f,
                    random.nextInt((int)(OwlCityTribute.WIDTH*.0025f) + 1),
                    random.nextInt(Shimmer.MAX_LIFE) + 50));
        }
        fallen = false;
        noteScale = 1f;
        shrinking = false;
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched() /*&& textCount < sceneText.size()-1*/ && !readyToFadeWhite && !fallen) player.lift();
    }

    @Override
    public void update(float dt) {
        handleInput();
        if(!readyToFadeWhite) player.update(dt); //So player can be controlled upward from Sky
        else{
            //Lift player
            whiteOverlay += .3f*dt;
            player.getVelocity().add(0, -player.gravity/2);
            player.getVelocity().scl(dt);
            player.getPosition().add(player.movement * dt, player.getVelocity().y);
            player.getVelocity().scl(1 / dt);
            float temp = player.normalize(-450, 200, (float)-Math.PI/4f, (float)Math.PI/4f, player.getVelocity().y);
            player.rotation = 25*(float)Math.sin(temp);
            if(whiteOverlay > 1f){
                dispose();
                this.gsm.setState(new Space(this.gsm));
            }
        }
        readyToFadeBlack = fallen = player.getPosition().y == -OwlCityTribute.HEIGHT*.1f;
        note.update(dt);
        noteAnim.update(dt);
        noteRotation += 100f*dt;
        if(noteRotation > 360) noteRotation = 0;

        cam.position.x = player.getPosition().x + playerXOffset;
        float leftEdge = player.getPosition().x - (cam.viewportWidth/2) + playerXOffset;

        //Check if note is hit
        if(!shrinking && Math.sqrt(Math.pow((player.getPosition().x + player.getPlane().getWidth()*.75f) - note.getPosition().x, 2) + Math.pow((player.getPosition().y + (player.getPlane().getHeight()*.75f)/2) - (note.getPosition().y + noteAnim.getFrame().getRegionHeight()/2), 2)) < 40){
            //Open textbox
            textCount++;
            textBox.prepare(sceneText.get(textCount)
                            .get(0),
                    (sceneText.get(textCount).size() == 1) ? "" : sceneText.get(textCount).get(1), .1f);
            boxInitialized = true;

            //Set bounds
            int w =  (int)font.getBounds(sceneText.get(textCount).get(0)).width;
            //int h = (int)(font.getBounds(sceneText.get(textCount).get(0)).height*2.5);
            int h = (int)(font.getBounds(sceneText.get(textCount).get(0)).height*sceneText.get(textCount).size() * 1.5);
            textBox.setBounds(w, h, (int)(OwlCityTribute.WIDTH*.75) - w/2, (int)(OwlCityTribute.HEIGHT*.875)-h/2);
            waiting = true;
            shrinking = true;
        }
        else if(note.getPosition().x < leftEdge - noteAnim.getFrame().getRegionWidth() && !waiting && textCount < sceneText.size()-1 && player.movement > player.maxMovement*.75){
            note.getPosition().x = player.getPosition().x + cam.viewportWidth;
            note.setyOffset((int) (Math.random() * 100) + 200);
        }

        if(shrinking) {
            noteScale -=2.5*dt;
            if(noteScale < 0){
                shrinking = false;
                noteScale = 1f;
                note.getPosition().x -= cam.viewportWidth;
            }
        }

        if(textBox.readyToUpdate) {
            if(!textBox.update(dt)) waiting = true;
            else {
                waiting = false;
                if(textCount < sceneText.size()-1 && player.movement > player.maxMovement*.75) {
                    note.getPosition().x = player.getPosition().x + cam.viewportWidth;
                    note.setyOffset((int) (Math.random() * 100) + 200);
                }
            }
        }

        if(!fallen && boxInitialized && textCount == sceneText.size()-1 && textBox.finished) {
            readyToFadeWhite = true;
        }



        //clouds
        for(int i= 0; i<clouds.size; i++){
            clouds.get(i).update(dt);
            if(clouds.get(i).getPosition().x < leftEdge - cloud.getWidth()) {
                int index = (i == 0) ? clouds.size-1 : i-1;
                clouds.get(i).getPosition().x = (float)(clouds.get(index).getPosition().x + cam.viewportWidth*.8);
                clouds.get(i).yOffset = random.nextInt((int)(OwlCityTribute.HEIGHT*.2f)) - (int)(OwlCityTribute.HEIGHT*.1f);
            }
        }

        if(readyToFadeBlack){
            whiteValue  = (whiteValue > 0) ? whiteValue - .2f*dt : 0f;
            if(whiteValue == 0f){
                if(player.getPosition().y == -OwlCityTribute.HEIGHT*.1f){
                    dispose();
                    this.gsm.setState(new Sky(this.gsm));
                }
            }
        }
        else{
            whiteValue = (whiteValue < 1f) ? whiteValue+.4f*dt : 1f;
        }

        //Shimmer update
        for(int i = 0; i < NUM_SHIMMERS; i++){
            shimmers.get(i).update(dt);
        }

        cam.update();
    }

    @Override
    public void dispose() {
        font.dispose();
        background.dispose();
        vignette.dispose();
        cloud.dispose();
        player.getPlane().dispose();
    }

    @Override
    public void render(SpriteBatch sb) {
        float leftEdge = player.getPosition().x - cam.viewportWidth / 2 + playerXOffset;
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.setColor(whiteValue, whiteValue, whiteValue, 1f);
        sb.draw(background, player.getPosition().x - cam.viewportWidth / 2 + playerXOffset, 0, cam.viewportWidth, cam.viewportHeight);
        sb.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);

        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        //Draw sparkles
        for(int i = 0; i< NUM_SHIMMERS; i++){
            sr.setColor(whiteValue, whiteValue, whiteValue, Math.abs((float) Math.sin(Math.PI * shimmers.get(i).life / Shimmer.MAX_LIFE)));
            sr.circle(shimmers.get(i).x + player.getPosition().x - cam.viewportWidth / 2 + playerXOffset, shimmers.get(i).y,
                    (float) Math.abs(Math.sin(Math.PI * shimmers.get(i).life / Shimmer.MAX_LIFE)) * shimmers.get(i).size);
        }
        sr.end();
        sb.begin();

        //Draw text
        if(!textBox.finished) {
            sb.end();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            sr.setProjectionMatrix(cam.combined);
            sr.setAutoShapeType(true);
            sr.begin();
            sr.set(ShapeRenderer.ShapeType.Filled);
            sr.setColor(1f, 1f, 1f, (textBox.alpha > .4f) ? .4f : textBox.alpha);
            //Vert
            sr.rect(leftEdge + textBox.x - textBox.width / 2 - textBox.padding/2,
                    textBox.y - textBox.height / 2, textBox.width + textBox.padding, textBox.height);
            //Horiz
            sr.rect(leftEdge + textBox.x - textBox.width/2, textBox.y + textBox.height/2, textBox.width, textBox.padding/2);
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


        //clouds
        sb.setColor(whiteValue, whiteValue, whiteValue, .75f);
        sb.draw(cloud, clouds.get(0).getPosition().x, clouds.get(0).getPosition().y);
        sb.draw(cloud, clouds.get(1).getPosition().x, clouds.get(1).getPosition().y);

        //draw note
        sb.setColor(whiteValue, whiteValue * (230f / 255f), whiteValue, 1f);
        sb.draw(noteAnim.getFrame(), note.getPosition().x, note.getPosition().y,
                noteAnim.getFrame().getRegionWidth() / 2, noteAnim.getFrame().getRegionHeight() / 2,
                noteAnim.getFrame().getRegionWidth(), noteAnim.getFrame().getRegionHeight(), noteScale, noteScale,
                noteRotation);

        sb.setColor(whiteValue, whiteValue, whiteValue, 1f);
        //Draw player
        sb.draw(planeRegion, player.getPosition().x, player.getPosition().y, planeRegion.getRegionWidth() / 2,
                planeRegion.getRegionHeight() / 2, player.getPlane().getWidth() * .75f,
                player.getPlane().getHeight() * .75f, 1, 1, player.rotation);

        sb.setColor(whiteValue, whiteValue, whiteValue, .5f);
        sb.end();
        //HUD
        Matrix4 uiMatrix = cam.combined.cpy();
        uiMatrix.setToOrtho2D(0, 0, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);

        sb.setProjectionMatrix(uiMatrix);
        sb.begin();
        sb.draw(vignette, 0, 0, cam.viewportWidth, cam.viewportHeight);
        sb.end();
        sr.begin(ShapeRenderer.ShapeType.Filled);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        sr.setColor(1f, 1f, 1f, whiteOverlay);
        sr.setProjectionMatrix(cam.combined);
        sr.rect(leftEdge, 0, cam.viewportWidth, cam.viewportHeight);
        sr.end();
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
        planeRegion = new TextureRegion(player.getPlane());
    }
}
