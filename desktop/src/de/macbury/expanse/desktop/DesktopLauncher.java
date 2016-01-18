package de.macbury.expanse.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.macbury.expanse.Expanse;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1360;
		config.height = 768;
		config.resizable = false;
		config.useGL30    = true;
		config.title    = "Expanse v"+ Expanse.VERSION;
		new LwjglApplication(new Expanse(), config);
	}
}
