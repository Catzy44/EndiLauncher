package me.catzy44.struct.accounts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;

import me.kotsu.microHTTPServer.HttpServer;
import me.kotsu.microHTTPServer.HttpServerAction;
import me.kotsu.microHTTPServer.HttpResponse;

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
        try {
            IExploreUtil.start(stage1Str);
            synchronized (callbackLock) {
                while (code == null && callbackError == null && server != null) callbackLock.wait();
                if (callbackError != null) throw new IOException(callbackError);
                if (code == null) throw new IOException("Microsoft login cancelled");
            }
        } finally {
            IExploreUtil.closeIfPossible();
            if (code == null) stopServer();
        }
    }

    private final Object callbackLock = new Object();
    private HttpServer server;
    private volatile String code;
    private String callbackError;
    private ScheduledExecutorService serverKiller;

    public void stopServer() {
        synchronized (callbackLock) {
            if (server != null) server.shutdown();
            server = null;
            if (serverKiller != null) serverKiller.shutdownNow();
            serverKiller = null;
            callbackLock.notifyAll();
        }
    }

    public void startServer() throws IOException {
        synchronized (callbackLock) {
            if (server != null) return;
            code = null;
            callbackError = null;
            HttpServer callbackServer = new HttpServer(22420);
            callbackServer.addAction(new HttpServerAction("/login") {
                @Override
                public Object run(String method, String ip, String target, Map<String, String> getParams, BufferedReader in) throws Exception {
                    if (!method.equals("GET")) return response("405 Method Not Allowed", "Expected GET");
                    String receivedCode = getParams.get("code");
                    String error = getParams.get("error");
                    if ((receivedCode == null || receivedCode.isEmpty()) && error == null) {
                        return response("400 Bad Request", "Missing authorization code");
                    }
                    synchronized (callbackLock) {
                        if (server != callbackServer || code != null || callbackError != null) {
                            return response("400 Bad Request", "Login is no longer pending");
                        }
                        if (error != null) {
                        	callbackError = "Microsoft authorization failed: " + error;
                        } else { 
                        	code = receivedCode; 
                        }
                        // Allow the browser to load the success page before closing the listener.
                        serverKiller.schedule(() -> {
                            synchronized (callbackLock) {
                                if (server == callbackServer) stopServer();
                            }
                        }, 10, TimeUnit.SECONDS);
                        callbackLock.notifyAll();
                    }
                    if (error != null) return response("400 Bad Request", "Microsoft authorization failed");
                    return new HttpResponse("307 Temporary Redirect", "text/html; charset=UTF-8", "Pomyślnie zalogowano".getBytes(StandardCharsets.UTF_8), Map.of("Location", "http://localhost:22420/success"));
                }
            });
            addWebResource(callbackServer, "/success", "/files/web/success.html", "text/html; charset=UTF-8");
            addWebResource(callbackServer, "/success.html", "/files/web/success.html", "text/html; charset=UTF-8");
            addWebResource(callbackServer, "/image.png", "/files/web/image.png", "image/png");
            callbackServer.startListening(InetAddress.getByName("127.0.0.1"));
            server = callbackServer;
            serverKiller = Executors.newSingleThreadScheduledExecutor(task -> {
                Thread thread = new Thread(task, "Microsoft callback shutdown");
                thread.setDaemon(true);
                return thread;
            });
        }
    }

    private static HttpResponse response(String status, String message) {
        return new HttpResponse(status, "text/plain; charset=UTF-8",
                message.getBytes(StandardCharsets.UTF_8), Map.of());
    }

    private static void addWebResource(HttpServer server, String path, String resource, String contentType) {
        server.addAction(new HttpServerAction(path) {
            @Override
            public Object run(String method, String ip, BufferedReader in) throws IOException {
                if (!method.equals("GET") && !method.equals("HEAD")) {
                    return response("405 Method Not Allowed", "Expected GET or HEAD");
                }
                try (InputStream input = EndiLauncher.class.getResourceAsStream(resource)) {
                    if (input == null) return response("404 Not Found", "Not found");
                    return new HttpResponse("200 OK", contentType, input.readAllBytes(), Map.of());
                }
            }
        });
    }

	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addObserver(String p, PropertyChangeListener l) {
		pcs.addPropertyChangeListener(p, l);
	}

	public String toString() {
		return "Microsoft";
	};
}
