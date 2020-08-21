package me.catzy44.struct;

import com.google.gson.JsonObject;

public class GameVersion {
	public enum ModApi {
		FORGE("forge"),
		FABRIC("fabric"),
		NULL("null");
		
		ModApi(String s) {
			this.s = s;
		}
		private String s;
		public String getString() {
			return s;
		}
	}
	
	private String version = null;
	private String forgeVersion = null;
	private String fabricVersion = null;
	private String assetsVersion = null;

	public GameVersion(String version) {
		this.version = version;
	}

	public GameVersion(String version, String forgeVersion, String fabricVersion) {
		this.version = version;
		this.forgeVersion = forgeVersion;
		this.fabricVersion = fabricVersion;
	}
	
	public GameVersion(JsonObject json) {
		version = json.get("version") == null ? null : json.get("version").getAsString();
		forgeVersion = json.get("forge") == null ? null : json.get("forge").getAsString();
		fabricVersion = json.get("fabric") == null ? null : json.get("fabric").getAsString();
		assetsVersion = json.get("assets") == null ? null : json.get("assets").getAsString();
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("version", version);
		json.addProperty("forge", forgeVersion);
		json.addProperty("fabric", fabricVersion);
		json.addProperty("assets", assetsVersion);
		return json;
	}
	
	public String getProfileName() {
		if(isForge()) {
			return version+"-forge-"+forgeVersion;
		} else if(isFabric()) {
			return "fabric-loader-"+fabricVersion+"-"+version;
		}
		return version;
	}
	
	public void setModded(ModApi api) {
		setModded(api,null);
	}
	public void setModded(ModApi api, String version) {
		if(api == ModApi.FORGE) {
			setFabricVersion(null);
			setForgeVersion(version);
		} else if(api == ModApi.FABRIC) {
			setFabricVersion("true");
			setForgeVersion(null);
		} else {
			setFabricVersion(null);
			setForgeVersion(null);
		}
	}
	public ModApi getModded() {
		if(isForge()) {
			return ModApi.FORGE;
		} else if(isFabric()) {
			return ModApi.FABRIC;
		} else {
			return ModApi.NULL;
		}
	}

	public String getMcVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getForgeVersion() {
		return forgeVersion;
	}

	public void setForgeVersion(String forgeVersion) {
		this.forgeVersion = forgeVersion;
	}

	public String getFabricVersion() {
		return fabricVersion;
	}

	public void setFabricVersion(String fabricVersion) {
		this.fabricVersion = fabricVersion;
	}

	public String getAssetsVersion() {
		return assetsVersion;
	}

	public void setAssetsVersion(String assetsVersion) {
		this.assetsVersion = assetsVersion;
	}

	public boolean isForge() {
		return forgeVersion != null;
	}
	public boolean isFabric() {
		return fabricVersion != null;
	}
	
	public boolean isModded() {
		return isForge() || isFabric();
	}
}
