package me.catzy44.game.installers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import me.catzy44.tools.Download;
import me.catzy44.tools.Unzip;
import me.catzy44.utils.Hash;
import me.catzy44.utils.Utils;

import me.catzy44.game.GameInstallation.Status;

public final class LibraryInstaller {
	private final InstallationContext context;

	public LibraryInstaller(InstallationContext context) {
		this.context = context;
	}

	public void install() throws Exception {
		JsonArray libs = context.manifest.get("libraries").getAsJsonArray();
		context.classpath = "";
		libs:
		for (int processed = 0; processed < libs.size() && !Thread.interrupted(); processed++) {
			context.status = Status.INSTALLING_DOWNLOADING_LIBRARIES;
			JsonObject lib = libs.get(processed).getAsJsonObject();

			String name = lib.get("name").getAsString();
			context.processingArtifact(name);
			
			if (!context.shouldIncludeArgument(lib, new HashMap<String, Boolean>())) {
				context.completedArtifact(processed, libs.size());
				continue;
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
				File saveLocation = new File(context.libsDir,path+"/"+fname+"/"+version+"/"+filename);
				
				Utils.mkDirs(saveLocation.getParentFile());
				
				context.down = new Download(new URL(aurl), saveLocation.getAbsolutePath());
				context.down.addObserver("downloaded", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						context.pcs.firePropertyChange("downloaded", e.getOldValue(), e.getNewValue());
					}
				});
				/*while (context.down.getStatus() == Download.DOWNLOADING) {
					context.down.waitt(10000);
				}*/
				context.down.locker.waitForUnlock();
				if(context.down.getStatus() != Download.COMPLETE) {
					throw context.down.getException();
				}
				
				context.classpath += saveLocation.getAbsolutePath()+";";
				
				context.completedArtifact(processed, libs.size());
				continue;
			}
			
			if(lib.get("downloads") == null) {
				continue;
			}
			if (lib.get("downloads").getAsJsonObject().get("artifact") != null) {

				JsonObject art = lib.get("downloads").getAsJsonObject().get("artifact").getAsJsonObject();
				String hash = art.get("sha1").getAsString();
				xx: {
					File saveLocation = new File(context.libsDir, art.get("path").getAsString());

					File parentSaveDir = saveLocation.getParentFile();
					if (!parentSaveDir.exists() || !parentSaveDir.isDirectory()) {
						parentSaveDir.mkdirs();
					}

					context.classpath += saveLocation.getAbsolutePath() + ";";

					if (saveLocation.exists() && new Hash(saveLocation).toString().equals(hash)) {
						break xx;
					}
					context.down = new Download(new URL(art.get("url").getAsString()), saveLocation.getAbsolutePath());
					context.down.addObserver("downloaded", new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent e) {
							context.pcs.firePropertyChange("downloaded", e.getOldValue(), e.getNewValue());
						}
					});
					/*
					 * while (context.down.getStatus() == Download.DOWNLOADING) { context.down.waitt(10000); }
					 */
					context.down.locker.waitForUnlock();
					if (context.down.getStatus() != Download.COMPLETE) {
						if(context.down.getException() == null) {
							throw new InterruptedException("Download exception status "+context.down.getStatus());
						}
						throw context.down.getException();
					}
				}
				if (lib.get("downloads").getAsJsonObject().get("classifiers") == null) {
					context.completedArtifact(processed, libs.size());
					continue;
				}
			}
			
			if(lib.get("downloads").getAsJsonObject().get("classifiers").getAsJsonObject().get("natives-"+context.osName) == null) {
				//System.out.println(lib.get("downloads").getAsJsonObject().get("classifiers"));
				//TODO ??
				context.completedArtifact(processed, libs.size());
				continue;
			}
			
			JsonObject classifiers = lib.get("downloads").getAsJsonObject().get("classifiers").getAsJsonObject().get("natives-"+context.osName).getAsJsonObject();
			xx:
			{
				File saveLocation = new File(context.libsDir, classifiers.get("path").getAsString());
				String chash = classifiers.get("sha1").getAsString();
				
				File parentSaveDir = saveLocation.getParentFile();
				if(!parentSaveDir.exists() || !parentSaveDir.isDirectory()) {
					parentSaveDir.mkdirs();
				}
				if(saveLocation.exists() && new Hash(saveLocation).toString().equals(chash)) {
					break xx;
				}
				
				context.down = new Download(new URL(classifiers.get("url").getAsString()), saveLocation.getAbsolutePath());
				context.down.addObserver("downloaded", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						context.pcs.firePropertyChange("downloaded", e.getOldValue(), e.getNewValue());
					}
				});
				/*while (context.down.getStatus() == Download.DOWNLOADING) {
					context.down.waitt(10000);
				}*/
				context.down.locker.waitForUnlock();
				if(context.down.getStatus() != Download.COMPLETE) {
					throw context.down.getException();
				}
			}
			//String[] exc = new String[0];
			//String regex = "(META-INF\\/|.*\\.git|.*\\.sha1";
			context.status = Status.INSTALLING_UNZIPPING_LIBRARY;
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
			
			File file = new File(context.libsDir,extName[0].replace(".", "/")+"/"+extName[1]+"/"+extName[2]+"/"+extName[1]+"-"+extName[2]+"-natives-"+context.osName+".jar");
			context.unzip = new Unzip();
			context.unzip.addObserver("processedEntry", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					context.pcs.firePropertyChange("processedEntry", e.getOldValue(), e.getNewValue());
				}
			});
			context.unzip.unzip(file, context.natDir, regex);
			while (context.unzip.getStatus() == Unzip.PROCESSING) {
				context.unzip.waitt(10000);
			}
			context.completedArtifact(processed, libs.size());
		}
	}
}
