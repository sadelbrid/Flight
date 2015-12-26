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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Random;

import sun.font.TrueTypeFont;

/**
 * Created by Seth on 8/10/15.
 */
public class Beach extends State {
    private static final int NUM_SHIMMERS = 30;
    private Player player;
    private Texture vignette;
    private Texture background;
    private Texture sandTexture;
    private Texture waterTexture;
    private Texture cloud;
    private Texture shadow;
    private Texture pauseButton;
    private float playerXOffset;
    private float femaleXOffset;
    private Array<FluctuatingObject> sand;
    private Array<FluctuatingObject> waterForeground;
    private Array<FluctuatingObject> waterBackground;
    private Array<FluctuatingObject> clouds;
    private Array<Shimmer> shimmers;
    private FluctuatingObject female;
    private TextBox textBox;
    private ShapeRenderer sr;
    private TextureRegion planeRegion;
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
    private boolean intro;
    private float leftEdge;
    private float femaleOffsetSpeed;
    private boolean adjusting;
    private Random random;
    private float noteScale;
    private boolean shrinking;
    //private fl
    public Beach(GameStateManager gsm){
        super(gsm);
        cam.setToOrtho(false, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);
        player = new Player(OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT, OwlCityTribute.HEIGHT, OwlCityTribute.HEIGHT*.1f);
        background = new Texture("background.png");
        sandTexture = new Texture("sand.png");
        waterTexture = new Texture("waves.png");
        vignette = new Texture("vignette.png");
        cloud = new Texture("cloud.png");
        shadow = new Texture("shadow.png");
        pauseButton = new Texture("pause.png");
        playerXOffset = OwlCityTribute.WIDTH*.4f;

        textCount = 4;
        sand = new Array<FluctuatingObject>();
        noteAnim = new Animation(new TextureRegion(new Texture("paper.png")), 9, .5f);
        planeRegion = new TextureRegion(player.getPlane());
        for(int i = 0; i<3; i++){
            //sand.add(new FluctuatingObject(i*sandTexture.getWidth() + (int)(player.getPosition().x - cam.viewportWidth / 2 + playerXOffset), 0, -50, 0, 0));
            sand.add(new FluctuatingObject(i*sandTexture.getWidth() - (int)(cam.viewportWidth / 2), 0, -50, 0, 0));
        }
        waterForeground = new Array<FluctuatingObject>();
        for(int i = 0; i<3; i++){
            //waterForeground.add(new FluctuatingObject(i*waterTexture.getWidth() + (int)(player.getPosition().x - cam.viewportWidth / 2 + playerXOffset), 2f, 20, 10, -10));
            waterForeground.add(new FluctuatingObject(i*waterTexture.getWidth() - (int)(cam.viewportWidth / 2), 2f, 20, 10, -10));
        }
        waterBackground = new Array<FluctuatingObject>();
        for(int i = 0; i<3; i++){
            //waterBackground.add(new FluctuatingObject(i*waterTexture.getWidth() + (int)(player.getPosition().x - cam.viewportWidth / 2 + playerXOffset-waterTexture.getWidth()*.5), 1f, 20, 20, -5));
            waterBackground.add(new FluctuatingObject(i*waterTexture.getWidth() + (int)(-cam.viewportWidth / 2 - waterTexture.getWidth()*.5), 1f, 20, 20, -5));

        }
        clouds = new Array<FluctuatingObject>();
        clouds.add(new FluctuatingObject((int)(player.getPosition().x - cam.viewportWidth*.1), 0f, 300, 0, -1));
        clouds.add(new FluctuatingObject((int)(player.getPosition().x + cam.viewportWidth*.7), 0f, 300, 0, -1));
        sr = new ShapeRenderer();
        noteRotation = 0f;

        for(int i = 0; i < 7; i++) this.sceneText.add(new ArrayList<String>());
        sceneText.get(0).add("We woke up beside");
        sceneText.get(0).add("the ocean.");

        sceneText.get(1).add("We floated and looked aft");
        sceneText.get(1).add("to watch the moon rise.");

        sceneText.get(2).add("The oceanic vista");
        sceneText.get(2).add("was so divine.");

        sceneText.get(3).add("I watched you sailing far");
        sceneText.get(3).add("above the seashore.");

        sceneText.get(4).add("We laid on the beach and vowed");
        sceneText.get(4).add("that we'd live and we'd learn.");

        sceneText.get(5).add("While singing about the tide");
        sceneText.get(5).add("and the ocean surf.");

        sceneText.get(6).add("But that was years");
        sceneText.get(6).add("ago.");
        parameter.size = 20;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'.";

        font = generator.generateFont(parameter);
        generator.dispose();
        textBox = new TextBox();
        sr = new ShapeRenderer();
        waiting = false;
        zooming = false;
        whiteValue = 0f;
        readyToFade = false;
        boxInitialized = false;
        intro = true;
        female = new FluctuatingObject((int)(cam.viewportWidth/2 - planeRegion.getRegionWidth()), 2f, OwlCityTribute.HEIGHT/2, (int)(OwlCityTribute.HEIGHT*.2), 100);
        femaleXOffset = OwlCityTribute.WIDTH*.5f + planeRegion.getRegionWidth();
        leftEdge = female.getPosition().x - (cam.viewportWidth/2) + femaleXOffset;
        femaleOffsetSpeed = 70f;
        //player.getPosition().x = ((cam.viewportWidth/2) / femaleOffsetSpeed) *  female.MOVEMENT + playerXOffset;
        player.getPosition().x = 1600f - OwlCityTribute.WIDTH*.4f;
        adjusting = false;
        random = new Random(System.currentTimeMillis());
        //Shimmers
        shimmers = new Array<Shimmer>();
        for(int i = 0; i < NUM_SHIMMERS; i++){
            shimmers.add(new Shimmer(random.nextInt(OwlCityTribute.WIDTH),
                    random.nextInt((int)(OwlCityTribute.HEIGHT * .48f - OwlCityTribute.HEIGHT * .42f)) + OwlCityTribute.HEIGHT * .42f,
                    random.nextInt((int)(OwlCityTribute.WIDTH*.0025f) + 1),
                    random.nextInt(Shimmer.MAX_LIFE) + 50));
        }
        noteScale = 1f;
        shrinking = false;
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched() && textCount < sceneText.size()-1) {
            float scaleX = Gdx.graphics.getWidth()/OwlCityTribute.WIDTH;
            float scaleY = Gdx.graphics.getHeight()/OwlCityTribute.HEIGHT;
            float x = Gdx.input.getX()/scaleX;
            float y = Gdx.input.getY()/scaleY;
            if(x < cam.viewportWidth*.05f + pauseButton.getWidth() &&
                    y < cam.viewportHeight*.05f + pauseButton.getHeight()){
                gsm.push(new Pause(gsm));
                paused = true;
            }
            else if(!intro) player.lift();
        }
    }

    @Override
    public void update(float dt) {
        if(!paused) {
            player.update(dt);
            if (intro) {
                female.update(dt);
                femaleXOffset -= femaleOffsetSpeed * dt;
                if (femaleXOffset < -cam.viewportWidth / 2) {
                    //intro = false;
                    note = new FluctuatingObject((int) (player.getPosition().x + cam.viewportWidth), 2f, 200, 50, -200);
                    adjusting = true;
                    player.xOffset = cam.position.x - player.getPosition().x;
                    player.xOffset = cam.position.x - player.getPosition().x;
                }
                leftEdge = female.getPosition().x - (cam.viewportWidth / 2) + femaleXOffset;
                if (adjusting) {
                    player.xOffset += 20 * dt;
                    if (player.xOffset > OwlCityTribute.WIDTH * .4) {
                        player.xOffset = OwlCityTribute.WIDTH * .4f;
                        adjusting = false;
                        intro = false;
                    }
                }
                cam.position.x = female.getPosition().x + femaleXOffset;
            } else {
                note.update(dt);
                noteAnim.update(dt);
                noteRotation += 100f * dt;
                if (noteRotation > 360) noteRotation = 0;
                leftEdge = player.getPosition().x - (cam.viewportWidth / 2) + player.xOffset;
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

                if (!loss && player.getPosition().x < leftEdge) {
                    loss = true;
                    this.gsm.push(new UponLoss(this.gsm));
                }

                //Check if note is hit
                if (!shrinking && Math.sqrt(Math.pow((player.getPosition().x + player.getPlane().getWidth() * .75f) - note.getPosition().x, 2) + Math.pow((player.getPosition().y + (player.getPlane().getHeight() * .75f) / 2) - (note.getPosition().y + noteAnim.getFrame().getRegionHeight() / 2), 2)) < 40) {
                    //Open textbox
                    textCount++;
                    textBox.prepare(sceneText.get(textCount).get(0), sceneText.get(textCount).get(1), .1f);
                    boxInitialized = true;

                    //Set bounds
                    int w = (int) font.getBounds(sceneText.get(textCount).get(0)).width;
                    int h = (int) (font.getBounds(sceneText.get(textCount).get(0)).height * 2.5);
                    textBox.setBounds(w, h, (int) (OwlCityTribute.WIDTH * .65) - w / 2, (int) (OwlCityTribute.HEIGHT * .75) - h / 2);
                    waiting = true;
                    shrinking = true;
                } else if (note.getPosition().x < leftEdge - noteAnim.getFrame().getRegionWidth() && !waiting && textCount < sceneText.size() - 1 && player.movement > player.maxMovement * .75) {
                    note.getPosition().x = player.getPosition().x + cam.viewportWidth;
                    note.setyOffset((int) (Math.random() * 100) + 200);
                }

                if (shrinking) {
                    noteScale -= 2.5 * dt;
                    if (noteScale < 0) {
                        shrinking = false;
                        noteScale = 1f;
                        note.getPosition().x -= cam.viewportWidth;
                    }
                }

            }

            handleInput();


            for (int i = 0; i < NUM_SHIMMERS; i++) {
                shimmers.get(i).update(dt);
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

            //Check water background
            for (int i = 0; i < waterBackground.size; i++) {
                waterBackground.get(i).update(dt);
                if (waterBackground.get(i).getPosition().x < leftEdge - waterTexture.getWidth()) {
                    int index = (i == 0) ? waterBackground.size - 1 : i - 1;
                    waterBackground.get(i).getPosition().x = waterBackground.get(index).getPosition().x + waterTexture.getWidth();
                    break;
                }
            }
            //Check water foreground
            for (int i = 0; i < waterForeground.size; i++) {
                waterForeground.get(i).update(dt);
                if (waterForeground.get(i).getPosition().x < leftEdge - waterTexture.getWidth()) {
                    int index = (i == 0) ? waterForeground.size - 1 : i - 1;
                    waterForeground.get(i).getPosition().x = waterForeground.get(index).getPosition().x + waterTexture.getWidth();
                    break;

                }
            }
            //Check sand
            for (int i = 0; i < sand.size; i++) {
                sand.get(i).update(dt);
                if (sand.get(i).getPosition().x < leftEdge - sandTexture.getWidth()) {
                    int index = (i == 0) ? sand.size - 1 : i - 1;
                    sand.get(i).getPosition().x = sand.get(index).getPosition().x + sandTexture.getWidth();
                    break;
                }
            }
            //clouds
            for (int i = 0; i < clouds.size; i++) {
                clouds.get(i).update(dt);
                if (clouds.get(i).getPosition().x < leftEdge - cloud.getWidth()) {
                    int index = (i == 0) ? clouds.size - 1 : i - 1;
                    clouds.get(i).getPosition().x = (float) (clouds.get(index).getPosition().x + cam.viewportWidth * .8);
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
                if (whiteValue == 0) {
                    dispose();
                    Preferences p = Gdx.app.getPreferences(OwlCityTribute.GAME_PREFS);
                    p.putInteger("level", 2);
                    if(p.getInteger("maxlevel") < 2){
                        p.putInteger("maxlevel", 2);
                    }
                    p.flush();
                    gsm.currentState = GameStateManager.OCEAN;
                    this.gsm.setState(new Ocean(this.gsm));
                }
            } else {
                whiteValue = (whiteValue < 1f) ? whiteValue + .2f * dt : 1f;
            }

            cam.update();
        }
    }

    @Override
    public void dispose() {
        font.dispose();
        background.dispose();
        vignette.dispose();
        sandTexture.dispose();
        cloud.dispose();
        waterTexture.dispose();
        sr.dispose();
        player.getPlane().dispose();
        shadow.dispose();
        pauseButton.dispose();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.setColor(whiteValue, whiteValue, whiteValue, 1f);
        sb.draw(background, leftEdge, 0, cam.viewportWidth, cam.viewportHeight);
        sb.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);

        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        //Draw sparkles
        for(int i = 0; i< NUM_SHIMMERS; i++){
            sr.setColor(whiteValue, whiteValue, whiteValue, Math.abs((float) Math.sin(Math.PI * shimmers.get(i).life / Shimmer.MAX_LIFE)));
            sr.circle(leftEdge + shimmers.get(i).x, shimmers.get(i).y,
                    (float)Math.abs(Math.sin(Math.PI * shimmers.get(i).life / Shimmer.MAX_LIFE))*shimmers.get(i).size);
        }
        sr.end();

        sb.begin();
        //Draw pause
        sb.draw(pauseButton, cam.position.x - cam.viewportWidth/2 + cam.viewportWidth*.05f, cam.viewportHeight*.95f - pauseButton.getHeight());

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
        sb.setColor(whiteValue,whiteValue,whiteValue, .55f);
        sb.draw(cloud, clouds.get(0).getPosition().x, clouds.get(0).getPosition().y);
        sb.draw(cloud, clouds.get(1).getPosition().x, clouds.get(1).getPosition().y);
        //Water bg
        sb.setColor(.75f*whiteValue,.75f*whiteValue,.75f*whiteValue, 1);
        for (FluctuatingObject f : waterBackground){
            sb.draw(waterTexture, f.getPosition().x, f.getPosition().y);
        }
        sb.setColor(whiteValue, whiteValue, whiteValue, 1f);
        //Water foreground
        for (FluctuatingObject f : waterForeground){
            sb.draw(waterTexture, f.getPosition().x, f.getPosition().y);
        }
        //Sand
        for (FluctuatingObject f : sand){
            sb.draw(sandTexture, f.getPosition().x, f.getPosition().y);
        }

        //draw note
        if(!intro) {
            sb.setColor(whiteValue, whiteValue*(230f/255f), whiteValue, 1f);
            sb.draw(noteAnim.getFrame(), note.getPosition().x, note.getPosition().y,
                    noteAnim.getFrame().getRegionWidth() / 2, noteAnim.getFrame().getRegionHeight() / 2,
                    noteAnim.getFrame().getRegionWidth(), noteAnim.getFrame().getRegionHeight(), noteScale, noteScale,
                    noteRotation);
        }
        else{
            //draw female
            sb.setColor(whiteValue, whiteValue*(200f/255f), whiteValue, 1f);
            //sb.set
            sb.draw(planeRegion, female.getPosition().x, female.getPosition().y, planeRegion.getRegionWidth() / 2, planeRegion.getRegionHeight() / 2, player.getPlane().getWidth() * .65f, player.getPlane().getHeight() * .65f, 1, 1, .25f*(float)Math.toDegrees(Math.cos(female.flow)));

        }

        //Draw shadow
        float temp = OwlCityTribute.HEIGHT*.45f - ((player.getPosition().y - OwlCityTribute.HEIGHT*.1f) - OwlCityTribute.HEIGHT*.45f);
        sb.setColor(whiteValue, whiteValue, whiteValue, temp/OwlCityTribute.HEIGHT*.9f);
        sb.draw(shadow, player.getPosition().x- shadow.getWidth()/2 + player.getPlane().getWidth()*.3f, OwlCityTribute.HEIGHT*.1f - shadow.getHeight()*.4f);
        //Draw player
        sb.setColor(whiteValue, whiteValue, whiteValue, 1f);
        sb.draw(planeRegion, player.getPosition().x, player.getPosition().y, planeRegion.getRegionWidth() / 2, planeRegion.getRegionHeight() / 2, player.getPlane().getWidth() * .75f, player.getPlane().getHeight() * .75f, 1, 1, player.rotation);

        sb.setColor(whiteValue, whiteValue, whiteValue, .5f);
        sb.end();
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
            Shimmer.this.life += 75*dt;
        }
    }
}
