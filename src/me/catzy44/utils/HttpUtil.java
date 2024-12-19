package me.catzy44.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HttpUtil {
	
	public enum ReqType {
		GET, POST, PUT
	}
	
	public enum ContentType {
		UNKNW(null),JSON("application/json");
		private String type;
		ContentType(String s) {
			this.type = s;
		}
		@Override
		public String toString() {
			return type;
		}
	}
	
	public enum ReqStatus {
		SUCCESS,ERROR,EMPTY
	}
	
	private Gson gson = new Gson();
	
	private String url = null;
	private ReqType type = ReqType.GET;
	private ContentType contentType = null;
	private ContentType acceptType = null;
	
	private String body = null;
	
	private String authKey = "Bearer";
	private String authVal = null;
	
	private ReqStatus status = ReqStatus.EMPTY;
	private int responseCode = -1;
	
	private String response = "";
	
	public HttpUtil(String url, ReqType type) {
		this.url = url;
		this.type = type;
	}
	
	public void setAuth(String key, String val) {
		if(key != null) {
			authKey = key;
		}
		authVal = val;
	}
	
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public void setAcceptType(ContentType acceptType) {
		this.acceptType = acceptType;
	}

	public void setRequestBody(String body) {
		this.body = body;
	}
	
	public void setRequestBody(JsonObject json) {
		this.body = gson.toJson(json);
		this.contentType = ContentType.JSON;
	}
	
	public String getResponseBody() {
		return response;
	}
	
	public JsonElement getResponseBodyAsJson() {
		if(response == null || response.isEmpty()) {
			return null;
		}
		return gson.fromJson(response, JsonElement.class);
	}
	
	public int getResponseStatusCode() {
		return responseCode;
	}
	
	public ReqStatus getResponseStatus() {
		return status;
	}
	
	private Exception ex;
	
	public Exception getError() {
		return ex;
	}
	
	public ReqStatus send() {
		try {
			
			HttpsURLConnection	connection = (HttpsURLConnection)new URL(url).openConnection();
			
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			
			if(acceptType != null && acceptType != ContentType.UNKNW)
			connection.setRequestProperty("Accept", acceptType.toString());
			
			if(authVal != null) {
				connection.setRequestProperty("Authorization", authKey+" " + authVal);
			}
			((HttpURLConnection) connection).setRequestMethod(type.toString());

			if(body != null && body.length() > 0) {
				if(contentType != null && contentType != ContentType.UNKNW)
					connection.setRequestProperty("Content-Type", contentType.toString());
				
				byte[] contentBytes = body == null ? null : body.getBytes("UTF-8");
				int contentLength = body == null ? 0 : contentBytes.length;
				
				connection.setRequestProperty("Content-Length", Integer.toString(contentLength));
				
				OutputStream requestStream = connection.getOutputStream();
				requestStream.write(contentBytes, 0, contentLength);
				requestStream.close();
			}

			if (((HttpURLConnection) connection).getResponseCode() == 200) {
				response = readEntireInputStream(connection.getInputStream());
			} else {
				response = readEntireInputStream(((HttpURLConnection) connection).getErrorStream());
			}

			responseCode = ((HttpURLConnection) connection).getResponseCode();
			status = ReqStatus.SUCCESS;
			
			connection.disconnect();
		} catch (Exception e) {
			//System.out.println("Nieudany request: "+url);
			//System.out.println(e.getMessage());
			ex = e;
			status = ReqStatus.ERROR;
		}
		return status;
	}
	
	private String readEntireInputStream(InputStream is) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		for (int result = bis.read(); result != -1; result = bis.read()) {
		    buf.write((byte) result);
		}
		// StandardCharsets.UTF_8.name() > JDK 7
		return buf.toString("UTF-8");
	}
}
