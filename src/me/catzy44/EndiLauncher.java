package me.catzy44;

import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import me.catzy44.game.GameInstallation;
import me.catzy44.game.GameInstallationManager;
import me.catzy44.tools.Config;
import me.catzy44.tools.Starter;
import me.catzy44.tools.Updater;
import me.catzy44.tools.net.EndiAPI;
import me.catzy44.ui.creators.GameVersionCreator;
import me.catzy44.ui.creators.UserAccountCreator;
import me.catzy44.user.UserProfile;
import me.catzy44.user.UserProfileManager;
import me.catzy44.utils.Args;
import me.catzy44.utils.SystemInfo;
import me.catzy44.utils.Utils;
import me.catzy44.utils.Version;

public class EndiLauncher {
	private static EndiLauncher instance;
	
	public static final File workingDir = new File(Utils.getAppData() + "/.EndiLauncher");
	public static final Version version = new Version("1.0.3");
	
	public void start(Args args) {
		instance = this;
		
		try {
			SystemInfo.load();
			if(Updater.check(args)) {
				return;
			}
			Starter.check(args);
			Interface.preInit();
			Config.init();
			
			Interface.build();
			
			UserProfileManager.init();
			GameInstallationManager.init();
			
			Interface.setActiveUserProfile(UserProfileManager.getActiveProfile());
			
			Interface.show(true);
			
			Updater.killUpdaterIfJustUpdated(args);
			Updater.init(args);
			
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					UserProfileManager.save();
					GameInstallationManager.save();
					GameInstallation gi = GameInstallationManager.getActive();
					if(gi != null && gi.isGameRunning()) {
						gi.cleanupGame(true);
					}
				}
			}));
			
			if(UserProfileManager.getProfiles().isEmpty()) {
				UserAccountCreator.showInterface();
			}
			
			if(GameInstallationManager.getAll().isEmpty()) {
				GameVersionCreator.showInterface();
			}
			
			EndiAPI.startPinger();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		new EndiLauncher().start(new Args(args));
	}
	public static EndiLauncher getInstance() {
		return instance;
	}
	public static File getWorkingdir() {
		return workingDir;
	}
	public static void log(String s) {
		Interface.printToConsole(s);
	}
	
}
