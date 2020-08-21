package me.catzy44.tools.net;

import java.io.IOException;

import com.google.gson.JsonObject;

import me.catzy44.EndiLauncher;
import me.catzy44.tools.Config;
import me.catzy44.utils.JsonUtility;
import me.catzy44.utils.JsonUtility.ReqType;

public class EndiAPI {
	
	private static Thread pinger;
	public static void startPinger() {
		/*pinger = new Thread(()->{
			try {
				String uuid = Config.getConfig().get("uuid").getAsString();
				while (!Thread.interrupted()) {
					JsonUtility.httpRequest("https://endimc.pl/api/launcher/ping",ReqType.POST, "uuid=" + uuid);
					Thread.sleep(10 * 60 * 1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		pinger.start();*/
		EndiAPIConnector api = new EndiAPIConnector();
		api.enable();
		
	}
	public static void main(String[] args) {
		try {
			Config.init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		EndiAPIConnector api = new EndiAPIConnector();
		api.enable();
		
		while(!api.isConnected()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		JsonObject obj = new JsonObject();
		obj.addProperty("cmd", "handshake");
		obj.addProperty("name", "EndiLauncher");
		obj.addProperty("version", EndiLauncher.version.get());
		obj.addProperty("uuid", Config.getConfig().get("uuid").getAsString());
		api.send("handshake", obj);
	}
	public static void log(String s) {
		System.out.println(s);
	}
}
