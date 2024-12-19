package me.catzy44.tools.net;

import com.google.gson.JsonObject;

public abstract class PacketHandler {
	private String cmdName;
	public  PacketHandler(String cmdName) {
		this.cmdName = cmdName;
	}
	public String getCmdName() {
		return cmdName;
	}
	public abstract void process(JsonObject obj, EndiAPIConnector conn);
	public abstract JsonObject reply(JsonObject obj, EndiAPIConnector conn);
}
