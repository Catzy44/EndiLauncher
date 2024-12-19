package me.catzy44.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.catzy44.EndiLauncher;
import me.catzy44.Interface;
import me.catzy44.ui.creators.LauncherUpdateUI;
import me.catzy44.utils.Args;
import me.catzy44.utils.HttpUtil;
import me.catzy44.utils.HttpUtil.ContentType;
import me.catzy44.utils.HttpUtil.ReqStatus;
import me.catzy44.utils.HttpUtil.ReqType;
import me.catzy44.utils.SystemInfo;
import me.catzy44.utils.Utils;
import me.catzy44.utils.Version;

public class Updater {
	private static Download downloader = null;

	public static Download getDownloader() {
		return downloader;
	}
	
	//if this method returns TRUE launcher is not starting!
	public static boolean check(Args args) {
		int updateme = args.checkArg("updateme");
		if(updateme != -1) {
			Updater.finishLauncherUpdate(args.get(updateme+1));
			return true;
		}
		return false;
	}
	
	public static boolean killUpdaterIfJustUpdated(Args args) {
		int updatesuccess = args.checkArg("updatesuccess");
		if(updatesuccess != -1) {
			updateSuccess(Integer.valueOf(args.get(updatesuccess+1)));
		}
		return false;
	}
	
	private static File update = new File(EndiLauncher.workingDir,"update.jar");
	private static void updateSuccess(int updaterPid) {
		Utils.killProcessByPID(updaterPid);
		if(update.exists()) {
			update.delete();
		}
		Interface.printToConsole("<font color=green>Pomyślnie zaktualizowano do wersji "+EndiLauncher.version.get()+"</font>");
	}
	
	public static void finishLauncherUpdate(String s) {
		try {
			Interface.preInit();
			LauncherUpdateUI.showInterface();
			
			int time = 4000;
			long cur = System.currentTimeMillis();
			//progressbar updater
			new Thread(() -> {
				try {
					while (System.currentTimeMillis() - cur < time) {
						Thread.sleep(40);

						int timeleft = (int) (System.currentTimeMillis() - cur);
						LauncherUpdateUI.getProgressBar().setValue((int) (((double) timeleft / time) * 100d));
					}
					System.exit(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).start();
			
			
			File self = Utils.getJarFileF();
			
			File toUpdate = new File(s);
			Files.copy(self.toPath(), toUpdate.toPath(),StandardCopyOption.REPLACE_EXISTING);
			//Runtime.getRuntime().exec(Java.getJava()+" -jar \""+toUpdate.getAbsolutePath()+"\" updatesuccess");
			//Runtime.getRuntime().exec(new String[] {Java.getJavaBinaryStr(),"-jar",toUpdate.getAbsolutePath(),"updatesuccess"});
			Starter.startLauncherNohup(toUpdate, new String[]{"updatesuccess",ProcessHandle.current().pid()+""});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class LauncherUpdate {
		public static enum Status {
			AVAILABLE,CANNOT_CHECK,LATEST;
		}
		public Status status;
		public Version version;
		public String url;
		public LauncherUpdate(Status status, Version version, String url) {
			this.status = status;
			this.version = version;
			this.url = url;
		}
		
	}
	public static LauncherUpdate checkForLauncherUpdates() {
		//JsonObject info = gson.fromJson(JsonUtility.httpRequest("https://endimc.pl/api/launcher/latest", null),JsonObject.class);
		HttpUtil http = new HttpUtil("https://endimc.pl/api/launcher/latest", ReqType.GET);
		http.setAcceptType(ContentType.JSON);
		http.send();
		
		if(http.getResponseStatus() != ReqStatus.SUCCESS) {
			return new LauncherUpdate(LauncherUpdate.Status.CANNOT_CHECK,null,null);
		}
		JsonElement out = http.getResponseBodyAsJson();
		if(!out.isJsonObject()) {
			return new LauncherUpdate(LauncherUpdate.Status.CANNOT_CHECK,null,null);
		}
		JsonObject info = out.getAsJsonObject();
		if(info == null || info.get("status").getAsString().equals("err")) {
			return new LauncherUpdate(LauncherUpdate.Status.CANNOT_CHECK,null,null);
		}
		JsonObject obj = info.get("value").getAsJsonObject();
		Version latest = new Version(obj.get("latest").getAsString());
		String url = info.get("value").getAsJsonObject().get("url").getAsString();
		if(latest.compareTo(EndiLauncher.version) > 0) {
			return new LauncherUpdate(LauncherUpdate.Status.AVAILABLE,latest,url);
		}
		
		return new LauncherUpdate(LauncherUpdate.Status.LATEST,latest,url);
	}
	
	public static void init(Args args) {
		if(args.checkArg("forceupdate") != -1) {
			LauncherUpdate s= checkForLauncherUpdates();
			update(s,true);
			return;
		}
		LauncherUpdate s= checkForLauncherUpdates();
		if(args.checkArg("noupdate") != -1) {
			if(s.status == LauncherUpdate.Status.AVAILABLE) {
				Interface.printToConsole("");
				Interface.printToConsole("<font color=yellow>Dostępna jest nowa wersja launchera: "+s.version.get()+"</font>");
				Interface.printToConsole("<font color=yellow>Automatyczne aktualizacje są wyłączone.</font>");
			} else if(s.status == LauncherUpdate.Status.CANNOT_CHECK) {
				Interface.printToConsole("<font color=orange>Brak połaczenia z internetem. Nie można sprawdzić aktualizacji. </font>");
			}
		} else {
			if(s.status == LauncherUpdate.Status.AVAILABLE) {
				Interface.printToConsole("");
				Interface.printToConsole("<font color=yellow>Dostępna jest nowa wersja launchera: "+s.version.get()+"</font>");
				Interface.printToConsole("<font color=green>Automatyczna aktualizacja rozpoczeta.</font>");
				Interface.printToConsole("Pobieranie...");
				update(s,false);
			} else if(s.status == LauncherUpdate.Status.CANNOT_CHECK) {
				Interface.printToConsole("<font color=orange>Brak połaczenia z internetem. Nie można sprawdzić aktualizacji. </font>");
			}
		}
	}
	
	public static void update(LauncherUpdate upd,boolean forceUpdate) {
		try {
			Interface.switchCard("instalacja");
			Interface.setDownloadPanelVisible(true);
			
			File update = new File(EndiLauncher.workingDir,"update.jar");
			
			if(update.exists()) {
				update.delete();
			}
			
			Download down = new Download(new URL(upd.url),update.getAbsolutePath());
			
			down.addObserver("downloaded", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					/*int x[] = ((int[]) e.getNewValue());
					int processed = x[0];
					int length = x[1];
					Interface.getProgressBar().setValue((int) ((float) processed / (float) length * 100d));*/
					Interface.updateDownloadInfo(down);
				}
			});

			while (down.getStatus() == Download.DOWNLOADING) {
				down.waitt(10000);
			}
			
			if(down.getStatus() != Download.COMPLETE) {
				throw down.getException();
			}
			
			//Runtime.getRuntime().exec(Java.getJava().getAbsolutePath()+" -jar \""+update.getAbsolutePath()+"\" updateme \""+Utils.getJarFile()+"\"");
			//Runtime.getRuntime().exec(new String[] {Java.getJavaBinaryStr(),"-jar",update.getAbsolutePath(),"updateme",Utils.getJarFile()+""});
			Starter.startLauncherNohup(update, new String[] {"updateme",Utils.getJarFileF().getAbsolutePath()});
			System.exit(0);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void CupdateLauncher() throws IOException, URISyntaxException {
		//DiscordRichPresenceAPI.setState("W launcherze - aktualizuje launcher");
		//DiscordRichPresenceAPI.setStartTimestamp(System.currentTimeMillis());
		//DiscordRichPresenceAPI.update();
		
		Interface.printToConsole("<font color=yellow>Rozpoczęto aktualizację launchera.</font>");
		Interface.printToConsole("Pobieranie danych z serwera...");

		/*Interface.setDownloadPanelVisible(true);

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		String[] sv = Server.getLatestlauncherVersionAndLink();

		Interface.printToConsole("Pobieranie nowego launchera...");
		downloader = new Download(new URL(sv[1]), modpackfolder.getAbsolutePath() + "/update.jar");

		try {
			while (downloader.getStatus() != Download.COMPLETE && !Thread.interrupted()) {
				if (downloader.getStatus() == Download.DOWNLOADING) {
					Interface.getProgressBar().setValue((int) downloader.getProgressInPercent());
					String string = nf.format(downloader.getProgressInPercent()).replace("-", "");
					Interface.getLblPobieraniePobrano().setText("Pobrano: " + downloader.getDownloaded() / 1024 / 1024 + "/" + downloader.getSize() / 1024 / 1024 + "MB [" + string + "%]");
					Interface.getLblPobieraniePreskosc().setText("Prędkość: " + downloader.getFormattedSomethingPerSecond());
					Interface.getLblPobieranieCzas().setText("Pozostało: " + downloader.getTimeLeft());
					Thread.sleep(100L);
				} else if (downloader.getStatus() == Download.ERROR) {
					Interface.printToConsole("<font color=red>Podczas aktualizacji launchera wystąpił błąd.<br>Zrestartuj launcher.</font>");
					downloader.cancel();
					
					//DiscordRichPresenceAPI.setState("W launcherze");
					//DiscordRichPresenceAPI.setStartTimestamp(0);DiscordRichPresenceAPI.update();
					return;
				}
			}
		} catch (InterruptedException e) {
			downloader.cancel();
		}

		Interface.getProgressBar().setValue(100);*/

		Interface.printToConsole("Przygotowywanie patcha...");

		if (SystemInfo.getOS() == SystemInfo.WINDOWS) {
			File script = new File(EndiLauncher.workingDir, "updater.bat");
			if (!script.exists()) {
				script.createNewFile();
			}
			//String s = null;
			/*if (Interface.autoStartAfterUpdate) {
				s = "@echo off\r\n" + "chcp 65001\r\n" + "color 9b\r\n" + "mode con:cols=40 lines=15\r\n" + "echo Aktualizator launchera " + Interface.name + " v1.0\r\n" + "echo Nie zamykaj tego okna.\r\n" + "echo Oczekiwanie na zamkniecie launchera...\r\n" + "timeout 3 > NUL\r\n" + "echo Aktualizacja...\r\n" + "move \"" + modpackfolder.getAbsolutePath() + "\\update.jar" + "\" \"" + Utils.getJarFile() + "\"\r\n" + "echo Czyszczenie...\r\n" + "start /b javaw -jar " + Utils.getJarFile() + "\r\n" + "del \"%~f0\" & exit";
			} else {
				s = "@echo off\r\n" + "chcp 65001\r\n" + "color 9b\r\n" + "mode con:cols=40 lines=15\r\n" + "echo Aktualizator launchera " + Interface.name + " v1.0\r\n" + "echo Nie zamykaj tego okna.\r\n" + "echo Oczekiwanie na zamkniecie launchera...\r\n" + "timeout 3 > NUL\r\n" + "echo Aktualizacja...\r\n" + "move \"" + modpackfolder.getAbsolutePath() + "\\update.jar" + "\" \"" + Utils.getJarFile() + "\"\r\n" + "echo Czyszczenie...\r\n" + "msg * \"Pomyslnie zaktualizowano launcher z wersji " + Interface.wersja + " do wersji " + sv[0] + "\"\r\n" + "del \"%~f0\" & exit";
			}*/
			
			String scr = new String(Interface.class.getResourceAsStream("/files/scripts/update.bat").readAllBytes(),StandardCharsets.UTF_8);
			scr = scr.replace("[NAME]", Interface.name).replace("[DATADIR]", EndiLauncher.workingDir.getAbsolutePath()).replace("[LOCATION]", Utils.getJarFileF().getAbsolutePath());
			
			Utils.replaceFileContents(script, scr);
			//Runtime.getRuntime().exec("cmd /c start cmd.exe /c \"" + script.getAbsolutePath() + "&& exit\"", null, Updater.modpackfolder);
		}/* else if (SystemInfo.getOS() == SystemInfo.LINUX) {
			File script = new File(EndiLauncher.workingDir, "updater.sh");
			if (!script.exists()) {
				script.createNewFile();
			}
			String s = null;
			if (Interface.autoStartAfterUpdate) {
				s = "#!/bin/bash\n" 
						+ "echo Aktualizator launchera " 
						+ Interface.name 
						+ " v1.0\n" 
						+ "echo Nie zamykaj tego okna.\n" 
						+ "echo Oczekiwanie na zamkniecie launchera...\n" 
						+ "sleep 3\n" 
						+ "echo Aktualizacja...\n"
						+ "mv \"" + modpackfolder.getAbsolutePath() + "/update.jar" + "\" \"" + Utils.getJarFile() + "\"\n" 
						+ "echo Nadawanie uprawnień...\n"
						+ "chmod +x " + Utils.getJarFile()+"\n"
						+ "echo Uruchamianie launchera...\n" 
						+ "nohup java -jar " + Utils.getJarFile() + " >/dev/null 2>&1 &\n" 
						+ "sleep 1\n"
						+ "echo Czyszczenie..." 
						+ "rm -- \"$0\"\n" 
						+ "kill -9 $PPID\n";
			} else {
				s = "#!/bin/bash\n" 
						+ "echo Aktualizator launchera " + Interface.name + " v1.0\n" 
						+ "echo Nie zamykaj tego okna.\n" 
						+ "echo Oczekiwanie na zamkniecie launchera...\n" 
						+ "sleep 3\n" 
						+ "echo Aktualizacja...\n" 
						+ "mv \"" + modpackfolder.getAbsolutePath() + "/update.jar" + "\" \"" + Utils.getJarFile() + "\"\n" 
						+ "echo Czyszczenie...\n"
						+ "chmod +x " + Utils.getJarFile()+"\n" 
						+ "rm -- \"$0\"\n" 
						+ "kill -9 $PPID\n";

				if (SystemInfo.zenityPresent()) {
					s += "nohup zenity --no-wrap --info --text=\"Pomyślnie zaktualizowano launcher z wersji " + Interface.wersja + " do wersji " + sv[0] + "\" --title=\"Ukończono\" >/dev/null 2>&1 &\n" + "#rm -- \"$0\"\n" + "kill -9 $PPID\n";
				} else {
					s += "echo Pomyślnie zaktualizowano launcher\n" + "echo z wersji " + Interface.wersja + " do wersji " + sv[0] + "\n" + "read -p \"Naciśnij dowolny klawisz\"\n" + "#rm -- \"$0\"\n" + "kill -9 $PPID\n";
				}
			}
			Utils.replaceFileContents(script, s);
			// Runtime.getRuntime().exec("chmod 755 \""+script.getAbsolutePath()+"\"", null,
			// Updater.modpackfolder);
			Runtime.getRuntime().exec("chmod 755 " + script.getAbsolutePath(), null, Updater.modpackfolder);
			if (SystemInfo.getTerminal() == SystemInfo.XTERM) {
				Runtime.getRuntime().exec("xterm -xrm 'XTerm.vt100.allowTitleOps: false' -T Aktualizacja -e /bin/bash " + script.getAbsolutePath(), null, Updater.modpackfolder);
			} else if (SystemInfo.getTerminal() == SystemInfo.GNOMETERMINAL) {
				Runtime.getRuntime().exec("gnome-terminal --hide-menubar --geometry=40x10+500+400 -x /bin/bash " + script.getAbsolutePath(), null, Updater.modpackfolder);
			} else if (SystemInfo.getTerminal() == SystemInfo.LXTERMINAL) {
				Runtime.getRuntime().exec("lxterminal --title=Aktualizacja --geometry=40x10 -e /bin/bash " + script.getAbsolutePath(), null, Updater.modpackfolder);
			} else if (SystemInfo.getTerminal() == SystemInfo.XFCE4TERM) {
				Runtime.getRuntime().exec("xfce4-terminal --hide-menubar --hide-toolbar --title Aktualizacja --geometry 40x10 -e " + script.getAbsolutePath(), null, Updater.modpackfolder);
			} else if (SystemInfo.getTerminal() == SystemInfo.QTERMINAL) {
				Runtime.getRuntime().exec("qterminal --geometry 480x240+500+400 -e /bin/bash " + script.getAbsolutePath(), null, Updater.modpackfolder);
			} else {
				Runtime.getRuntime().exec("/bin/bash " + script.getAbsolutePath(), null, Updater.modpackfolder);
			}
		} else if (SystemInfo.getOS() == SystemInfo.MACOS) {
			File script = new File(modpackfolder + "/updater");
			if (!script.exists()) {
				script.createNewFile();
			}
			String s = null;
			if (Interface.autoStartAfterUpdate) {
				s = "echo Aktualizator launchera " + Interface.name + " v1.0\n" + "echo Nie zamykaj tego okna.\n" + "echo Oczekiwanie na zamkniecie launchera...\n" + "sleep 3\n" + "echo Aktualizacja...\n" + "mv \"" + modpackfolder.getAbsolutePath() + "/update.jar" + "\" \"" + Utils.getJarFile() + "\"\n" + "echo Czyszczenie...\n" + "nohup java -jar " + Utils.getJarFile() + " >/dev/null 2>&1 &\n" + "rm -- \"$0\"\n" + "osascript -e 'tell application \"Terminal\" to close first window' & exit\n";
			} else {
				s = "echo Aktualizator launchera " + Interface.name + " v1.0\n" + "echo Nie zamykaj tego okna.\n" + "echo Oczekiwanie na zamkniecie launchera...\n" + "sleep 3\n" + "echo Aktualizacja...\n" + "mv \"" + modpackfolder.getAbsolutePath() + "/update.jar" + "\" \"" + Utils.getJarFile() + "\"\n" + "echo Czyszczenie...\n" + "nohup osascript -e 'tell app \"System Events\" to display dialog \"Pomyślnie zaktualizowano launcher z wersji " + Interface.wersja + " do wersji " + sv[0] + "\"' >/dev/null 2>&1 &\n" + "#rm -- \"$0\"\n" + "osascript -e 'tell application \"Terminal\" to close first window' & exit\n";

			}
			Utils.replaceFileContents(script, s);
			Runtime.getRuntime().exec("chmod +x " + script.getAbsolutePath(), null, Updater.modpackfolder);
			Runtime.getRuntime().exec("open -a Terminal.app " + script.getAbsolutePath(), null, Updater.modpackfolder);
			// Runtime.getRuntime().exec("osascript -e 'tell application \"Terminal\" to do
			// script \"./"+script.getAbsolutePath()+"\"'", null, Updater.modpackfolder);
		} else {
			DialogUtil.showConfirmDialog("Autoaktualizacja w tym systemie nie jest jeszcze wspierana.\nZaktualizuj launcher ręcznie.\nMożesz nam pomóc i wysłać informacje o twoim systemie do administracji.\nPrzepraszamy za utrudnienia.\n");
		}*/
		System.exit(0);
	}
}
