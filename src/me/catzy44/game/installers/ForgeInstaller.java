package me.catzy44.game.installers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;

import me.catzy44.Interface;
import me.catzy44.tools.Download;
import me.catzy44.utils.Utils;

import me.catzy44.game.GameInstallation.Status;

public final class ForgeInstaller {
	private final InstallationContext context;

	public ForgeInstaller(InstallationContext context) {
		this.context = context;
	}

	public void install() throws Exception {
		downloadAndRunForgeInstaller();
		context.reloadManifest();
		context.setStatus(Status.INSTALLING_DOWNLOADING_FORGE_LIBRARIES);
		new LibraryInstaller(context).install();
		installForgeClientJar();
	}
	private boolean downloadAndRunForgeInstaller() throws Exception {
		context.setStatus(Status.INSTALLING_DOWNLOADING_FORGE);
		
		Interface.printToConsole("-------------------------------------------------");
		Interface.printToConsole("  Instalacja forge, to zajmie kilka minut...");
		Interface.printToConsole("-------------------------------------------------");
		
		Thread.sleep(1000);//give user a change to see message

		String forge = context.ver.getMcVersion() + "-" + context.ver.getForgeVersion();
		String filename = "forge-" + forge + "-installer.jar";
		
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		// download forge
		File forgeF = new File(context.gameDir.getAbsolutePath() + "/" + filename);
		context.down = new Download(new URL("https://maven.minecraftforge.net/net/minecraftforge/forge/" + forge + "/" + filename), forgeF.getAbsolutePath());

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

		// unpack installer
		File fnm = new File(context.gameDir, "ForgeNastyInstaller.jar");
		fnm.deleteOnExit();
		Utils.unpackFileFromJar("/files/other/ForgeNastyInstaller.jar", fnm);
		
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}

		context.setStatus(Status.INSTALLING_INSTALLING_FORGE);
		// run installer
		Process forgeInst = Runtime.getRuntime().exec("java -cp \"ForgeNastyInstaller.jar;" + filename + "\" me.catzy44.ForgeNastyInstaller \"" + context.gameDir.getAbsolutePath()+"\"", null, context.gameDir);
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
	private boolean installForgeClientJar() throws IOException {
		String forge = context.ver.getMcVersion() + "-" + context.ver.getForgeVersion();
		File from = new File(context.libsDir, "net/minecraftforge/forge/" + forge + "/forge-" + forge + "-client.jar");
		File coreFile = new File(context.gameDir,"/versions/"+context.ver.getProfileName()+"/"+context.ver.getProfileName()+".jar");
		Utils.deleteIfExists(coreFile);
		Files.copy(from.toPath(), coreFile.toPath());
		
		return true;
	}

}
