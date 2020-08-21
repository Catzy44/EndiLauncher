package me.catzy44.game;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import me.catzy44.Interface;
import me.catzy44.struct.GameVersion;
import me.catzy44.struct.accounts.MinecraftProfile;
import me.catzy44.tools.Download;
import me.catzy44.tools.Unzip;
import me.catzy44.user.UserProfileManager;
import me.catzy44.utils.Hash;
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

	public Download getDownloader() {
		return down;
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
	
	private Download down;
	private Unzip unzip;
	
	private Thread installer;
	
	private boolean installingNow = false;
	
	public void cancelInstall() {//TODO
		installer.interrupt();
		if(down != null) {
			down.cancel();
		}
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

					pcs.firePropertyChange("status", status, Status.INSTALLING_DOWNLOADING_LIBRARIES);
					installLibs();//
					pcs.firePropertyChange("status", status, Status.INSTALLING_DOWNLOADING_ASSETS);
					installAssets();//
					installClient();//

					if (ver.isForge()) {
						installForge();
						loadVersionManifest();
						pcs.firePropertyChange("status", status, Status.INSTALLING_DOWNLOADING_FORGE_LIBRARIES);
						installLibs();
						installForgeCore();
					} else if(ver.isFabric()) {
						installFabric();
						loadVersionManifest();
						pcs.firePropertyChange("status", status, Status.INSTALLING_DOWNLOADING_FABRIC_LIBRARIES);
						installLibs();
						installFabricCore();
						if(isInstallSodium()) {
							pcs.firePropertyChange("status", status, Status.INSTALLING_SODIUM);
							Sodium.installSodium(instance);
						}
						if(isInstallFabricAPI()) {
							pcs.firePropertyChange("status", status, Status.INSTALLING_FABRIC_API);
							FabricAPI.installFabricAPI(instance);
						}
					}

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
		loadVersionManifest();
		buildClasspath();
		createCommandline();
		return true;
	}

	String cpCoreEnding = "";
	private void loadVersionManifest() throws JsonSyntaxException, IOException {
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
			
			cpCoreEnding += new File(gameDir, "versions/" + ver.getProfileName() + "/" + ver.getProfileName() + ".jar").getAbsolutePath();
			if(!ver.isForge()) {
				cpCoreEnding +=";"+  new File(gameDir, "versions/" + in + "/" + in + ".jar").getAbsolutePath();
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

	private boolean installClient() throws Exception {
		pcs.firePropertyChange("status", status, Status.INSTALLING_DOWNLOADING_CORE);
		
		JsonObject json = manifest.get("downloads").getAsJsonObject().get("client").getAsJsonObject();
		
		String url = json.get("url").getAsString();
		String sha1 = json.get("sha1").getAsString();
		
		File profile = new File(gameDir,"versions/"+ver.getMcVersion());
		Utils.mkDirs(profile);
		Utils.replaceFileContents(new File(profile,ver.getMcVersion()+".json"), manifest.toString());
		
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		File save = new File(profile,ver.getMcVersion()+".jar");
		
		if(save.exists() && save.isFile() && new Hash(save).equals(sha1)) {
			return true;
		}
		
		down = new Download(new URL(url), save.getAbsolutePath());

		down.addObserver("downloaded", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				pcs.firePropertyChange("downloaded", e.getOldValue(), e.getNewValue());
			}
		});

		/*while (down.getStatus() == Download.DOWNLOADING) {
			down.waitt(10000);
		}*/
		down.locker.waitForUnlock();
		
		if(down.getStatus() != Download.COMPLETE) {
			throw down.getException();
		}
		
		return true;
	}
	
	private boolean installFabricCore() throws IOException {
		File coreFile = new File(gameDir,"/versions/"+ver.getProfileName()+"/"+ver.getProfileName()+".jar");
		File from = new File(libsDir, "net/fabricmc/fabric-loader/"+ver.getFabricVersion()+"/fabric-loader-" + ver.getFabricVersion()+".jar");
		Utils.deleteIfExists(coreFile);
		Files.copy(from.toPath(), coreFile.toPath());
		
		return true;
	}
	
	private boolean installFabric() throws Exception {
		pcs.firePropertyChange("status", status, Status.INSTALLING_DOWNLOADING_FABRIC);
		String mavenManifest = Utils.readEverythingFromURL("https://maven.fabricmc.net/net/fabricmc/fabric-installer/maven-metadata.xml");
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(new InputSource(new StringReader(mavenManifest)));
        doc.getDocumentElement().normalize();
        
        Element versioning = (Element) doc.getElementsByTagName("versioning").item(0);
        String latest = versioning.getElementsByTagName("latest").item(0).getTextContent();
        
        String filename = "fabric-installer-"+latest+".jar";
        String url = "https://maven.fabricmc.net/net/fabricmc/fabric-installer/"+latest+"/"+filename;
        
        if(Thread.interrupted()) {
			throw new InterruptedException();
		}
        
        File fabricF = new File(gameDir.getAbsolutePath() + "/" + filename);
        down = new Download(new URL(url), fabricF.getAbsolutePath());
        fabricF.deleteOnExit();

		down.addObserver("downloaded", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				pcs.firePropertyChange("downloaded", e.getOldValue(), e.getNewValue());
			}
		});

		while (down.getStatus() == Download.DOWNLOADING) {
			down.waitt(10000);
		}
		
		if(down.getStatus() != Download.COMPLETE) {
			throw down.getException();
		}
		
		pcs.firePropertyChange("status", status, Status.INSTALLING_INSTALLING_FABRIC);
		
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		Process fabricInst = Runtime.getRuntime().exec("java -jar "+filename+" client -dir \""+gameDir.getAbsolutePath()+"\" -mcversion "+ver.getMcVersion()+" -noprofile", null, gameDir);
		BufferedReader bf = new BufferedReader(new InputStreamReader(fabricInst.getInputStream(), "UTF-8"));
		String line;
		boolean status = false;
		while ((line = bf.readLine()) != null) {
			if (line.equals("Gotowe")) {
				status = true;
			}
			if(line.contains("Installing")) {
				String[] s = line.split(" ");
				String fVer = s[4];
				ver.setFabricVersion(fVer);
				System.out.println("installed fabric "+fVer);
			}
		}
		
		Utils.deleteIfExists(fabricF);
		
        return status;
	}
	
	private boolean installForgeCore() throws IOException {
		String forge = ver.getMcVersion() + "-" + ver.getForgeVersion();
		File from = new File(libsDir, "net/minecraftforge/forge/" + forge + "/forge-" + forge + "-client.jar");
		File coreFile = new File(gameDir,"/versions/"+ver.getProfileName()+"/"+ver.getProfileName()+".jar");
		Utils.deleteIfExists(coreFile);
		Files.copy(from.toPath(), coreFile.toPath());
		
		return true;
	}
	
	private void buildClasspath() throws Exception {
		JsonArray libs = manifest.get("libraries").getAsJsonArray();
		classpath = "";
		libs:
		for (int processed = 0; processed < libs.size(); processed++) {
			status = Status.INSTALLING_DOWNLOADING_LIBRARIES;
			JsonObject lib = libs.get(processed).getAsJsonObject();

			String name = lib.get("name").getAsString();
			processingArtifact(name);
			
			// LIBRARIES HAVE OWN RULES!
			if (lib.get("rules") != null) {
				for (JsonElement ruleE : lib.get("rules").getAsJsonArray()) {
					JsonObject rule = ruleE.getAsJsonObject();

					boolean allow = (rule.get("action").getAsString().equals("allow"));

					boolean good2go = true;
					if (rule.get("os") != null) {
						JsonObject os = rule.get("os").getAsJsonObject();
						if (os.get("name") != null) {
							if (!os.get("name").getAsString().equals(osName)) {
								good2go = false;
							}
						}
						if (os.get("version") != null) {
							if (!os.get("version").getAsString().equals(osVersion)) {
								good2go = false;
							}
						}
						if (os.get("arch") != null) {
							if (!os.get("arch").getAsString().equals(arch)) {
								good2go = false;
							}
						}
					}
					
					if(allow) {
						//musi spełniać wszystkie
						if(!good2go) {
							completedArtifact(processed,libs.size());
							continue libs;
						}
					} else {
						//nie moze spełniać żadnego
						if(good2go) {
							completedArtifact(processed,libs.size());
							continue libs;
						}
					}
				}
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
	
	private boolean installForge() throws Exception {
		pcs.firePropertyChange("status", status, Status.INSTALLING_DOWNLOADING_FORGE);
		
		Interface.printToConsole("-------------------------------------------------");
		Interface.printToConsole("  Instalacja forge, to zajmie kilka minut...");
		Interface.printToConsole("-------------------------------------------------");
		
		Thread.sleep(1000);//give user a change to see message

		String forge = ver.getMcVersion() + "-" + ver.getForgeVersion();
		String filename = "forge-" + forge + "-installer.jar";
		
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		// download forge
		File forgeF = new File(gameDir.getAbsolutePath() + "/" + filename);
		down = new Download(new URL("https://maven.minecraftforge.net/net/minecraftforge/forge/" + forge + "/" + filename), forgeF.getAbsolutePath());

		down.addObserver("downloaded", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				pcs.firePropertyChange("downloaded", e.getOldValue(), e.getNewValue());
			}
		});

		/*while (down.getStatus() == Download.DOWNLOADING) {
			down.waitt(10000);
		}*/
		down.locker.waitForUnlock();
		
		if(down.getStatus() != Download.COMPLETE) {
			throw down.getException();
		}

		// unpack installer
		File fnm = new File(gameDir, "ForgeNastyInstaller.jar");
		fnm.deleteOnExit();
		Utils.unpackFileFromJar("/files/other/ForgeNastyInstaller.jar", fnm);
		
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		pcs.firePropertyChange("status", status, Status.INSTALLING_INSTALLING_FORGE);
		// run installer
		Process forgeInst = Runtime.getRuntime().exec("java -cp \"ForgeNastyInstaller.jar;" + filename + "\" me.catzy44.ForgeNastyInstaller \"" + gameDir.getAbsolutePath()+"\"", null, gameDir);
		BufferedReader bf = new BufferedReader(new InputStreamReader(forgeInst.getInputStream(), "UTF-8"));
		String line;
		boolean status = false;
		while ((line = bf.readLine()) != null) {
			if (line.equals("NFI SUCCESS")) {
				status = true;
			} else {
				if(line.startsWith("You can delete this")) {
					continue;
				}
				Interface.printToConsole(line);
			}
		}
		forgeF.delete();
		fnm.delete();
		return status;
	}
	
	int ta[] = new int[2];
	private void completedArtifact(int no, int count) {
		ta[0] = no;
		ta[1] = count;
		pcs.firePropertyChange("completedArtifact", null, ta);
	}
	private void processingArtifact(String filename) {
		String[] spl = filename.split("/");
		pcs.firePropertyChange("processingArtifact", null, spl[spl.length-1]);
	}

	private void installLibs() throws Exception {
		JsonArray libs = manifest.get("libraries").getAsJsonArray();
		classpath = "";
		libs:
		for (int processed = 0; processed < libs.size() && !Thread.interrupted(); processed++) {
			status = Status.INSTALLING_DOWNLOADING_LIBRARIES;
			JsonObject lib = libs.get(processed).getAsJsonObject();

			String name = lib.get("name").getAsString();
			processingArtifact(name);
			
			// LIBRARIES HAVE OWN RULES!
			if (lib.get("rules") != null) {
				for (JsonElement ruleE : lib.get("rules").getAsJsonArray()) {
					JsonObject rule = ruleE.getAsJsonObject();

					boolean allow = (rule.get("action").getAsString().equals("allow"));

					boolean good2go = true;
					if (rule.get("os") != null) {
						JsonObject os = rule.get("os").getAsJsonObject();
						if (os.get("name") != null) {
							if (!os.get("name").getAsString().equals(osName)) {
								good2go = false;
							}
						}
						if (os.get("version") != null) {
							if (!os.get("version").getAsString().equals(osVersion)) {
								good2go = false;
							}
						}
						if (os.get("arch") != null) {
							if (!os.get("arch").getAsString().equals(arch)) {
								good2go = false;
							}
						}
					}
					
					if(allow) {
						//musi spełniać wszystkie
						if(!good2go) {
							completedArtifact(processed, libs.size());
							continue libs;
						}
					} else {
						//nie moze spełniać żadnego
						if(good2go) {
							completedArtifact(processed, libs.size());
							continue libs;
						}
					}
				}
			}
			
			if(lib.get("url") != null) {
				//old format
				String url = lib.get("url").getAsString();
				String d[] = name.split(":");
				
				String path = d[0].replaceAll("\\.", "/");//org.ow2.asm
				String fname = d[1];//asm-util
				String version = d[2];//9.2
				
				String filename = fname+"-"+version+".jar";
				
				String aurl = url+path+"/"+fname+"/"+version+"/"+filename;
				File saveLocation = new File(libsDir,path+"/"+fname+"/"+version+"/"+filename);
				
				Utils.mkDirs(saveLocation.getParentFile());
				
				down = new Download(new URL(aurl), saveLocation.getAbsolutePath());
				down.addObserver("downloaded", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						pcs.firePropertyChange("downloaded", e.getOldValue(), e.getNewValue());
					}
				});
				/*while (down.getStatus() == Download.DOWNLOADING) {
					down.waitt(10000);
				}*/
				down.locker.waitForUnlock();
				if(down.getStatus() != Download.COMPLETE) {
					throw down.getException();
				}
				
				classpath += saveLocation.getAbsolutePath()+";";
				
				completedArtifact(processed, libs.size());
				continue;
			}
			
			if(lib.get("downloads") == null) {
				continue;
			}
			if (lib.get("downloads").getAsJsonObject().get("artifact") != null) {

				JsonObject art = lib.get("downloads").getAsJsonObject().get("artifact").getAsJsonObject();
				String hash = art.get("sha1").getAsString();
				xx: {
					File saveLocation = new File(libsDir, art.get("path").getAsString());

					File parentSaveDir = saveLocation.getParentFile();
					if (!parentSaveDir.exists() || !parentSaveDir.isDirectory()) {
						parentSaveDir.mkdirs();
					}

					classpath += saveLocation.getAbsolutePath() + ";";

					if (saveLocation.exists() && new Hash(saveLocation).toString().equals(hash)) {
						break xx;
					}
					down = new Download(new URL(art.get("url").getAsString()), saveLocation.getAbsolutePath());
					down.addObserver("downloaded", new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent e) {
							pcs.firePropertyChange("downloaded", e.getOldValue(), e.getNewValue());
						}
					});
					/*
					 * while (down.getStatus() == Download.DOWNLOADING) { down.waitt(10000); }
					 */
					down.locker.waitForUnlock();
					if (down.getStatus() != Download.COMPLETE) {
						if(down.getException() == null) {
							throw new InterruptedException("Download exception status "+down.getStatus());
						}
						throw down.getException();
					}
				}
				if (lib.get("downloads").getAsJsonObject().get("classifiers") == null) {
					completedArtifact(processed, libs.size());
					continue;
				}
			}
			
			if(lib.get("downloads").getAsJsonObject().get("classifiers").getAsJsonObject().get("natives-"+osName) == null) {
				//System.out.println(lib.get("downloads").getAsJsonObject().get("classifiers"));
				//TODO ??
				completedArtifact(processed, libs.size());
				continue;
			}
			
			JsonObject classifiers = lib.get("downloads").getAsJsonObject().get("classifiers").getAsJsonObject().get("natives-"+osName).getAsJsonObject();
			xx:
			{
				File saveLocation = new File(libsDir, classifiers.get("path").getAsString());
				String chash = classifiers.get("sha1").getAsString();
				
				File parentSaveDir = saveLocation.getParentFile();
				if(!parentSaveDir.exists() || !parentSaveDir.isDirectory()) {
					parentSaveDir.mkdirs();
				}
				if(saveLocation.exists() && new Hash(saveLocation).toString().equals(chash)) {
					break xx;
				}
				
				down = new Download(new URL(classifiers.get("url").getAsString()), saveLocation.getAbsolutePath());
				down.addObserver("downloaded", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						pcs.firePropertyChange("downloaded", e.getOldValue(), e.getNewValue());
					}
				});
				/*while (down.getStatus() == Download.DOWNLOADING) {
					down.waitt(10000);
				}*/
				down.locker.waitForUnlock();
				if(down.getStatus() != Download.COMPLETE) {
					throw down.getException();
				}
			}
			//String[] exc = new String[0];
			//String regex = "(META-INF\\/|.*\\.git|.*\\.sha1";
			status = Status.INSTALLING_UNZIPPING_LIBRARY;
			String regex = "(";
			excludes: {
				if (lib.get("extract") == null) {
					//System.out.println("x!" + lib);
					break excludes;
				}
				JsonObject extract = lib.get("extract").getAsJsonObject();

				JsonArray exclude = extract.get("exclude").getAsJsonArray();
				//exc = new String[exclude.size()];
				for (int z = 0; z < exclude.size(); z++) {
					//exc[z] = exclude.get(z).getAsString();
					regex += exclude.get(z).getAsString().replace("/", "\\/") + "|";
				}
			}
			regex = regex.substring(0, regex.length()-1);
			regex += ")";
			if(regex.length() <= 2) {
				regex = null;
			}
			String extName[] = lib.get("name").getAsString().split(":");
			
			File file = new File(libsDir,extName[0].replace(".", "/")+"/"+extName[1]+"/"+extName[2]+"/"+extName[1]+"-"+extName[2]+"-natives-"+osName+".jar");
			unzip = new Unzip();
			unzip.addObserver("processedEntry", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					pcs.firePropertyChange("processedEntry", e.getOldValue(), e.getNewValue());
				}
			});
			unzip.unzip(file, natDir, regex);
			while (unzip.getStatus() == Unzip.PROCESSING) {
				unzip.waitt(10000);
			}
			completedArtifact(processed, libs.size());
		}
	}
	
	private void installAssets() throws Exception {
		JsonObject assetIndex = manifest.get("assetIndex").getAsJsonObject();
		ver.setAssetsVersion(assetIndex.get("id").getAsString());
		String assetsIndexDownS = Utils.readEverythingFromURL(assetIndex.get("url").getAsString());
		JsonObject objects = gson.fromJson(assetsIndexDownS, JsonObject.class).get("objects").getAsJsonObject();
		{
			File assetIndexFile = new File(assetsDir, "/indexes/" + assetIndex.get("id").getAsString() + ".json");
			File assetIndexFileDir = assetIndexFile.getParentFile();
			if (!assetIndexFileDir.exists() || !assetIndexFileDir.isDirectory()) {
				assetIndexFileDir.mkdirs();
			}
			Utils.replaceFileContents(assetIndexFile, assetsIndexDownS);
		}
		Set<Entry<String, JsonElement>> entries = objects.entrySet();
		
		int processed = 0;
		int toProcess = entries.size();
		
		for (Entry<String, JsonElement> entry : entries) {
			if(Thread.interrupted()) {
				throw new InterruptedException();
			}

			JsonObject ass = entry.getValue().getAsJsonObject();

			String hash = ass.get("hash").getAsString();
			// int size = ass.get("size").getAsInt();

			String assetName = hash.substring(0, 2) + "/" + hash;
			processingArtifact(entry.getKey());
			
			File assetFile = new File(assetsDir, "objects/" + assetName);
			File assetDir = assetFile.getParentFile();
			if (!assetDir.exists() || !assetDir.isDirectory()) {
				assetDir.mkdirs();
			}

			if (assetFile.exists() && new Hash(assetFile).toString().equals(hash)) {
				processed++;
				completedArtifact(processed, toProcess);
				continue;
			}
			
			if(Thread.interrupted()) {
				throw new InterruptedException();
			}

			String url = "https://resources.download.minecraft.net/" + assetName;
			down = new Download(new URL(url), assetFile.getAbsolutePath());
			down.addObserver("downloaded", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					pcs.firePropertyChange("downloaded", e.getOldValue(), e.getNewValue());
				}
			});
			/*while (down.getStatus() == Download.DOWNLOADING) {
				down.waitt(10000);
			}*/
			down.locker.waitForUnlock();
			if(down.getStatus() != Download.COMPLETE) {
				if(down.getException() != null) {
					throw down.getException();
				}
				throw new InterruptedException("Download exception status: "+down.getStatus());
			}

			completedArtifact(processed, toProcess);
			processed++;
		}
	}
	
	private List<String> jvmCmdArgs = new ArrayList<String>();
	private List<String> gameCmdArgs = new ArrayList<String>();
	
	private void createCommandline() {
		Map<String,Boolean> curRules = new HashMap<String,Boolean>();
		curRules.put("is_demo_user",false);
		curRules.put("has_custom_resolution", false);
		
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
		args:
		for(int i = 0; i < gameArgs.size(); i++) {
			JsonElement val = gameArgs.get(i);
			if(val.isJsonPrimitive()) {
				gameCmdArgs.add(val.getAsString());
			} else {
				JsonObject obj = val.getAsJsonObject();
				for(JsonElement ruleE : obj.get("rules").getAsJsonArray()) {
					JsonObject rule = ruleE.getAsJsonObject();
					
					boolean allow = (rule.get("action").getAsString().equals("allow"));
					
					boolean good2go = true;
					for(Entry<String, JsonElement> featureE : rule.get("features").getAsJsonObject().entrySet()) {
						String key = featureE.getKey();
						if(!curRules.containsKey(key)) {
							System.out.println("UNKNW KEY: "+key);
							continue;
						}
						if(curRules.get(key) != featureE.getValue().getAsBoolean()) {
							good2go = false;
							break;
						}
					}
					if(allow) {
						//musi spełniać wszystkie
						if(!good2go) {
							continue args;
						}
					} else {
						//nie moze spełniać żadnego
						if(good2go) {
							continue args;
						}
					}
					
					JsonElement valPrimitive = obj.get("value");
					if(valPrimitive.isJsonPrimitive()) {
						gameCmdArgs.add(obj.get("value").getAsString());
					} else {
						JsonArray value = obj.get("value").getAsJsonArray();

						for (int z = 0; z < value.size(); z++) {
							gameCmdArgs.add(value.get(z).getAsString());
						}
					}
				}
			}
		}
		
		jvmCmdArgs.clear();
		JsonArray jvmArgs = manifest.get("arguments").getAsJsonObject().get("jvm").getAsJsonArray();
		args:
		for(int i = 0; i < jvmArgs.size(); i++) {
			JsonElement val = jvmArgs.get(i);
			if(val.isJsonPrimitive()) {
				jvmCmdArgs.add(val.getAsString());
			} else {
				JsonObject obj = val.getAsJsonObject();
				for(JsonElement ruleE : obj.get("rules").getAsJsonArray()) {
					JsonObject rule = ruleE.getAsJsonObject();
					
					boolean allow = (rule.get("action").getAsString().equals("allow"));
					
					boolean good2go = true;
					if(rule.get("os") != null) {
						JsonObject os = rule.get("os").getAsJsonObject();
						if(os.get("name") != null) {
							if(!os.get("name").getAsString().equals(osName)) {
								good2go = false;
								break;
							}
						}
						if(os.get("version") != null) {
							if(!os.get("version").getAsString().equals(osVersion)) {
								good2go = false;
								break;
							}
						}
						if(os.get("arch") != null) {
							if(!os.get("arch").getAsString().equals(arch)) {
								good2go = false;
								break;
							}
						}
					}
					
					if(allow) {
						//musi spełniać wszystkie
						if(!good2go) {
							continue args;
						}
					} else {
						//nie moze spełniać żadnego
						if(good2go) {
							continue args;
						}
					}
					
					JsonElement valPrimitive = obj.get("value");
					if(valPrimitive.isJsonPrimitive()) {
						jvmCmdArgs.add(obj.get("value").getAsString());
					} else {
						JsonArray value = obj.get("value").getAsJsonArray();

						for (int z = 0; z < value.size(); z++) {
							jvmCmdArgs.add(value.get(z).getAsString());
						}
					}
				}
			}
		}
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
