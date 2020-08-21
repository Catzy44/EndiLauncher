package me.catzy44.struct;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import me.catzy44.EndiLauncher;
import me.catzy44.Interface;
import me.catzy44.skin.SkinPreview;
import me.catzy44.struct.accounts.Mojang;
import me.catzy44.struct.accounts.MinecraftProfile.Type;
import me.catzy44.tools.Download;
import me.catzy44.user.UserProfile;
import me.catzy44.user.UserProfileManager;
import me.catzy44.utils.JsonUtility;
import me.catzy44.utils.JsonUtility.ReqType;
import me.catzy44.utils.Utils;

import java.awt.image.BufferedImage;

/**
 * The Class PlayerSkin.
 */
public class PlayerSkin {
	
	/** The file. */
	private File file = null;
	
	/** The skin ID. */
	private String skinID;
	
	/** The up. */
	private UserProfile up;

	/** The gson. */
	private static Gson gson = new Gson();

	/**
	 * Instantiates a new player skin.
	 *
	 * @param up the up
	 * @param skinID the skin ID
	 */
	public PlayerSkin(UserProfile up, String skinID) {
		this.up = up;
		this.skinID = skinID;  
		this.file = new File(EndiLauncher.workingDir, "skins/" + skinID + ".png");
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Gets the skin ID.
	 *
	 * @return the skin ID
	 */
	public String getSkinID() {
		return skinID;
	}

	public void setSkinID(String skinID) {
		this.skinID = skinID;
		this.file = new File(EndiLauncher.workingDir, "skins/" + skinID + ".png");
	}

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public BufferedImage getImage() {
		try {
			//System.out.println(up.getAk().getType().toString());
			if(up.getAk().getType() == Type.NONPREMIUM) {
				return ImageIO.read(Interface.class.getResourceAsStream("/files/images/skins/steve.png"));
			}
			if(up.getAk().getAccessToken() == null || file == null || !file.exists()) {
				return ImageIO.read(Interface.class.getResourceAsStream("/files/images/skins/steve.png"));
			}
			return ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Change player skin.
	 *
	 * @param f local file to upload
	 * @throws IOException 
	 */
	public void change(File f) throws IOException {
		Mojang.uploadToMojang(f, up.getAk().getAccessToken(), up.getAk().getUuid());
	}
	
	public void showSkin() {
		if (UserProfileManager.getActiveProfile() == up) {
			SkinPreview.loadSkinPreview(getImage());
		}
	}
	
	/**
	 * Refresh async.
	 */
	public void refreshAsync() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!refresh()) {
					return;
				}
				showSkin();
			}
		}).start();
	}

	/**
	 * Refresh.
	 *
	 * @return true, if successful
	 */
	public boolean refresh() {
		try {
			if(up.getAk().getType() == Type.NONPREMIUM || !up.getAk().isLogged()) {
				return true;
			}
			String accessToken = up.getAk().getAccessToken();
			if (accessToken == null || accessToken.isEmpty()) {
				return false;
			}
			String data = JsonUtility.httpRequest("https://api.minecraftservices.com/minecraft/profile", ReqType.GET, null, accessToken);
			if (data == null || data.isEmpty()) {
				return false;
			}
			
			JsonObject json = gson.fromJson(data, JsonObject.class);
			if(json == null) {
				return false;
			}
			if (json.get("error") != null) {
				return false;
			}
			JsonArray skins = json.get("skins").getAsJsonArray();
			JsonObject skin = skins.get(0).getAsJsonObject();

			String skinURL = skin.get("url").getAsString();
			skinID = skin.get("id").getAsString();

			file = new File(EndiLauncher.workingDir, "skins/" + skinID + ".png");

			Utils.mkDirs(file.getParentFile());
			
			Download down = new Download(new URL(skinURL), file.getAbsolutePath());
			
			while(down.getStatus() == Download.DOWNLOADING) {
				down.waitt(5000);
			}
			
			return true; 

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
