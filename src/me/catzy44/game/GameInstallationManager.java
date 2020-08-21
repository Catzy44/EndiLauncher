package me.catzy44.game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import me.catzy44.EndiLauncher;
import me.catzy44.Interface;
import me.catzy44.utils.Utils;

public class GameInstallationManager {
	private static Gson gson = new Gson();

	private static File versionManifest = new File(EndiLauncher.getWorkingdir(), "/launcher/versionManifest_v2.json");
	private static File versionsDir = new File(EndiLauncher.getWorkingdir(), "/launcher/versions/");
	public static File games = new File(EndiLauncher.getWorkingdir(), "/game");
	private static JsonObject manifest;
	
	public static String osName = "windows";
	public static String osVersion = "^10\\.";
	public static String arch = "x86";
	
	private static List<GameInstallation> installations = new ArrayList<GameInstallation>();
	private static GameInstallation activeInstallation;

	public static void init() {
		
		Utils.mkDirs(games);
		
		//String activeInstall = Config.getConfig().get("activeInstall") == null ? null : Config.getConfig().get("activeInstall").getAsString();
		File[] files = games.listFiles();

		for(File f : files) {
			if(!f.isDirectory() || !f.exists()) {
				continue;
			}
			String name = f.getName();
			File manifest = new File(f,"installation.json");
			if(!manifest.exists() || !manifest.isFile()) {
				continue;
			}
			GameInstallation gi = new GameInstallation(name);
			/*if(activeInstall != null && name.equals(activeInstall)) {
				activeInstallation = gi;
			}*/
			installations.add(gi);
		}
		
		
		if(/*activeInstall == null &&*/ installations.size() > 0) {
			activeInstallation = installations.get(0);
		}
		
		if(activeInstallation != null) {
			//Interface.setActiveGameInstallation(activeInstallation);
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				createDir();
				updateLocalFile();
				processLocalFile();
				//Interface.reloadVersions();//moved to setactivegameinstallation in interface class
				
				Sodium.loadManifest();
				ForgeVersionManager.updateLocalVersionsFile();
				ForgeVersionManager.updateLocalPromosFile();
				ForgeVersionManager.loadVersions();
				ForgeVersionManager.loadPromos();
				Interface.reloadForgeComboBox();
				Interface.reloadVersions();
			}
		}).start();
	}
	
	public static void save() {
		if(activeInstallation != null) {
			//Config.getConfig().addProperty("activeInstall", activeInstallation.getName());
		}
		for(GameInstallation ga : installations) {
			try {
				ga.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	public static GameInstallation getActive() {
		return activeInstallation;
	}
	
	

	public static void setActive(GameInstallation activeInstallation) {
		GameInstallationManager.activeInstallation = activeInstallation;
	}

	private static void createDir() {
		File x = new File(EndiLauncher.getWorkingdir(), "/launcher");
		if (!x.exists() || !x.isDirectory()) {
			x.mkdirs();
		}
		if (!versionsDir.exists() || !versionsDir.isDirectory()) {
			versionsDir.mkdirs();
		}
	}

	public static boolean updateLocalFile() {
		try {
			String dataS = Utils.readEverythingFromURL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
			JsonObject data = gson.fromJson(dataS, JsonObject.class);
			if (data.get("latest") == null) {
				return false;
			}
			Utils.replaceFileContents(versionManifest, dataS);

		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("local version manifest update failed. GameInstallatioNmanager/128");
			return false;
		}
		return true;
	}

	private static String latestRelease;
	private static boolean processLocalFile() {
		try {
			String dataS = Utils.readEverythingFromFile(versionManifest);
			manifest = gson.fromJson(dataS, JsonObject.class);
			if (manifest.get("latest") == null) {
				return false;
			}

			latestRelease = manifest.get("latest").getAsJsonObject().get("release").getAsString();
			//latestSnapshot = manifest.get("latest").getAsJsonObject().get("snapshot").getAsString();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static JsonObject getVersionFromManifest(String version) {
		if(manifest == null) {
			processLocalFile();
		}
		JsonArray versions = manifest.get("versions").getAsJsonArray();
		for (int i = 0; i < versions.size(); i++) {
			JsonObject ver = versions.get(i).getAsJsonObject();
			if (!ver.get("id").getAsString().equals(version)) {
				continue;
			}
			return ver;
		}
		return null;
	}

	public static JsonObject downloadVersionManifest(String version) {
		JsonObject manVer = getVersionFromManifest(version);
		if (manVer == null) {
			System.out.println("getVersionFromManifest/"+version+" NULL");
			return null;
		}
		try {
			String versionStr = Utils.readEverythingFromURL(manVer.get("url").getAsString());
			JsonObject ver = gson.fromJson(versionStr, JsonObject.class);
			//if (ver.get("arguments") == null) {
			if(ver.get("mainClass") == null) {
				//System.out.println("downloadVersionManifest/"+version+" ARGUMENTS NULL");
				//System.out.println(ver);
				return null;
			}
			Utils.replaceFileContents(new File(versionsDir, version + ".json"), versionStr);
			
			return ver;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static GameInstallation getByName(String name) {
		if(name == null) {
			return null;
		}
		for(GameInstallation ga : getAll()) {
			if(ga.getName().equals(name)) {
				return ga;
			}
		}
		return null;
	}
	
	public static List<GameInstallation> getAll() {
		return installations;
	}

	public static JsonObject getVersionsManifest() {
		return manifest;
	}

	public static String getLatestRelease() {
		return latestRelease;
	}
}
