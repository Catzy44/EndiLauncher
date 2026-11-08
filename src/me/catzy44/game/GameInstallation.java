package me.catzy44.game;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import me.catzy44.Interface;
import me.catzy44.game.installers.InstallationContext;
import me.catzy44.game.installers.FabricInstaller;
import me.catzy44.game.installers.ForgeInstaller;
import me.catzy44.game.installers.VanillaInstaller;
import me.catzy44.struct.GameVersion;
import me.catzy44.struct.accounts.MinecraftProfile;
import me.catzy44.tools.Download;
import me.catzy44.user.UserProfileManager;
import me.catzy44.utils.SystemInfo;
import me.catzy44.utils.Utils;

public class GameInstallation {
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	JsonObject manifest;
	
	private String osName = GameInstallationManager.osName;
	private String osVersion = GameInstallationManager.osVersion;
	private String arch = GameInstallationManager.arch;
	
	//generated during init
	//String jvmCommandLine;
	//String gameCommandLine;
	String classpath;
	
	private String installationName;
	private boolean installed = false;
	private boolean showSnapshots = false;
	private boolean showOlds = false;
	
	private boolean installSodium = false;
	private boolean installFabricAPI = false;
	
	private boolean installMesa = false;
	
	File gameDir;
	File assetsDir;
	File libsDir;
	File natDir;
	//File coreFile;
	File installationFile;
	
	GameVersion ver;
	//String assetsVersionName;
	
	private void mkdirIN(File f) {
		if(!f.exists() || !f.isDirectory()) {
			f.mkdirs();
		}
	}

	public String getName() {
		return installationName;
	}

	public File getGameDirectory() {
		return gameDir;
	}

	public File getModsDirectory() {
		File modsDirectory = new File(gameDir, "mods");
		mkdirIN(modsDirectory);
		return modsDirectory;
	}

	public Download getDownloader() {
		return installationContext == null ? null : installationContext.getDownloader();
	}

	public GameInstallation(String installationName, boolean createIfNotExists) {
		this.instance = this;
		try {
			this.installationName = installationName;
			gameDir = new File(GameInstallationManager.games,installationName);
			
			mkdirIN(gameDir);
			assetsDir = new File(gameDir, "assets");
			mkdirIN(assetsDir);
			libsDir = new File(gameDir, "libraries");
			mkdirIN(libsDir);
			natDir = new File(gameDir, "natives");
			mkdirIN(natDir);
			
			installationFile = new File(gameDir, "installation.json");
			if(!installationFile.exists()) {
				if(!createIfNotExists) {
					return;
				}
				ver = new GameVersion(GameInstallationManager.getLatestRelease());
				save();
			}
			install = gson.fromJson(Utils.readEverythingFromFile(installationFile), JsonObject.class);
			
			ver = new GameVersion(install.get("version").getAsJsonObject());
			installed = install.get("installed") == null ? false : install.get("installed").getAsBoolean();
			showSnapshots = install.get("showSnapshots") == null ? false : install.get("showSnapshots").getAsBoolean();
			showOlds = install.get("showOlds") == null ? false : install.get("showOlds").getAsBoolean();
			
			installSodium = install.get("installSodium") == null ? false : install.get("installSodium").getAsBoolean();
			installFabricAPI = install.get("installFabricAPI") == null ? false : install.get("installFabricAPI").getAsBoolean();
			
			pcs.addPropertyChangeListener("status",new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					status = (Status) e.getNewValue();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();//1.17.1-forge-37.0.108
		}
	}
	
	public void save() throws IOException {
		JsonObject installation = new JsonObject();
		installation.add("version", ver.toJson());
		installation.addProperty("installed", installed);
		installation.addProperty("showSnapshots", showSnapshots);
		installation.addProperty("showOlds", showOlds);
		
		installation.addProperty("installSodium", installSodium);
		installation.addProperty("installFabricAPI", installFabricAPI);
		Utils.replaceFileContents(installationFile, gson.toJson(installation));
	}
	
	private GameInstallation instance;
	public GameInstallation(String installationName) {
		this(installationName,false);
	}
	
	public enum Status {
		INITIATED("Rozpoczęto",0,false),
		INSTALLING_DOWNLOADING_ASSETS("Pobieranie assetów",1,false),
		INSTALLING_DOWNLOADING_LIBRARIES("Pobieranie bibliotek",2,false),
		INSTALLING_UNZIPPING_LIBRARY("Wypakowywanie biblioteki",3,true),
		INSTALLING_DOWNLOADING_CORE("Pobieranie jądra",4,false),
		INSTALLING_CREATING_COMMANDLINE("Przygotowywanie startera",5,false),

		INSTALLING_DOWNLOADING_FORGE("Pobieranie forge",6,false),
		INSTALLING_INSTALLING_FORGE("Instalacja forge",7,false),
		INSTALLING_DOWNLOADING_FORGE_LIBRARIES("Pobieranie bibliotek forge",8,false),
		
		INSTALLING_DOWNLOADING_FABRIC("Pobieranie fabric",6,false),
		INSTALLING_INSTALLING_FABRIC("Instalowanie fabric",7,false),
		INSTALLING_DOWNLOADING_FABRIC_LIBRARIES("Pobieranie bibliotek fabric",8,false),
		
		INSTALLING_SODIUM("Pobieranie Sodium...",9,false), 
		INSTALLING_FABRIC_API("Pobieranie FabricAPI...",9,false),

		INSTALLING_FINISHED("Ukończono",9,false),

		INSTALLING_FAILED_NETWORK("Błąd: Problem z siecią",9,false), 
		INSTALLING_FAILED_OTHER("Błąd: Nieznany",9,false);
		
		private String s = "";
		private int step;
		
		Status(String s, int step, boolean nothing) {
			this.s = s;
			this.step = step;
		}

		@Override
		public String toString() {
			return s;
		}

		public int getStep(boolean modded) {
			if(modded && step >= 6) {
				return step-3;
			}
			return step;
		}
	}
	
	public int getStepCount() {
		if(ver.isModded()) {
			return 9+(isInstallSodium() ? 1 : 0)+(isInstallFabricAPI() ? 1 : 0);
		} else {
			return 6;
		}
	}

	private Status status = Status.INITIATED;
	
	private volatile InstallationContext installationContext;
	
	private Thread installer;
	
	private boolean installingNow = false;
	
	public void cancelInstall() {//TODO
		if (installer != null) installer.interrupt();
		if (installationContext != null) installationContext.cancel();
		//pcs.firePropertyChange("status", status, Status.INSTALLING_FAILED_OTHER);
	}
	
	public boolean install() {//status/completedArtifact/downloaded
		installer = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					status = Status.INITIATED;
					
					manifest = GameInstallationManager.downloadVersionManifest(ver.getMcVersion());
					if(manifest == null) {
						System.out.println("manifest null!");
						return;
					}
					
					setInstalled(false);
					setInstallingNow(true);

					installationContext = new InstallationContext(instance, manifest, GameInstallation.this::shouldIncludeArgument);
					new VanillaInstaller(installationContext).install();

					if (ver.isForge()) {
						new ForgeInstaller(installationContext).install();
					} else if(ver.isFabric()) {
						new FabricInstaller(installationContext).install();
					}

					manifest = installationContext.manifest;
					classpath = installationContext.classpath;
					createCommandline();
					
					save();
					pcs.firePropertyChange("status", status, Status.INSTALLING_FINISHED);
					
					setInstalled(true);
					
				} catch (InterruptedException e) {
					setInstalled(false);
					//e.printStackTrace();
				} catch (Exception e) {
					setInstalled(false);
					e.printStackTrace();
				}
				
				setInstallingNow(false);
				
				cancelInstall();//cleanup
			}
		});
		installer.start();
		
		return true;
	}
	
	public void setInstallSodium(boolean installSodium) {
		this.installSodium = installSodium;
		
		if(installSodium) {
			installed = false;
		} else {
			Sodium.uninstallSodium(instance);
		}
	}
	
	public void setInstallFabricAPI(boolean installFabricAPI) {
		this.installFabricAPI = installFabricAPI;
		
		if(installFabricAPI) {
			installed = false;
		} else {
			FabricAPI.uninstallFabricAPI(instance);
		}
	}

	public boolean prepareToStart() throws Exception {
		loadInstalledVersionManifest();
		buildClasspath();
		createCommandline();
		return true;
	}

	public JsonObject getVersionManifest() { return manifest; }

	String cpCoreEnding = "";
	public void loadInstalledVersionManifest() throws JsonSyntaxException, IOException {
		cpCoreEnding = "";
		
		File manifestF = new File(gameDir, "versions/" + ver.getProfileName() + "/" + ver.getProfileName() + ".json");
		
		manifest = gson.fromJson(Utils.readEverythingFromFile(manifestF), JsonObject.class);
		
		if (manifest.get("inheritsFrom") != null) {
			String in = manifest.get("inheritsFrom").getAsString();
			JsonObject inherits = gson.fromJson(Utils.readEverythingFromFile(new File(gameDir, "versions/" + in + "/" + in + ".json")), JsonObject.class);

			if(inherits.get("arguments") != null) {
				inherits.get("arguments").getAsJsonObject().get("game").getAsJsonArray().addAll(manifest.get("arguments").getAsJsonObject().get("game").getAsJsonArray());
				inherits.get("arguments").getAsJsonObject().get("jvm").getAsJsonArray().addAll(manifest.get("arguments").getAsJsonObject().get("jvm").getAsJsonArray());
			} else {
				inherits.addProperty("minecraftArguments", inherits.get("minecraftArguments").getAsString()+" "+manifest.get("minecraftArguments").getAsString());
			}
			inherits.get("libraries").getAsJsonArray().addAll(manifest.get("libraries").getAsJsonArray());
			inherits.addProperty("mainClass", manifest.get("mainClass").getAsString());

			manifest = inherits;
			
			if (ver.isForge()) {
				cpCoreEnding = new File(gameDir, "versions/" + ver.getProfileName() + "/" + ver.getProfileName() + ".jar").getAbsolutePath();
			} else {
				// Fabric Loader is already included in libraries. Older installations
				// also contain a copied loader in the profile directory; do not load it twice.
				cpCoreEnding = new File(gameDir, "versions/" + in + "/" + in + ".jar").getAbsolutePath();
			}
		} else {//vanilla
			cpCoreEnding +=  new File(gameDir, "versions/" + ver.getMcVersion() + "/" + ver.getMcVersion() + ".jar").getAbsolutePath();
		}
	}
	
	public void startGame() {
		//we need: jvmCommandLine X
		//gameCommandLine X
		//all dirs X
		//classpath 
		//cpcoreending X
		//version X
		
		String jvm = "";
		for(String s : jvmCmdArgs) {
			String arg = s.replace("${natives_directory}", natDir.getAbsolutePath())
			.replace("${launcher_name}", "EndiLauncher")
			.replace("${launcher_version}", "1.0.0")
			.replace("${classpath}", classpath + cpCoreEnding)
			
			.replace("${library_directory}", libsDir.getAbsolutePath())
			.replace("${classpath_separator}", ";")
			.replace("${version_name}", ver.getProfileName());
			
			if(arg.contains(" ")) {
				jvm += "\""+arg+"\" ";
			} else {
				jvm += arg+" ";
			}
		}
	
		MinecraftProfile mp = UserProfileManager.getActiveProfile().getAk();
		
		String game = "";
		for(String s : gameCmdArgs) {
			String arg = s.replace("${auth_player_name}", mp.getNickname())
			.replace("${version_name}", ver.getProfileName())
			.replace("${game_directory}", gameDir.getAbsolutePath())
			.replace("${assets_root}", assetsDir.getAbsolutePath())
			.replace("${assets_index_name}", ver.getAssetsVersion())
			.replace("${auth_uuid}", mp.getUuid())
			.replace("${auth_access_token}", mp.getAccessToken() == null ? "xxx" : mp.getAccessToken())
			.replace("${user_type}", "mojang")
			.replace("${version_type}", manifest.get("type").getAsString())
			.replace("${user_properties}", "{}");
			
			if(arg.contains(" ")) {
				game += "\""+arg+"\" ";
			} else {
				game += arg+" ";
			}
		}
		
		String exec = "java " + jvm + " " + manifest.get("mainClass").getAsString() + " "+ game;
		/*try {
			Runtime.getRuntime().exec("cmd /c start cmd.exe /c \"" + exec + "& pause\"", null, gameDir);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		try {
			Interface.consoleMode(true);
			setGameRunning(true);
			Interface.printToConsole("");
			Interface.printToConsole("-------------------------------------------------------");
			Interface.printToConsole("  Uruchamianie gry!");
			Interface.printToConsole("-------------------------------------------------------");
			
			gameProc = Runtime.getRuntime().exec(exec,null,gameDir);
			
			inputReader = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						BufferedReader r = new BufferedReader(new InputStreamReader(gameProc.getInputStream(),"UTF-8"));
						String s;
						while ((s = r.readLine()) != null && !Thread.interrupted() && gameProc.isAlive()) {
							Interface.printToConsole(s);
							if(s.contains("[Render thread/INFO]: Stopping!")) {//TODO implement Log4J errors in RED!
								Matcher matcher = gameStopPattern.matcher(s);
								if(!matcher.matches()) {
									continue;
								}
								
								cleanupGame(false);
								Interface.consoleMode(false);
								Interface.printToConsole("-------------------------------------------------------");
								Interface.printToConsole("  Gra została zamknięta.");
								Interface.printToConsole("-------------------------------------------------------");
								return;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					cleanupGame(false);
					Interface.consoleMode(false);
					Interface.printToConsole("-------------------------------------------------------");
					Interface.printToConsole("<font color=orange>  Proces gry został zatrzymany.</font>");
					Interface.printToConsole("-------------------------------------------------------");
				}
			});
			inputReader.start();
			
			errorReader = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						BufferedReader r = new BufferedReader(new InputStreamReader(gameProc.getErrorStream()));
						String s;
						while ((s = r.readLine()) != null && !Thread.interrupted()) {
							Interface.printToConsole("<font color=red>"+s+"</font>");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			errorReader.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private Thread inputReader;
	private Thread errorReader;
	private Process gameProc;
	private boolean isGameRunning = false;
	private Pattern gameStopPattern = Pattern.compile("\\[..:..:..\\] \\[Render thread\\/INFO\\]: Stopping!");

	public void cleanupGame(boolean force) {
		if (inputReader != null)
			inputReader.interrupt();
		inputReader = null;
		if (errorReader != null)
			errorReader.interrupt();
		errorReader = null;
		if (gameProc != null) {
			if (force) {
				gameProc.destroyForcibly();
			} else {
				gameProc.destroy();
			}
		}
		gameProc = null;
		setGameRunning(false);
	}

	public boolean isGameRunning() {
		return isGameRunning;
	}

	public void setGameRunning(boolean isGameRunning) {
		this.isGameRunning = isGameRunning;
		Interface.reloadPlayBtnText();
	}

	
	
	
	
	private void buildClasspath() throws Exception {
		JsonArray libs = manifest.get("libraries").getAsJsonArray();
		classpath = "";
		libs:
		for (int processed = 0; processed < libs.size(); processed++) {
			status = Status.INSTALLING_DOWNLOADING_LIBRARIES;
			JsonObject lib = libs.get(processed).getAsJsonObject();

			String name = lib.get("name").getAsString();
			pcs.firePropertyChange("processingArtifact", null, name);
			
			if (!shouldIncludeArgument(lib, new HashMap<String, Boolean>())) {
				pcs.firePropertyChange("completedArtifact", null, new int[] {processed, libs.size()});
				continue;
			}
			
			if(lib.get("url") != null) {
				String d[] = name.split(":");
				
				String path = d[0].replaceAll("\\.", "/");
				String fname = d[1];
				String version = d[2];
				
				String filename = fname+"-"+version+".jar";
				
				File saveLocation = new File(libsDir,path+"/"+fname+"/"+version+"/"+filename);
				classpath += saveLocation.getAbsolutePath()+";";
				continue;
			}
			
			if(lib.get("downloads").getAsJsonObject().get("artifact") == null) {
				continue;//in older versions like 1.7.10 sometimes there're libraries without artifact, but with classifiers!
			}
			
			JsonObject art = lib.get("downloads").getAsJsonObject().get("artifact").getAsJsonObject();
			{
				File saveLocation = new File(libsDir, art.get("path").getAsString());
				classpath += saveLocation.getAbsolutePath()+";";
			}
		}
	}
	
	

	
	
	private List<String> jvmCmdArgs = new ArrayList<String>();
	private List<String> gameCmdArgs = new ArrayList<String>();
	
	private void createCommandline() {
		Map<String,Boolean> curRules = new HashMap<String,Boolean>();
		curRules.put("is_demo_user",false);
		curRules.put("has_custom_resolution", false);
		curRules.put("has_quick_plays_support", false);
		curRules.put("is_quick_play_singleplayer", false);
		curRules.put("is_quick_play_multiplayer", false);
		curRules.put("is_quick_play_realms", false);
		
		if(manifest.get("arguments") == null) {
			gameCmdArgs.clear();
			String gameArguments = manifest.get("minecraftArguments").getAsString();
			String[] spl = gameArguments.split(" ");
			for(String s : spl) {
				gameCmdArgs.add(s);
			}
			
			jvmCmdArgs.clear();
			jvmCmdArgs.add("-Djava.library.path=${natives_directory}");
			jvmCmdArgs.add("-Dminecraft.launcher.brand=${launcher_name}");
			jvmCmdArgs.add("-Dminecraft.launcher.version=${launcher_version}");
			jvmCmdArgs.add("-cp");
			jvmCmdArgs.add("${classpath}");
			
			if(SystemInfo.getOS() == SystemInfo.MACOS) {
				jvmCmdArgs.add("-XstartOnFirstThread");
			} else if(SystemInfo.getOS() == SystemInfo.WINDOWS) {
				jvmCmdArgs.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
				if(SystemInfo.getWindowsVersion() == 10) {
					jvmCmdArgs.add("-Dos.name=Windows 10");
					jvmCmdArgs.add("-Dos.version=10.0");
				}
			}
			return;
		}
		
		gameCmdArgs.clear();
		JsonArray gameArgs = manifest.get("arguments").getAsJsonObject().get("game").getAsJsonArray();
		for(int i = 0; i < gameArgs.size(); i++) {
			JsonElement val = gameArgs.get(i);
			if(val.isJsonPrimitive()) {
				gameCmdArgs.add(val.getAsString());
			} else if (shouldIncludeArgument(val.getAsJsonObject(), curRules)) {
				addArgumentValue(gameCmdArgs, val.getAsJsonObject().get("value"));
			}
		}
		
		jvmCmdArgs.clear();
		JsonArray jvmArgs = manifest.get("arguments").getAsJsonObject().get("jvm").getAsJsonArray();
		for(int i = 0; i < jvmArgs.size(); i++) {
			JsonElement val = jvmArgs.get(i);
			if(val.isJsonPrimitive()) {
				jvmCmdArgs.add(val.getAsString());
			} else if (shouldIncludeArgument(val.getAsJsonObject(), curRules)) {
				addArgumentValue(jvmCmdArgs, val.getAsJsonObject().get("value"));
			}
		}
	}

	private void addArgumentValue(List<String> target, JsonElement value) {
		if (value.isJsonPrimitive()) {
			target.add(value.getAsString());
			return;
		}
		for (JsonElement entry : value.getAsJsonArray()) {
			target.add(entry.getAsString());
		}
	}

	private boolean shouldIncludeArgument(JsonObject argument, Map<String, Boolean> features) {
		if (argument.get("rules") == null) {
			return true;
		}

		boolean allowed = false;
		for (JsonElement ruleElement : argument.getAsJsonArray("rules")) {
			JsonObject rule = ruleElement.getAsJsonObject();
			if (ruleMatches(rule, features)) {
				allowed = "allow".equals(rule.get("action").getAsString());
			}
		}
		return allowed;
	}

	private boolean ruleMatches(JsonObject rule, Map<String, Boolean> features) {
		if (rule.get("os") != null) {
			JsonObject os = rule.getAsJsonObject("os");
			if (os.get("name") != null && !os.get("name").getAsString().equals(osName)) {
				return false;
			}
			if (os.get("arch") != null && !os.get("arch").getAsString().equals(arch)) {
				return false;
			}
		}
		if (rule.get("features") != null) {
			for (Entry<String, JsonElement> feature : rule.getAsJsonObject("features").entrySet()) {
				if (features.getOrDefault(feature.getKey(), false) != feature.getValue().getAsBoolean()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isInstallerRunning() {
		return installingNow;
	}
	
	public void setInstallingNow(boolean installingNow) {
		this.installingNow = installingNow;
		Interface.reloadPlayBtnText();
	}

	public boolean isInstalled() {
		return installed;
	}
	
	public void setInstalled(boolean installed) {
		this.installed = installed;
		Interface.reloadPlayBtnText();
	}

	public boolean showSnapshots() {
		return showSnapshots;
	}

	public boolean showOlds() {
		return showOlds;
	}

	public void showSnapshots(boolean b) {
		showSnapshots = b;
	}

	public void showOlds(boolean b) {
		showOlds = b;
	}

	public GameVersion getVersion() {
		return ver;
	}

	public boolean isInstallSodium() {
		return installSodium;
	}

	public boolean isInstallFabricAPI() {
		return installFabricAPI;
	}

	public PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private JsonObject install;
	
	public JsonObject getInstall() {
		return install;
	}

	public void addObserver(String p, PropertyChangeListener l) {
		pcs.addPropertyChangeListener(p, l);
	}
	
	public void removeAllObservers() {
		for(PropertyChangeListener r : pcs.getPropertyChangeListeners()){
			pcs.removePropertyChangeListener(r);
		}
	}

	public String toString() {
		return "GameInstallation";
	};
}
