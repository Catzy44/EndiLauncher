package me.catzy44.game.installers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import me.catzy44.tools.Download;
import me.catzy44.utils.Utils;

import me.catzy44.game.GameInstallation.Status;
import me.catzy44.game.FabricAPI;
import me.catzy44.game.Sodium;

public final class FabricInstaller {
	private final InstallationContext context;

	public FabricInstaller(InstallationContext context) {
		this.context = context;
	}

	public void install() throws Exception {
		downloadAndRunFabricInstaller();
		context.reloadManifest();
		context.setStatus(Status.INSTALLING_DOWNLOADING_FABRIC_LIBRARIES);
		new LibraryInstaller(context).install();
		installFabricAddons();
	}
	private boolean downloadAndRunFabricInstaller() throws Exception {
		context.setStatus(Status.INSTALLING_DOWNLOADING_FABRIC);
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
        
        File fabricF = new File(context.gameDir.getAbsolutePath() + "/" + filename);
        context.down = new Download(new URL(url), fabricF.getAbsolutePath());
        fabricF.deleteOnExit();

		context.down.addObserver("downloaded", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				context.pcs.firePropertyChange("downloaded", e.getOldValue(), e.getNewValue());
			}
		});

		while (context.down.getStatus() == Download.DOWNLOADING) {
			context.down.waitt(10000);
		}
		
		if(context.down.getStatus() != Download.COMPLETE) {
			throw context.down.getException();
		}
		
		context.setStatus(Status.INSTALLING_INSTALLING_FABRIC);
		
		if(Thread.interrupted()) {
			throw new InterruptedException();
		}
		
		Process fabricInst = Runtime.getRuntime().exec("java -jar "+filename+" client -dir \""+context.gameDir.getAbsolutePath()+"\" -mcversion "+context.ver.getMcVersion()+" -noprofile", null, context.gameDir);
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
				context.ver.setFabricVersion(fVer);
				System.out.println("installed fabric "+fVer);
			}
		}
		
		Utils.deleteIfExists(fabricF);
		
        return status;
	}
	private void installFabricAddons() throws Exception {
		if (context.installation.isInstallSodium()) {
			context.setStatus(Status.INSTALLING_SODIUM);
			Sodium.installSodium(context.installation);
		}
		if (context.installation.isInstallFabricAPI()) {
			context.setStatus(Status.INSTALLING_FABRIC_API);
			FabricAPI.installFabricAPI(context.installation);
		}
	}
}
