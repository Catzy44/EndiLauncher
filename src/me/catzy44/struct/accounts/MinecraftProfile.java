package me.catzy44.struct.accounts;

import java.util.UUID;

import com.google.gson.JsonObject;

public class MinecraftProfile {

	private String nickname;
	private String uuid;
	private String accessToken;
	private Type type = Type.NONPREMIUM;
	private Status status = Status.UNKNW;

	public enum Type {
		NONPREMIUM, MICROSOFT, MOJANG
	}
	public enum Status {
		UNKNW,LOGGED_IN,INVALID_CREDITIENALS,UNKNW_ERR,NETWORK_ERR
	}
	
	public boolean isLogged() {
		return status == Status.LOGGED_IN && accessToken != null && !accessToken.isEmpty();
	}
	
	public void logout() {
		uuid = "";
		accessToken = "";
		status = Status.UNKNW;
	}

	public MinecraftProfile(String nickname, String uuid, String accessToken, Type type) {
		this.nickname = nickname;
		this.uuid = uuid;
		this.accessToken = accessToken;
		this.type = type;
		if(accessToken != null && !accessToken.isEmpty()) {
			status = Status.LOGGED_IN;
		}
	}
	public MinecraftProfile(String nickname, String uuid, String accessToken, Type type, Status status) {
		this.nickname = nickname;
		this.uuid = uuid;
		this.accessToken = accessToken;
		this.type = type;
		this.status = status;
		if(accessToken != null && !accessToken.isEmpty()) {
			this.status = Status.LOGGED_IN;
		}
	}
	public MinecraftProfile(Status status) {
		this.status = status;
	}

	public MinecraftProfile(JsonObject obj) {
		this.nickname = obj.get("nickname") == null ? "" : obj.get("nickname").getAsString();
		this.uuid = obj.get("uuid") == null ? "" : obj.get("uuid").getAsString();
		this.accessToken = obj.get("accessToken") == null ? "" : obj.get("accessToken").getAsString();
		this.type = Type.valueOf(obj.get("type") == null ? "NONPREMIUM" : obj.get("type").getAsString());
		this.status = Status.valueOf(obj.get("status") == null ? "UNKNW" : obj.get("status").getAsString());
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("nickname", nickname);
		json.addProperty("uuid", uuid);
		json.addProperty("accessToken", accessToken);
		json.addProperty("type", type.name());
		json.addProperty("status", status.name());
		return json;
	}

	public String getNickname() {
		return nickname;
	}

	public String getUuid() {
		return uuid;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
		if(type == Type.NONPREMIUM) {
			uuid = UUID.randomUUID().toString();
		}
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Status getStatus() {
		return status;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
