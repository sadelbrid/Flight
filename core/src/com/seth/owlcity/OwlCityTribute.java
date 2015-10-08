package com.seth.owlcity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
	public static Music intro;
	public static Array<Music> loops;
	public static boolean paused;
	public static float introTime, loopOneTime, loopTwoTime;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		paused = false;
		introTime = loopOneTime = loopTwoTime = 0f;
		intro = Gdx.audio.newMusic(Gdx.files.internal("intro.mp3"));
		loops = new Array<Music>();
		loops.add(Gdx.audio.newMusic(Gdx.files.internal("loop2.mp3")));
		loops.add(Gdx.audio.newMusic(Gdx.files.internal("loop2.mp3")));
		loops.get(0).setLooping(false);
		loops.get(1).setLooping(false);
		gsm = new GameStateManager();
		gsm.push(new Menu(gsm));
		intro.play();
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
		super.dispose();
		intro.dispose();
		loops.get(0).dispose();
		loops.get(1).dispose();
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
