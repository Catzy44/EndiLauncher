package me.catzy44.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import me.catzy44.EndiLauncher;
import me.catzy44.utils.Utils;

public class Logger {
	private static List<String> logCache = new ArrayList<String>();

	public static File getFile() {
		return new File(EndiLauncher.workingDir.getAbsolutePath() + "/logcache.txt");
	}

	public static void init() {
		try {
			Utils.replaceFileContents(getFile(), "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		try {
			/*FileWriter writer = new FileWriter(getFile(), true);
			PrintWriter pw = new PrintWriter(writer);*/
			
			FileOutputStream fos = new FileOutputStream(getFile());
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter writer = new BufferedWriter(osw);
            PrintWriter pw = new PrintWriter(writer);

			for (String f : logCache) {
				pw.println(f);
			}
			logCache.clear();

			pw.close();
			writer.close();
			osw.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void log(String s) {
		s = Utils.removeHTMLTagsFromString(s);
		logCache.add(s);
		if (logCache.size() > 50) {
			save();
		}
	}
}
