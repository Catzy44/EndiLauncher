package me.catzy44.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonUtility {
	private static Gson gson = new Gson();

	public enum ReqType {
		GET, POST, PUT
	}
	
	public static String httpRequest(String url, String content) {
		return httpRequest(url,content,null);
	}

	public static String httpRequest(String url, String content, String bearer) {
		return httpRequest(url, ReqType.POST, content, bearer);
	}
	
	public static String httpRequest(String url, ReqType type, String content) {
		return httpRequest(url, type, content, null);
	}

	public static String httpRequest(String url, ReqType type, String content, String bearer) {
		try {
			byte[] contentBytes = content == null ? null : content.getBytes("UTF-8");
			int contentLength = content == null ? 0 : contentBytes.length;
			
			HttpsURLConnection	connection = (HttpsURLConnection)new URL(url).openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Accept", "application/json");
			if(content != null && (content.startsWith("{") || content.startsWith("["))) {
				connection.setRequestProperty("Content-Type", "application/json");
			}
			connection.setRequestProperty("Content-Length", Integer.toString(contentLength));
			
			if(bearer != null) {
				connection.setRequestProperty("Authorization", "Bearer " + bearer);
			}
			((HttpURLConnection) connection).setRequestMethod("GET");

			if(contentLength > 0) {
				OutputStream requestStream = connection.getOutputStream();
				requestStream.write(contentBytes, 0, contentLength);
				requestStream.close();
			}

			String response = "";
			BufferedReader responseStream;
			if (((HttpURLConnection) connection).getResponseCode() == 200) {
				responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			} else {
				responseStream = new BufferedReader(new InputStreamReader(((HttpURLConnection) connection).getErrorStream(), "UTF-8"));
			}

			//response = responseStream.readLine();
			String line;
			while((line = responseStream.readLine()) != null) {
				response += line;
			}
			
			responseStream.close();

			if (((HttpURLConnection) connection).getResponseCode() != 200) {
				// Failed to login (Invalid Credentials or whatever)
			}

			return response;
		} catch (Exception e) {
			System.out.println("Nieudany request: "+url);
			System.out.println(e.getMessage());
		}
		return null;
	}
	public static JsonObject sendJSONRequest(String ad,String json) {
		try {
			URL url = new URL(ad);
			HttpsURLConnection http = (HttpsURLConnection)url.openConnection();
			http.setRequestMethod("POST"); // PUT is another valid option
			http.setDoOutput(true);

			byte[] out = json.getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			http.setFixedLengthStreamingMode(length);
			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			http.connect();
			try (OutputStream os = http.getOutputStream()) {
				os.write(out);
			}

			InputStream is = http.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader bf = new BufferedReader(isr);

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = bf.readLine()) != null) {
				sb.append(line);
			}

			String result = sb.toString();
			sb = null;

			bf.close();
			bf = null;
			isr.close();
			isr = null;
			is.close();
			is = null;

			JsonObject obj;
			synchronized(gson) {
				obj = (JsonObject) gson.fromJson(result,JsonObject.class);
			}
			return obj;
		} catch (Exception e) {
			System.out.println("Nieudany request: "+ad);
		}
		return null;
	}
	
	public static String sendPOSTRequest(String ad,Map<String,String> arguments) {
		try {
			StringJoiner sj = new StringJoiner("&");
			for(Map.Entry<String,String> entry : arguments.entrySet())
			    sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" 
			         + URLEncoder.encode(entry.getValue(), "UTF-8"));
			byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			//URLConnection connection = new URL(ad).openConnection();
			HttpsURLConnection connection = (HttpsURLConnection)new URL(ad).openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", Integer.toString(length));

			OutputStream requestStream = connection.getOutputStream();
			requestStream.write(out, 0, length);
			requestStream.close();

			String response = "";
			BufferedReader responseStream;
			if (((HttpURLConnection) connection).getResponseCode() == 200) {
				responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			} else {
				responseStream = new BufferedReader(new InputStreamReader(((HttpURLConnection) connection).getErrorStream(), "UTF-8"));
			}

			response = responseStream.readLine();
			responseStream.close();

			if (((HttpURLConnection) connection).getResponseCode() != 200) {
				// Failed to login (Invalid Credentials or whatever)
			}

			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String buildGetQuery(Map<String,String> map) {
		String x = "?";
		for(String s : map.keySet()) {
			String v = map.get(s);
			
			x += s+"="+v+"&";
		}
		return x.substring(0, x.length()-1);
	}
}
