package me.catzy44.user;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;

import me.catzy44.tools.Config;

public class UserProfileManager {
	private static List<UserProfile> profiles = new ArrayList<UserProfile>();
	private static UserProfile activeProfile; 
	
	public static void init() {
		if(Config.getConfig().get("profiles") != null) {
			JsonArray list = Config.getConfig().get("profiles").getAsJsonArray();
			for(int i = 0; i < list.size(); i++) {
				UserProfile up = new UserProfile(list.get(i).getAsJsonObject());
				profiles.add(up);
				if(up.isActive()) {
					activeProfile = up;
				}
			}
			if(activeProfile == null && profiles.size() > 0) {
				activeProfile = profiles.get(0);
			}
		}
	}
	
	public static void save() {
		JsonArray arr = new JsonArray();
		for(UserProfile p : profiles) {
			arr.add(p.toJson());
		}
		Config.getConfig().add("profiles", arr);
		Config.save();
	}

	public static List<UserProfile> getProfiles() {
		return profiles;
	}

	public static UserProfile getActiveProfile() {
		return activeProfile;
	}

	public static void setActiveProfile(UserProfile activeProfile) {
		UserProfileManager.activeProfile = activeProfile;
	}

}
