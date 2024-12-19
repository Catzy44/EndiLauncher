package me.catzy44.user;

import com.google.gson.JsonObject;

import me.catzy44.Interface;
import me.catzy44.struct.PlayerSkin;
import me.catzy44.struct.accounts.MinecraftProfile;

public class UserProfile {
	private MinecraftProfile ak;
	private String selectedInstallation;
	private boolean isActive = false;
	private PlayerSkin skin;

	public UserProfile(String profileName,MinecraftProfile ak, String selectedInstallation) {
		this.ak = ak;
		this.selectedInstallation = selectedInstallation;
		skin = new PlayerSkin(this,null);
		skin.refreshAsync();
	}
	
	public UserProfile(JsonObject obj) {
		this.ak = new MinecraftProfile(obj.get("ak").getAsJsonObject());
		this.selectedInstallation = obj.get("selectedInstall") == null ? null : obj.get("selectedInstall").getAsString();
		this.isActive = obj.get("active").getAsBoolean();
		skin = new PlayerSkin(this,obj.get("skinID").getAsString());
		skin.refreshAsync();
	}
	
	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.add("ak", ak.toJson());
		obj.addProperty("selectedInstall", selectedInstallation);
		obj.addProperty("active", isActive);
		obj.addProperty("skinID", skin.getSkinID() == null ? "" : skin.getSkinID());
		return obj;
	}

	public String getProfileName() {
		return ak.getNickname() != null ? ak.getNickname() : "??";
	}

	public MinecraftProfile getAk() {
		return ak;
	}

	public void setAk(MinecraftProfile ak) {
		this.ak = ak;
	}

	public String getSelectedInstallation() {
		return selectedInstallation;
	}

	public void setSelectedInstallation(String selectedInstallation) {
		this.selectedInstallation = selectedInstallation;
	}

	public boolean isActive() {
		return isActive;
	}

	public PlayerSkin getSkin() {
		return skin;
	}
	
	public void changeName(String s) {
		ak.setNickname(s);
	}

	public void resetSkin() {
		skin.setSkinID(null);
	}

}
