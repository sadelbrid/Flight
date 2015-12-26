package com.seth.owlcity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class OwlCityTribute extends ApplicationAdapter {
	SpriteBatch batch;
	GameStateManager gsm;
	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;
	public static Music promenade;
	public static boolean paused;
	public static final String GAME_PREFS = "flightPreferences";
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		paused = false;
		promenade = Gdx.audio.newMusic(Gdx.files.internal("promenade.mp3"));
		promenade.setLooping(true);

		Preferences prefs = Gdx.app.getPreferences(GAME_PREFS);
		if(!prefs.contains("level")){
			prefs.putInteger("level", 1);
			prefs.flush();
		}

		if(!prefs.contains("maxlevel")){
			prefs.putInteger("maxlevel", 1);
			prefs.flush();
		}

		gsm = new GameStateManager();
		gsm.currentState = GameStateManager.GRASSLAND;
		gsm.push(new Grassland(gsm));
		promenade.play();
	}

	@Override
	public void render () {
		if(!paused) {
			if(gsm.currentState == GameStateManager.SPACE) Gdx.gl.glClearColor(1, 1, 1, 1);
			else Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			float deltaTime = Gdx.graphics.getDeltaTime();
			if(deltaTime != 0) {
				gsm.update(deltaTime);
				gsm.render(batch);
			}
		}
	}

	@Override
	public void dispose(){
		promenade.dispose();
		batch.dispose();
		super.dispose();
	}

	@Override
	public void pause() {
		super.pause();
		paused = true;
	}

	@Override
	public void resume() {
		super.resume();
		paused = false;
	}
}
