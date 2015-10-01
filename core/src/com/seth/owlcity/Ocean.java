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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Random;

import sun.font.TrueTypeFont;
/**
 * Created by Seth on 8/10/15.
 */
public class Ocean extends State {
    private static final int NUM_SHIMMERS = 30;
    private static final int NUM_LIGHTS = 5;
    private static final int NUM_PARTICLES = 20;
    private Player player;
    private Texture vignette;
    private Texture background;
    private Texture sandTexture;
    private Texture shadow;
    private Texture cloud;
    private Texture boat;
    private Texture light;
    private Texture plane;
    private float playerXOffset;
    private Array<FluctuatingObject> sand;
    private Array<FluctuatingObject> clouds;
    private Array<Shimmer> shimmers;
    private Array<Light> lights;
    private Array<Particle> particles;
    private Ripple ripple;

    private TextBox textBox;
    private ShapeRenderer sr;
    private FluctuatingObject note;
    private FluctuatingObject boatFluctuation;
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
    public Ocean(GameStateManager gsm){
        super(gsm);
        cam.setToOrtho(false, OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT);
        random = new Random(System.currentTimeMillis());
        player = new Player(OwlCityTribute.WIDTH, OwlCityTribute.HEIGHT, OwlCityTribute.HEIGHT*.575f, OwlCityTribute.HEIGHT*.05f);
        player.gravity = -3;
        background = new Texture("oceanbg.png");
        sandTexture = new Texture("sand.png");
        vignette = new Texture("vignette.png");
        cloud = new Texture("cloud.png");
        boat = new Texture("boat.png");
        light = new Texture("light.png");
        plane = new Texture("plane.png");
        shadow = new Texture("shadow.png");
        playerXOffset = OwlCityTribute.WIDTH*.4f;
        textCount = -1;
        sand = new Array<FluctuatingObject>();
        note = new FluctuatingObject((int)(player.getPosition().x + cam.viewportWidth), 2f, 200, 50, -100);
        boatFluctuation = new FluctuatingObject(0, 1f, (int)(OwlCityTribute.HEIGHT*.71f), (int)(OwlCityTribute.HEIGHT*.008), 0);
        noteAnim = new Animation(new TextureRegion(new Texture("paper.png")), 9, .5f);
        for(int i = 0; i<3; i++){
            sand.add(new FluctuatingObject(i*sandTexture.getWidth() + (int)(player.getPosition().x - cam.viewportWidth / 2 + playerXOffset), 0, (int)(-OwlCityTribute.HEIGHT*.15f), 0, 0));
        }

        clouds = new Array<FluctuatingObject>();
        clouds.add(new FluctuatingObject((int)(player.getPosition().x - cam.viewportWidth*.1), 0f,
                random.nextInt((int)(OwlCityTribute.HEIGHT*.1f)) + (int)(OwlCityTribute.HEIGHT*.8f), 0, -25));
        clouds.add(new FluctuatingObject((int)(player.getPosition().x + cam.viewportWidth*.7), 0f,
                random.nextInt((int)(OwlCityTribute.HEIGHT*.1f)) + (int)(OwlCityTribute.HEIGHT*.8f), 0, -25));
        sr = new ShapeRenderer();
        noteRotation = 0f;
        for(int i = 0; i < 6; i++) this.sceneText.add(new ArrayList<String>());
        sceneText.get(0).add("We found ourselves");
        sceneText.get(0).add("in the sea.");

        sceneText.get(1).add("As the crashing whitecaps");
        sceneText.get(1).add("on the ocean.");

        sceneText.get(2).add("I wish we could sail our");
        sceneText.get(2).add("sad days away forever.");

        sceneText.get(3).add("So we swam the");
        sceneText.get(3).add("evening away");

        sceneText.get(4).add("I knew you'd remember");
        sceneText.get(4).add("that day in November");

        sceneText.get(5).add("since we felt");
        sceneText.get(5).add("alive again.");

        parameter.size = 20;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'. ";

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
                    random.nextInt((int)(OwlCityTribute.HEIGHT * .74f - OwlCityTribute.HEIGHT * .675f)) + OwlCityTribute.HEIGHT * .675f,
                    random.nextInt((int)(OwlCityTribute.WIDTH*.0025f) + 1),
                    random.nextInt(Shimmer.MAX_LIFE) + 50));
        }
        lights = new Array<Light>();
        for(int i = 0; i < NUM_LIGHTS; i++){
            lights.add(new Light(random.nextInt(OwlCityTribute.WIDTH), OwlCityTribute.HEIGHT * .675f, random.nextInt(2)+1, random.nextInt(Light.MAX_LIFE)));
        }

        particles = new Array<>();
        for(int i = 0; i < NUM_PARTICLES; i++){
            particles.add(new Particle(random.nextInt(OwlCityTribute.WIDTH), random.nextInt((int)(OwlCityTribute.HEIGHT*.675f))));
        }

        ripple = new Ripple(OwlCityTribute.WIDTH*.01f);
        noteScale = 1f;
        shrinking = false;
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched() && textCount < sceneText.size()-1) player.lift();
    }

    @Override
    public void update(float dt) {
        handleInput();
        player.update(dt);
        note.update(dt);
        boatFluctuation.update(dt);
        noteAnim.update(dt);
        noteRotation += 100f*dt;
        if(noteRotation > 360) noteRotation = 0;

        cam.position.x = player.getPosition().x + playerXOffset;
        float leftEdge = player.getPosition().x - (cam.viewportWidth/2) + playerXOffset;

        //Check if note is hit
        if(!shrinking && Math.sqrt(Math.pow((player.getPosition().x + plane.getWidth()*.75f) - note.getPosition().x, 2) + Math.pow((player.getPosition().y + (plane.getHeight()*.75f)/2) - (note.getPosition().y + noteAnim.getFrame().getRegionHeight()/2), 2)) < 40){
            //Open textbox
            textCount++;
            textBox.prepare(sceneText.get(textCount).get(0), sceneText.get(textCount).get(1), .1f);
            boxInitialized = true;

            //Set bounds
            int w =  (int)font.getBounds(sceneText.get(textCount).get(0)).width;
            //int h = (int)(font.getBounds(sceneText.get(textCount).get(0)).height*2.5);
            int h = (int)(font.getBounds(sceneText.get(textCount).get(0)).height*sceneText.get(textCount).size() * 1.5);
            textBox.setBounds(w, h, (int)(OwlCityTribute.WIDTH*.4) + w/2, (int)(OwlCityTribute.HEIGHT*.9)-h/2);
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

        if(!readyToFade && boxInitialized && textCount == sceneText.size()-1 && textBox.finished) {
            zooming = true;
        }

        //Check sand
        for(int i = 0; i < sand.size; i++){
            sand.get(i).update(dt);
            if(sand.get(i).getPosition().x < leftEdge - sandTexture.getWidth()*1.5){
                int index = (i == 0) ? sand.size-1 : i-1;
                sand.get(i).getPosition().x = sand.get(index).getPosition().x + sandTexture.getWidth();
                break;
            }
        }
        //clouds
        for(int i= 0; i<clouds.size; i++){
            clouds.get(i).update(dt);
            if(clouds.get(i).getPosition().x < -cloud.getWidth()/2) {
                int index = (i == 0) ? clouds.size-1 : i-1;
                clouds.get(i).getPosition().x = clouds.get(index).getPosition().x + cam.viewportWidth *.8f;
                clouds.get(i).yOffset = random.nextInt((int)(OwlCityTribute.HEIGHT*.1f)) + (int)(OwlCityTribute.HEIGHT*.8f);
            }
        }
        if(zooming){
            cam.zoom -= .1*dt;
            zooming = cam.zoom >=.6;
            readyToFade = !zooming;
            if(playerXOffset> player.getPlane().getWidth()/2) playerXOffset -= 50*dt;
            if(cam.position.y > Gdx.graphics.getHeight()*.05)cam.position.y -= 25*dt;
        }

        if(readyToFade) {
            whiteValue = (whiteValue > 0) ? whiteValue - .2f * dt : 0f;
            if (whiteValue == 0f){
                dispose();
                this.gsm.setState(new Sky(this.gsm));
            }
        }
        else if(whiteValue<1){
            whiteValue += dt;
            if(whiteValue > 1f) whiteValue = 1f;
        }


        //Shimmer update
        for(int i = 0; i < NUM_SHIMMERS; i++){
            shimmers.get(i).update(dt);
        }

        //Lights update
        for(int i = 0; i < NUM_LIGHTS; i++){
            lights.get(i).update(dt);
            if(lights.get(i).x < -light.getWidth()) lights.get(i).x = cam.viewportWidth;
        }

        //Particle update
        for(int i = 0; i < NUM_PARTICLES; i++){
            particles.get(i).update(dt);
            if(particles.get(i).x < leftEdge) particles.get(i).x += cam.viewportWidth;
            if(particles.get(i).y < 0) {
                particles.get(i).y = OwlCityTribute.HEIGHT*.675f;
                particles.get(i).life = 0;
            }
        }
        if(ripple.finished && boatFluctuation.getPosition().y < boatFluctuation.yOffset - boatFluctuation.range*.95) ripple.finished = ripple.reset(OwlCityTribute.WIDTH*.01f);
        if(!ripple.finished) ripple.update(dt);

        cam.update();
    }

    @Override
    public void dispose() {
        font.dispose();
        background.dispose();
        vignette.dispose();
        sandTexture.dispose();
        cloud.dispose();
        boat.dispose();
        light.dispose();
        player.getPlane().dispose();
        plane.dispose();
        shadow.dispose();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.setColor(whiteValue, whiteValue, whiteValue, 1f);
        sb.draw(background, player.getPosition().x - cam.viewportWidth / 2 + playerXOffset, 0, cam.viewportWidth, cam.viewportHeight);
        //Sand
//        for (FluctuatingObject f : sand){
//            sb.draw(sandTexture, f.getPosition().x, f.getPosition().y);
//        }
        sb.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);

        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        //Draw sparkles
        for(int i = 0; i< NUM_SHIMMERS; i++){
            sr.setColor(whiteValue, whiteValue, whiteValue, Math.abs((float) Math.sin(Math.PI * shimmers.get(i).life / Shimmer.MAX_LIFE)));
            sr.circle(shimmers.get(i).x + player.getPosition().x - cam.viewportWidth/2 + playerXOffset, shimmers.get(i).y,
                    (float)Math.abs(Math.sin(Math.PI * shimmers.get(i).life / Shimmer.MAX_LIFE))*shimmers.get(i).size);
        }

        //draw particles
        for(int i = 0; i< NUM_PARTICLES; i++){
            sr.setColor(whiteValue*100f/255f, whiteValue*225f/255f, whiteValue*150f/255f, Math.abs((float) Math.sin(Math.PI * particles.get(i).life / Particle.MAX_LIFE)));
            sr.rect(particles.get(i).x, particles.get(i).y, 3, 3);
                    //(float)Math.abs(Math.sin(Math.PI * shimmers.get(i).life / Shimmer.MAX_LIFE))*shimmers.get(i).size);
        }

        sr.setColor(whiteValue, whiteValue, whiteValue, (float) Math.sin(Math.PI * ripple.life / Ripple.MAX_LIFE));
        //sr.set(ShapeRenderer.ShapeType.Line);
        if(!ripple.finished){
            sr.circle(player.getPosition().x - cam.viewportWidth/2 + playerXOffset + cam.viewportWidth*.3f, cam.viewportHeight*.7f, ripple.radius);
        }
        sr.end();

        sb.begin();
        sb.draw(boat, player.getPosition().x - cam.viewportWidth/2 + playerXOffset + cam.viewportWidth*.3f, boatFluctuation.getPosition().y, boat.getWidth()/4, boat.getHeight()/4);
        sb.draw(boat, player.getPosition().x - cam.viewportWidth / 2 + playerXOffset + cam.viewportWidth * .85f, OwlCityTribute.HEIGHT * .74f, boat.getWidth() / 6, boat.getHeight() / 6);

        //clouds
        sb.setColor(whiteValue, whiteValue, whiteValue, .75f);
        sb.draw(cloud, player.getPosition().x - cam.viewportWidth/2 + playerXOffset + clouds.get(0).getPosition().x, clouds.get(0).getPosition().y, cloud.getWidth()/2,cloud.getHeight()/2);
        sb.draw(cloud, player.getPosition().x - cam.viewportWidth/2 + playerXOffset + clouds.get(1).getPosition().x, clouds.get(1).getPosition().y, cloud.getWidth()/2,cloud.getHeight()/2);



        //Draw text
        if(!textBox.finished) {
            sb.end();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            sr.setProjectionMatrix(cam.combined);
            sr.setAutoShapeType(true);
            sr.begin();
            sr.set(ShapeRenderer.ShapeType.Filled);
            sr.setColor(whiteValue, whiteValue, whiteValue, (textBox.alpha > .4f) ? .4f : textBox.alpha);
            float leftEdge = player.getPosition().x - cam.viewportWidth / 2 + playerXOffset;
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



        //Sand
        for (FluctuatingObject f : sand){
            sb.draw(sandTexture, f.getPosition().x, f.getPosition().y);
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
        sb.draw(plane, player.getPosition().x, player.getPosition().y, plane.getWidth() / 2, plane.getHeight() / 2,
                plane.getWidth() * .75f, plane.getHeight() * .75f, 1, 1, player.rotation, 0, 0, plane.getWidth(),
                plane.getHeight(), false, false);

        //Draw lights
        for(int i = 0; i<NUM_LIGHTS; i++){
            sb.setColor(whiteValue, whiteValue, whiteValue, Math.abs((float) Math.sin(Math.PI * lights.get(i).life / Shimmer.MAX_LIFE)));
            sb.draw(light,lights.get(i).x + player.getPosition().x - cam.viewportWidth/2 + playerXOffset, lights.get(i).y - light.getHeight()*(1f/lights.get(i).size), light.getWidth()*(1f/lights.get(i).size), light.getHeight()*(1f/lights.get(i).size));
        }
        sb.setColor(whiteValue, whiteValue, whiteValue, .5f);
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
            Shimmer.this.life += 75*dt;
        }
    }

    private class Light{
        public static final int MAX_LIFE = 100;
        public float x, y;
        public float size;
        public float life;

        public Light(float x, float y, float s, float l){
            Light.this.x = x;
            Light.this.y = y;
            Light.this.size = s;
            Light.this.life = l;
        }

        public void update(float dt){
            Light.this.life += 10*dt;
            x -= dt;
        }
    }

    private class Particle{
        public static final int MAX_LIFE = 100;
        public float x, y;
        public float life;

        public Particle(int x, int y){
            this.x = x;
            this.y = y;
            life = (int)(Math.random()*30) + 100;
        }

        public void update(float dt){
            Particle.this.x += 10*dt;
            Particle.this.y -= 10f*dt;
            life += 5*dt;
        }
    }

    private class Ripple{
        public static final int MAX_LIFE = 100;
        public boolean finished;
        public float radius;
        public float life;
        public Ripple(float r){
            Ripple.this.finished = true;
            Ripple.this.radius = r;
            Ripple.this.life = 100;
        }

        public void update(float dt){
            Ripple.this.life -= dt;
            radius += dt;
            finished = life < 0;
        }

        public boolean reset(float r){
            radius = r;
            Ripple.this.life = 100;
            return false;
        }
    }

    @Override
    public void reload() {

    }
}
