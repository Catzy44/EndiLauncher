package me.catzy44.game.installers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.catzy44.tools.Download;
import me.catzy44.utils.Hash;
import me.catzy44.utils.Utils;

import me.catzy44.game.GameInstallation.Status;

public final class VanillaInstaller {
	private final InstallationContext context;

	public VanillaInstaller(InstallationContext context) {
		this.context = context;
	}

	public void install() throws Exception {
		context.setStatus(Status.INSTALLING_DOWNLOADING_LIBRARIES);
		new LibraryInstaller(context).install();
		context.setStatus(Status.INSTALLING_DOWNLOADING_ASSETS);
		installAssets();
		installClient();
	}
	private boolean installClient() throws Exception {
		context.setStatus(Status.INSTALLING_DOWNLOADING_CORE);
		
		JsonObject json = context.manifest.get("downloads").getAsJsonObject().get("client").getAsJsonObject();
		
		String url = json.get("url").getAsString();
		String sha1 = json.get("sha1").getAsString();
		
		File profile = new File(context.gameDir,"versions/"+context.ver.getMcVersion());
		Utils.mkDirs(profile);
		Utils.replaceFileContents(new File(profile,context.ver.getMcVersion()+".json"), context.manifest.toString());
		
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		File save = new File(profile,context.ver.getMcVersion()+".jar");
		
		if(save.exists() && save.isFile() && new Hash(save).equals(sha1)) {
			return true;
		}
		
		context.down = new Download(new URL(url), save.getAbsolutePath());

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
		
		return true;
	}
	private void installAssets() throws Exception {
		JsonObject assetIndex = context.manifest.get("assetIndex").getAsJsonObject();
		context.ver.setAssetsVersion(assetIndex.get("id").getAsString());
		String assetsIndexDownS = Utils.readEverythingFromURL(assetIndex.get("url").getAsString());
		JsonObject objects = context.gson.fromJson(assetsIndexDownS, JsonObject.class).get("objects").getAsJsonObject();
		{
			File assetIndexFile = new File(context.assetsDir, "/indexes/" + assetIndex.get("id").getAsString() + ".json");
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
			context.processingArtifact(entry.getKey());
			
			File assetFile = new File(context.assetsDir, "objects/" + assetName);
			File assetDir = assetFile.getParentFile();
			if (!assetDir.exists() || !assetDir.isDirectory()) {
				assetDir.mkdirs();
			}

			if (assetFile.exists() && new Hash(assetFile).toString().equals(hash)) {
				processed++;
				context.completedArtifact(processed, toProcess);
				continue;
			}
			
			if(Thread.interrupted()) {
				throw new InterruptedException();
			}

			String url = "https://resources.download.minecraft.net/" + assetName;
			context.down = new Download(new URL(url), assetFile.getAbsolutePath());
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
				if(context.down.getException() != null) {
					throw context.down.getException();
				}
				throw new InterruptedException("Download exception status: "+context.down.getStatus());
			}

			context.completedArtifact(processed, toProcess);
			processed++;
		}
	}
}
