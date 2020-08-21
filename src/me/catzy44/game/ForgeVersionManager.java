package me.catzy44.game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import me.catzy44.EndiLauncher;
import me.catzy44.struct.ForgePromotedVersion;
import me.catzy44.utils.Utils;
import me.catzy44.utils.Version;

public class ForgeVersionManager {
	private static JsonObject versionManifest;
	private static JsonObject promosManifest;
	
	private static Gson gson = new Gson();
	
	private static File versionManifestF = new File(EndiLauncher.getWorkingdir(), "/launcher/forge-versions.json");
	private static File promosManifestF = new File(EndiLauncher.getWorkingdir(), "/launcher/forge-promos.json");
	
	private static Map<String,List<Version>> versions = new HashMap<String,List<Version>>();
	private static Map<String,ForgePromotedVersion> promosArr = new HashMap<String,ForgePromotedVersion>();
	
	public static List<Version> getForgesListForMc(String mcver) {
		if(!versions.containsKey(mcver)) {
			return null;
		}
		List<Version> vers = versions.get(mcver);
		Collections.reverse(vers);
		return vers;
	}
	
	public static String[] getForgesForMc(String mcver) {
		if(!versions.containsKey(mcver)) {
			System.err.println("getForgesForMc failed on "+mcver);
			return null;
		}
		return versionsListToStrArr(versions.get(mcver));
	}
	
	public static String[] versionsListToStrArr(List<Version> versions) {
		String[] arr = new String[versions.size()];
		for(int i = 0 ; i < versions.size(); i++) {
			arr[i] = versions.get(i).get();
		}
		return arr;
	}
	
	public static ForgePromotedVersion getPromotedVersion(String gameVersion) {
		if(!promosArr.containsKey(gameVersion)) {
			return null;
		}
		return promosArr.get(gameVersion);
	}
	
	public static void loadUI() {
		
	}
	
	public static void loadVersions() {
		try {
			versionManifest = gson.fromJson(Utils.readEverythingFromFile(versionManifestF), JsonObject.class);
			
			for(String gameVer : versionManifest.keySet()) {
				JsonArray vers = versionManifest.get(gameVer).getAsJsonArray();
				
				List<Version> list = new ArrayList<Version>();
				for (int i = 0; i < vers.size(); i++) {
					String forge = vers.get(i).getAsString().split("-")[1];
					list.add(new Version(forge));
				}
				versions.put(gameVer, list);
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void loadPromos() {
		try {
			promosManifest = gson.fromJson(Utils.readEverythingFromFile(promosManifestF), JsonObject.class);
			
			JsonObject promos = promosManifest.get("promos").getAsJsonObject();
			for(String left : promos.keySet()) {
				String[] gameVerS = left.split("-");
				
				String forgeVersion = promos.get(left).getAsString();
				String realGameVer = gameVerS[0];
				String type = gameVerS[1];
				
				boolean found = false;
				for(String p : promosArr.keySet()) {
					if(p.equals(realGameVer)) {
						promosArr.get(p).set(forgeVersion, type);
						found = true;
						break;
					}
				}
				if(!found) {
					promosArr.put(realGameVer,new ForgePromotedVersion(forgeVersion,type));
				}
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean updateLocalVersionsFile() {
		try {
			String dataS = Utils.readEverythingFromURL("https://files.minecraftforge.net/net/minecraftforge/forge/maven-metadata.json");
			//JsonObject data = gson.fromJson(dataS, JsonObject.class);
			Utils.replaceFileContents(versionManifestF, dataS);

		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("local forge manifest update failed. ForgeVersionManager/130");
			return false;
		}
		return true;
	}
	
	public static boolean updateLocalPromosFile() {
		try {
			String dataS = Utils.readEverythingFromURL("https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json");
			/*JsonObject data = gson.fromJson(dataS, JsonObject.class);
			if (data.get("latest") == null) {
				return false;
			}*/
			Utils.replaceFileContents(promosManifestF, dataS);

		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("local forge promos update failed. ForgeVersionManager/147");
			return false;
		}
		return true;
	}
}