package me.catzy44.struct.accounts;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Random;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import me.catzy44.struct.accounts.MinecraftProfile.Status;
import me.catzy44.struct.accounts.MinecraftProfile.Type;
import me.catzy44.utils.JsonUtility;

public class Mojang {

	private static Gson gson = new Gson();
	public static MinecraftProfile authenticate(String nick, String password) {
		try {
			JsonObject authjson1 = new JsonObject();
			authjson1.addProperty("name", "Minecraft");
			authjson1.addProperty("version", 1);
			
			JsonObject authjson = new JsonObject();
			authjson.add("agent", authjson1);
			authjson.addProperty("username",nick);
			authjson.addProperty("password", password);

			String res = JsonUtility.httpRequest("https://authserver.mojang.com/authenticate",authjson.toString());
			if(res == null) {
				return new MinecraftProfile(Status.NETWORK_ERR);
			}
			JsonObject authoutjson = gson.fromJson(res,JsonObject.class);
			if(authoutjson.get("errorMessage") != null) {
				if(authoutjson.get("errorMessage").getAsString().contains("Invalid credentials")) {
					return new MinecraftProfile(Status.INVALID_CREDITIENALS);
				}
				return new MinecraftProfile(Status.INVALID_CREDITIENALS);
			}
			String nickname = authoutjson.get("selectedProfile").getAsJsonObject().get("name").getAsString();
			String uuid = authoutjson.get("selectedProfile").getAsJsonObject().get("id").getAsString();
			String accessToken = authoutjson.get("accessToken").getAsString();
			
			return new MinecraftProfile(nickname,uuid,accessToken, Type.MOJANG, Status.LOGGED_IN);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new MinecraftProfile(Status.UNKNW_ERR);
	}

	public static String getUUID(String nick) {
		try {
			URL uuidurl = new URL("https://api.mojang.com/users/profiles/minecraft/"+nick);
			BufferedReader uuidin = new BufferedReader(new InputStreamReader(uuidurl.openStream()));

			String inputLine;
			String lines = "";
			while ((inputLine = uuidin.readLine()) != null) {
				lines = lines + inputLine;
			}
			uuidin.close();

			JsonObject uuidoutjson =  gson.fromJson(lines, JsonObject.class);
			return  uuidoutjson.get("id").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int uploadToMojang(File file, String accessToken, String uuid) throws IOException {
		String url = "https://api.mojang.com/user/profile/" + uuid + "/skin";
		String charset = "UTF-8";
		String boundary = Long.toHexString(System.currentTimeMillis());
		String CRLF = "\r\n";

		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("PUT");
		connection.setRequestProperty("Authorization", "Bearer " + accessToken);
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		try (OutputStream output = connection.getOutputStream();
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);) {
			// Send param.
			writer.append("--" + boundary).append(CRLF);
			writer.append("Content-Disposition: form-data; name=\"model\"").append(CRLF);
			// writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
			writer.append(CRLF).append("").append(CRLF).flush();

			// Send skin.
			writer.append("--" + boundary).append(CRLF);
			writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"")
					.append(CRLF);
			// writer.append("Content-Type: " +
			// URLConnection.guessContentTypeFromName(file.getName())).append(CRLF);
			writer.append("Content-Type: image/png").append(CRLF);
			// writer.append("Content-Transfer-Encoding: binary").append(CRLF);
			writer.append(CRLF).flush();
			Files.copy(file.toPath(), output);
			output.flush();
			writer.append(CRLF).flush();

			writer.append("--" + boundary + "--").append(CRLF).flush();
		}
		return ((HttpURLConnection) connection).getResponseCode();
	}

	public static String[] getTexture(String uuid) {
		try {
			URL texurl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			BufferedReader texin = new BufferedReader(new InputStreamReader(texurl.openStream()));

			String texInputLine = null;
			String texLines = "";
			while ((texInputLine = texin.readLine()) != null) {
				texLines = texLines + texInputLine;
			}
			texin.close();
			JsonObject texoutjson =  gson.fromJson(texLines,JsonObject.class);
			JsonObject texjsonproperties =  gson.fromJson(texoutjson.get("properties").toString().replace("[", "").replace("]", ""),JsonObject.class);
			String value =  texjsonproperties.get("value").getAsString();
			String signature =  texjsonproperties.get("signature").getAsString();
			return new String[]{value, signature};
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String[] createTexture(File file, String login, String pass) {
		MinecraftProfile ad = authenticate(login, pass);
		if (ad == null) {
			return null;
		}
		int responseCode = 0;
		try {
			responseCode = uploadToMojang(file, ad.getAccessToken(), ad.getUuid());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (responseCode != 204 && responseCode != 200) {
			System.out.println("error, Mojang returned: "+responseCode);
			return null;
		}
		return getTexture(ad.getUuid());
	}
	private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	public static String generateRandomString(int length){
        Random r = new Random();
        StringBuffer randStr = new StringBuffer();
        for(int i=0; i<length; i++){
            int number = r.nextInt(CHAR_LIST.length());
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }
}
