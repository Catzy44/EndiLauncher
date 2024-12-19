package me.catzy44.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import me.catzy44.EndiLauncher;
import me.catzy44.utils.DialogUtil;
import me.catzy44.utils.RandomString;
import me.catzy44.utils.Utils;

public class Config {
	private static final File ver = new File(EndiLauncher.workingDir, "launcher.json");
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static JsonObject config = null;
	
	public static boolean askForLicence() {
		//System.out.println(EndiMcLauncher.ralewayBold.canDisplayUpTo("d"));
		if(DialogUtil.showYesNoDialog("Aby przejść dalej musisz zaakceptować licencję:\n\n"
				+ "<span style=\"font-family:Arial;font-size:10px;\">●</span> Produkt jest licencjonowany bezpłatnie. Z tego powodu nie jest objęty\n"
				+ "ŻADNĄ GWARANCJĄ. Posiadacze praw autorskich czy też inne strony\n"
				+ "rozpowszechniają program w stanie takim jak jest bez JAKIEJKOLWIEK\n"
				+ " GWARANCJI, ani wyraźnej, ani domyślnej, o ile nie zaznaczono inaczej.\n\n"
				+ "<span style=\"font-family:Arial;font-size:10px;\">●</span> Posiadacz praw autorskich ani strona rozprowadzająca pracę NIE SĄ\n"
				+ "odpowiedzialne za ŻADNE SZKODY wynikłe z użytkowania pracy opartej\n"
				+ "o tą licencję, nawet jeśli użytkownik został poinformowany o możliwości\n"
				+ "wystąpienia takich szkód.\n","Akceptuję","Nie akceptuję") == 0) {
			return true;
		}
		return false;
	}

	public static File getConfigFile() {
		return ver;
	}

	public static void init() throws IOException {
		
		if(!EndiLauncher.workingDir.exists() || !EndiLauncher.workingDir.isDirectory()) {
			EndiLauncher.workingDir.mkdirs();
		}
		
		createReadme();
		
		if (!ver.exists()) {
			ver.createNewFile();
			config = new JsonObject();
			save();
		}
		try {
			config = gson.fromJson(Utils.readEverythingFromFile(ver), JsonObject.class);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("recreated config because of above stacktrace!");
			ver.delete();
			ver.createNewFile();
			config = new JsonObject();
			save();
			config = gson.fromJson(Utils.readEverythingFromFile(ver), JsonObject.class);
		}
		RandomString rstr = new RandomString(32,new Random(),RandomString.lower+RandomString.digits);
		config.addProperty("uuid", rstr.nextString());
		
		if(!(config.get("licenceAccepted") != null && config.get("licenceAccepted").getAsBoolean())) {
			if(!askForLicence()) {
				System.exit(0);
			}
			config.addProperty("licenceAccepted",true);
		}
		
		save();
	}
	
	public static void save() {
		try {
			Utils.replaceFileContents(ver, gson.toJson(config));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createReadme() {
		File readme = new File(EndiLauncher.workingDir, "/czytaj_to.txt");
		if (!readme.exists()) {
			try {
				Files.copy(EndiLauncher.class.getResourceAsStream("/files/other/readme.txt"), readme.toPath());
			} catch (IOException e) {
			}
		}
	}

	public static JsonObject getConfig() {
		return config;
	}
}
