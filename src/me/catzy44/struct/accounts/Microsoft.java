package me.catzy44.struct.accounts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import me.catzy44.Constants;
import me.catzy44.EndiLauncher;
import me.catzy44.struct.accounts.MinecraftProfile.Type;
import me.catzy44.utils.IExploreUtil;
import me.catzy44.utils.JsonUtility;
import me.catzy44.utils.JsonUtility.ReqType;

public class Microsoft {
	private Gson gson = new Gson();
	String redUrl = "http://localhost:22420/login";
	
	private boolean gamePurchased = false;
	
	public boolean isGamePurchased() {
		return gamePurchased;
	}

	public String getMcAccessToken() {
		return mcAccessToken;
	}
	
	public enum Status {
		WAITING_FOR_USER_TO_LOGIN("Oczekiwanie"),
		DOWNLOADING_ACCOUNT_DATA("Uwierzytelnianie"),
		REFRESHING_THE_TOKEN("Odświeżanie tokenu"),
		AUTHENTICATING_XBOX_LIVE("Łączenie z Xbox Live"),
		AUTHENTICATING_XBOX_XSTS("Uwierzytelnianie Xbox Live"),
		AUTHENTICATING_MINECRAFT("Uwierzytelniania Mojang"),
		CHECKING_PURCHASE_STATUS("Sprawdzanie statusu zakupu"),
		DOWNLOADING_PLAYER_DATA("Pobieranie danych gracza"),
		SUCCESS("Sukces."),
		FAILED("Niepowodzenie.");
		
		private String x = "";
		Status(String s) {
			this.x = s;
		}
		public String getMessage() {
			return x;
		}
	}
	private Status status = Status.WAITING_FOR_USER_TO_LOGIN;
	
	public Microsoft() {
		pcs.addPropertyChangeListener("status",new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				status = (Status) evt.getNewValue();
			}
		});
	}

	public MinecraftProfile getAk() {
		return ak;
	}

	private MinecraftProfile ak;

	public MinecraftProfile login() {
		try {
			pcs.firePropertyChange("status", status, Status.WAITING_FOR_USER_TO_LOGIN);
			stage1();
			pcs.firePropertyChange("status", status, Status.DOWNLOADING_ACCOUNT_DATA);
			stage2();
			pcs.firePropertyChange("status", status, Status.REFRESHING_THE_TOKEN);
			refreshToken();
			pcs.firePropertyChange("status", status, Status.AUTHENTICATING_XBOX_LIVE);
			stage3();
			pcs.firePropertyChange("status", status, Status.AUTHENTICATING_XBOX_XSTS);
			stage4();
			pcs.firePropertyChange("status", status, Status.AUTHENTICATING_MINECRAFT);
			stage5();
			pcs.firePropertyChange("status", status, Status.CHECKING_PURCHASE_STATUS);
			boolean gamePurchased = checkIfOwnsTheGame();
			if(gamePurchased) {
				pcs.firePropertyChange("status", status, Status.DOWNLOADING_PLAYER_DATA);
				stage6();
			}
			ak =  new MinecraftProfile(name,uuid,mcAccessToken, Type.MICROSOFT);
			pcs.firePropertyChange("status", status, Status.SUCCESS);
			return ak;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pcs.firePropertyChange("status", status, Status.FAILED);
		
		ak =  new MinecraftProfile("nick","uuid",mcAccessToken,Type.MICROSOFT,mcAccessToken != null ? MinecraftProfile.Status.LOGGED_IN : MinecraftProfile.Status.UNKNW_ERR);
		return ak;
	}

	private boolean checkIfOwnsTheGame() throws InterruptedException, IOException, URISyntaxException {
		String res = JsonUtility.httpRequest("https://api.minecraftservices.com/entitlements/mcstore", ReqType.GET, null, mcAccessToken);
		JsonObject jo = gson.fromJson(res, JsonObject.class);
		if (jo.get("error") != null) {
			if (jo.get("error").getAsString().equals("Bad Request")) {
				return true;
			}
			return true;
		}
		JsonArray items = jo.get("items").getAsJsonArray();
		if (items.size() > 0) {
			return true;
		}
		return false;
	}
	
	private String uuid;
	private String name;
	
	private void stage6() throws InterruptedException, IOException, URISyntaxException {

		String res = JsonUtility.httpRequest("https://api.minecraftservices.com/minecraft/profile", ReqType.GET, null, mcAccessToken);
		JsonObject jo = gson.fromJson(res, JsonObject.class);
		if (jo.get("errorMessage") != null) {
			return;
		}
		uuid = jo.get("id").getAsString();
		name = jo.get("name").getAsString();
		//TODO download sking
	}

	private String mcAccessToken;

	private void stage5() throws Exception {
		JsonObject b = new JsonObject();
		b.addProperty("identityToken", "XBL3.0 x=" + userHash + ";" + xstsToken);

		String res = JsonUtility.httpRequest("https://api.minecraftservices.com/authentication/login_with_xbox", b.toString());
		if(res == null) {
			throw new Exception("returned_null");
		}
		JsonObject jo = gson.fromJson(res, JsonObject.class);
		if(jo.get("error") != null) {
			throw new Exception(jo.get("error").getAsString());
		}

		mcAccessToken = jo.get("access_token").getAsString();
	}

	private String xstsToken;

	private void stage4() throws Exception {
		JsonObject b = new JsonObject();
		JsonObject p = new JsonObject();
		p.addProperty("SandboxId", "RETAIL");
		JsonArray arr = new JsonArray();
		arr.add(xblToken);
		p.add("UserTokens", arr);

		b.add("Properties", p);
		b.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
		b.addProperty("TokenType", "JWT");

		String res = JsonUtility.httpRequest("https://xsts.auth.xboxlive.com/xsts/authorize", b.toString());
		if(res == null) {
			throw new Exception("returned_null");
		}
		JsonObject jo = gson.fromJson(res, JsonObject.class);
		if(jo.get("error") != null) {
			throw new Exception(jo.get("error").getAsString());
		}

		xstsToken = jo.get("Token").getAsString();
	}

	private String xblToken;
	private String userHash;

	private void stage3() throws Exception {
		JsonObject b = new JsonObject();
		JsonObject p = new JsonObject();
		p.addProperty("AuthMethod", "RPS");
		p.addProperty("SiteName", "user.auth.xboxlive.com");
		p.addProperty("RpsTicket", "d=" + accessToken);

		b.add("Properties", p);
		b.addProperty("RelyingParty", "http://auth.xboxlive.com");
		b.addProperty("TokenType", "JWT");

		String res = JsonUtility.httpRequest("https://user.auth.xboxlive.com/user/authenticate", b.toString());
		if(res == null) {
			throw new Exception("returned_null");
		}
		JsonObject jo = gson.fromJson(res, JsonObject.class);
		if(jo.get("error") != null) {
			throw new Exception(jo.get("error").getAsString());
		}

		xblToken = jo.get("Token").getAsString();
		userHash = jo.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString();
	}

	private void refreshToken() throws Exception {
		Map<String, String> q = new HashMap<String, String>();

		q.put("client_id", Constants.MICROSOFT_CLIENT_ID);
		q.put("client_secret", Constants.MICROSOFT_SECRET);
		q.put("refresh_token", refreshToken);
		q.put("grant_type", "refresh_token");
		q.put("redirect_uri", redUrl);
		JsonObject jo = gson.fromJson(JsonUtility.sendPOSTRequest("https://login.live.com/oauth20_token.srf", q), JsonObject.class);
		if(jo.get("error") != null) {
			throw new Exception(jo.get("error").getAsString());
		}

		accessToken = jo.get("access_token").getAsString();
	}

	String accessToken;
	String refreshToken;
	int expires = 0;

	private void stage2() throws Exception {
		Map<String, String> q = new HashMap<String, String>();

		q.put("client_id", Constants.MICROSOFT_CLIENT_ID);
		q.put("client_secret", Constants.MICROSOFT_SECRET);
		q.put("code", code);
		q.put("grant_type", "authorization_code");
		q.put("redirect_uri", redUrl);
		JsonObject jo = gson.fromJson(JsonUtility.sendPOSTRequest("https://login.live.com/oauth20_token.srf", q), JsonObject.class);
		if(jo.get("error") != null) {
			throw new Exception(jo.get("error").getAsString());
		}

		accessToken = jo.get("access_token").getAsString();
		refreshToken = jo.get("refresh_token").getAsString();
		expires = jo.get("expires_in").getAsInt();
	}

	public String getStage1Str() {
		return stage1Str;
	}

	private String stage1Str;

	private void stage1() throws Exception {
		startServer();
		Thread.sleep(1000);
		stage1Str = "https://login.live.com/oauth20_authorize.srf" + 
				"?client_id=" + 
				Constants.MICROSOFT_CLIENT_ID + 
				"&response_type=code" +
				"&redirect_uri=" +
				redUrl + 
				"&scope=XboxLive.signin%20offline_access" + 
				"&state=123456" + 
				"&prompt=select_account";
		//Utils.openUrl(stage1Str);
		//IExploreUtil.start(stage1Str, true, false, true, true, 440, 338+140,true);
		IExploreUtil.start(stage1Str);

		while (code == null && !Thread.interrupted()) {
			synchronized (server) {
				server.wait(2000);
			}
		}

		IExploreUtil.stop();
		if(code == null) {
			throw new Exception("Exception during microsoft login");
		}
		code = code.split("\\.")[2];
		if(code == null) {
			throw new Exception("Exception during microsoft login");
		}
	}

	private ServerSocket serverConnect;
	private Thread server;
	private String code = null;
	
	private boolean serverRunning = false;
	private long lastServerAccess = -1;
	private Thread serverKiller;
	
	public void stopServer() {
		
			serverRunning = false;
			if (server != null) {
				synchronized (server) {
					server.notifyAll();
				}
			}
			try {server.interrupt();} catch (Exception e) {}
			try {serverConnect.close();} catch (Exception e) {}
			server = null;
			serverConnect = null;
			try {serverKiller.interrupt();} catch (Exception e) {}
			serverKiller = null;
			//System.out.println("[server] stopped!");
	}

	public void startServer() {
		lastServerAccess = System.currentTimeMillis();
		if(serverRunning) {
			//System.out.println("[server] server already running!");
			return;
		}
		serverRunning = true;
		//System.out.println("[server] started!");
		serverKiller = new Thread(new Runnable() {
			@Override
			public void run() {
				while(!Thread.interrupted() && serverRunning) {
					if(System.currentTimeMillis()-lastServerAccess > 10000) {
						if(code == null) {
							lastServerAccess = System.currentTimeMillis();
							continue;
						}
						stopServer();
						break;
					}
				}
			}
		});
		serverKiller.start();
		server = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					serverRunning = true;
					lastServerAccess = System.currentTimeMillis();
					
					serverConnect = new ServerSocket(22420);

					while (!Thread.interrupted()) {
						Socket socket = serverConnect.accept();
						lastServerAccess = System.currentTimeMillis();

						new Thread(new Runnable() {
							@Override
							public void run() {
								BufferedReader in = null;
								PrintWriter pw = null;
								BufferedOutputStream bos = null;
								try {
									in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
									pw = new PrintWriter(socket.getOutputStream());// for headers
									bos = new BufferedOutputStream(socket.getOutputStream());// for data

									String input = in.readLine();
									if (input == null) {
										return;
									}
									StringTokenizer parse = new StringTokenizer(input);
									String method = parse.nextToken().toUpperCase(); // HTTP method
									String path = parse.nextToken().toLowerCase(); // file
									String fileRequested = path.split("\\?")[0];
									
									int retCode = 404;
									byte[] response = null;
									String redirect = null;
									
									if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
										// throwError(pw, bos, "501 Not Implemented");
									}

									x:
									if (fileRequested.equals("/login")) {
										Map<String, List<String>> x = splitQuery(path.replaceAll("\\/.*\\?", ""));
										// String state = x.get("state").get(0);
										code = x.get("code").get(0);
										retCode = 307;
										redirect = "http://localhost:22420/success";
										response = "Pomyślnie zalogowano".getBytes(Charset.forName("UTF-8"));
									} else {
										if(fileRequested.contains("..")) {
											retCode = 404;
											response = new byte[0];
											break x;
										}
										InputStream is = EndiLauncher.class.getResourceAsStream("/files/web"+fileRequested);
										if(is == null) {
											is = EndiLauncher.class.getResourceAsStream("/files/web" + fileRequested + ".html");
											if (is == null) {
												retCode = 404;
												response = new byte[0];
												break x;
											}
										}
										retCode = 200;
										response = is.readAllBytes();
									}
									
									
									int fileLength = response.length;
									pw.println("HTTP/1.1 " + retCode + " OK");
									pw.println("Server: EndiLauncher ms callback server");
									pw.println("Date: " + new Date());
									pw.println("Content-type: " + "text/html");// or text/html text/plain application/json
									pw.println("Content-length: " + fileLength);
									if(redirect != null) {
										pw.println("Location: "+redirect);
									}
									pw.println();
									pw.flush();

									bos.write(response, 0, fileLength);
									bos.flush();
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									try {
										in.close();
									} catch (Exception ex) {
									}
									try {
										pw.close();
									} catch (Exception ex) {
									}
									try {
										bos.close();
									} catch (Exception ex) {
									}
									try {
										socket.close();
									} catch (Exception ex) {
									}
									in = null;
									pw = null;
									bos = null;
								}
							}
						}).start();
					}
				} catch (IOException e) {
					stopServer();
					//e.printStackTrace();
				}
			}
		});
		server.start();
	}

	public Map<String, List<String>> splitQuery(String query) throws UnsupportedEncodingException {
		final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		final String[] pairs = query.split("&");
		for (String pair : pairs) {
			final int idx = pair.indexOf("=");
			final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
			if (!query_pairs.containsKey(key)) {
				query_pairs.put(key, new LinkedList<String>());
			}
			final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
			query_pairs.get(key).add(value);
		}
		return query_pairs;
	}
	
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addObserver(String p, PropertyChangeListener l) {
		pcs.addPropertyChangeListener(p, l);
	}

	public String toString() {
		return "Microsoft";
	};
}
