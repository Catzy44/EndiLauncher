package me.catzy44.game;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import me.catzy44.Constants;
import me.catzy44.EndiLauncher;
import me.catzy44.Interface;
import me.catzy44.struct.RepoVersion;
import me.catzy44.tools.Download;
import me.catzy44.utils.HttpUtil;
import me.catzy44.utils.HttpUtil.ContentType;
import me.catzy44.utils.Utils;
import me.catzy44.utils.Version;

public class Sodium {
	private static Gson gson = new Gson();
	
	private static File localManifest = new File(EndiLauncher.workingDir,"launcher/sodium.json");
	public static void loadManifest() {
		try {
			if(localManifest.exists()) {
				String json = Utils.readEverythingFromFile(localManifest);
				arr = gson.fromJson(json, JsonArray.class);
				new Thread(()->{
					try {
						updateManifest();
						//loadManifest();
					} catch (Exception e) {
						System.out.println("manifest async update failed!");
					}
				}).start();
			} else {
				updateManifest();
				arr = gson.fromJson(Utils.readEverythingFromFile(localManifest), JsonArray.class);
			}
			
		} catch (JsonSyntaxException e) {
			try {
				//updateManifest();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			
		}
		
	}

	public static void updateManifest() throws Exception {
		HttpUtil http = new HttpUtil("https://api.github.com/repos/CaffeineMC/sodium-fabric/releases",HttpUtil.ReqType.GET);
		http.setAcceptType(ContentType.JSON);
		http.setAuth("token", Constants.CONST);
		http.send();
		if(http.getResponseStatus() != HttpUtil.ReqStatus.SUCCESS) {
			System.out.println("update sodium manifest failed with "+http.getResponseStatusCode());
			System.out.println(http.getResponseBody());
			http.getError().printStackTrace();
		}
		
		
		JsonElement element = http.getResponseBodyAsJson();
		if (!element.isJsonArray()) {
			if (!element.isJsonObject()) {
				// ??
			}
			JsonObject obj = element.getAsJsonObject();
			if (obj.get("message") != null) {
				System.out.println(obj.get("message").getAsString());
				throw new Exception("");
			}
		}
		if (!localManifest.exists()) {
			Utils.mkDirs(localManifest.getParentFile());
			localManifest.createNewFile();
		}
		Utils.replaceFileContents(localManifest, http.getResponseBody());
	}

	public static boolean isCompatible(String mcver) throws Exception {
		return getSodiumVersionForMc(mcver) != null;
	}
	
	static JsonArray arr = null;
	public static RepoVersion getSodiumVersionForMc(String mcVer) throws Exception {
		if(arr == null) {
			loadManifest();
		}
		RepoVersion latestSodium = null;
		for (int i = 0; i < arr.size(); i++) {
			JsonObject obj = arr.get(i).getAsJsonObject();
			String s[] = obj.get("tag_name").getAsString().split("-");// target_commitish

			// String mc = s[0].replace("mc", "");
			String mc = obj.get("target_commitish").getAsString().split("/")[0];
			if (!Version.checkCompatibility(mc, mcVer)) {
				continue;
			}
			RepoVersion sodium = new RepoVersion(new Version(s[1]), obj, mc);

			if (latestSodium == null || latestSodium.getVer().compareTo(sodium.getVer()) < 0) {
				latestSodium = sodium;
			}
		}
		
		return latestSodium;
	}
	public static void uninstallSodium(GameInstallation gi) {
		File mods = new File(gi.gameDir, "/mods/");
		for(File f : mods.listFiles()) {
			if(!f.isFile()) {
				continue;
			}
			if(f.getName().startsWith("endi-sodium") && f.getName().endsWith(".jar")) {
				f.delete();
			}
		}
	}
	public static void installSodium(GameInstallation gi) {
		try {
			RepoVersion latestSodium = getSodiumVersionForMc(gi.getVersion().getMcVersion());
			if(latestSodium == null) {
				Interface.printToConsole("Nie można zainstalować sodium: Niekompatybilna wersja gry.");
				return;
			}

			JsonObject data = latestSodium.getJson().get("assets").getAsJsonArray().get(0).getAsJsonObject();
			String url = data.get("browser_download_url").getAsString();
			File f = new File(gi.gameDir, "/mods/endi-" + data.get("name").getAsString());
			Utils.mkDirs(f.getParentFile());

			Download down = new Download(new URL(url), f.getAbsolutePath());

			down.addObserver("downloaded", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					gi.pcs.firePropertyChange("downloaded", e.getOldValue(), e.getNewValue());
				}
			});

			while (down.getStatus() == Download.DOWNLOADING) {
				down.waitt(10000);
			}

			if (down.getStatus() != Download.COMPLETE) {
				throw down.getException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
