package com.seth.owlcity.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.seth.owlcity.OwlCityTribute;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = OwlCityTribute.WIDTH;
		config.height = OwlCityTribute.HEIGHT;
		new LwjglApplication(new OwlCityTribute(), config);
	}
}
