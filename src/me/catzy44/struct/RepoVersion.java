package me.catzy44.struct;

import com.google.gson.JsonObject;

import me.catzy44.utils.Version;

public class RepoVersion {
	private Version ver;
	private JsonObject json;
	private String mcVer;

	public Version getVer() {
		return ver;
	}

	public void setVer(Version ver) {
		this.ver = ver;
	}

	public JsonObject getJson() {
		return json;
	}

	public void setJson(JsonObject json) {
		this.json = json;
	}

	public RepoVersion(Version ver, JsonObject json, String mcVer) {
		this.ver = ver;
		this.json = json;
		this.mcVer = mcVer;
	}

}