package de.macbury.expanse.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.macbury.expanse.Expanse;

public class DesktopLauncher {
  private static final boolean START_FULLSCREEN = false;

  public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

    if (START_FULLSCREEN) {
      config.width    = 1920;
      config.height   = 1080;
      config.fullscreen = true;
      config.vSyncEnabled = false;
    } else {
      config.width    = 1600;
      config.height   = 900;
      config.fullscreen = false;
      config.vSyncEnabled = false;
    }

		config.resizable  = false;
		config.useGL30    = false;

		config.title    	= "Expanse v"+ Expanse.VERSION;
		new LwjglApplication(new Expanse(), config);
	}
}
