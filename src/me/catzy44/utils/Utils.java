package me.catzy44.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.Icon;

import me.catzy44.EndiLauncher;

public class Utils {
	public static void killProcessByPID(int pid) {
		try {
			if (SystemInfo.getOS() == SystemInfo.WINDOWS) {
				Runtime.getRuntime().exec(new String[] { "taskkill", "/f", "/pid", pid + "" });
			} else {
				Runtime.getRuntime().exec(new String[] { "kill", pid + "" });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static Thread hookPrinterToProcess(Process p) {
		Thread t = new Thread(()->{
			try {
				BufferedReader br = p.inputReader();
				String line;
				while ((line = br.readLine()) != null) {
					System.out.println("SUB/"+p.pid()+":"+line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		t.start();
		return t;
	}
	public static String[] joinArrays(String[] a, String[] b) {
		if(b == null) {
			return a;
		}
		String out[] = new String[a.length+b.length];
		for(int i = 0; i < a.length; i++) {
			out[i] = a[i];
		}
		for(int i = 0; i < b.length; i++) {
			out[i+a.length] = b[i];
		}
		return out;
	}
	public static void mkDirs(File f) {
		if(!f.exists() || !f.isDirectory()) {
			f.mkdirs();
		}
	}
	
	public static void deleteIfExists(File f) {
		if(f.exists()) {
			f.delete();
		}
	}
	public static void openUrl(String url) {
		
		try {
			Desktop desktop = java.awt.Desktop.getDesktop();

			URI oURL = new URI(url);
			desktop.browse(oURL);
		} catch (Exception e) {
			//e.printStackTrace();
			
			try {
				//Runtime.getRuntime().exec("start \""+url+"\"");
				Runtime.getRuntime().exec("cmd /c start \"\" \""+url+"\" && exit");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public static String removeHTMLTagsFromString(String str) {
		if(str == null) {return null;}
		return str.replaceAll("\\<.*?\\>", "");
	}

	public static File searchForJava() {
		File javahome = new File(System.getenv("ProgramFiles") + "/Java");
		if (!javahome.exists() || !javahome.isDirectory()) {
			return null;
		}
		// szukaj JRE 1.8.0_?
		List<File> javas = new ArrayList<File>();
		for (File f : javahome.listFiles()) {
			if (f.getName().startsWith("jre1.8.0_")) {
				javas.add(f);
			}
		}
		Collections.sort(javas, Collections.reverseOrder());
		for (File java : javas) {
			if (new File(java.getAbsolutePath() + "/bin/java.exe").exists()) {
				return java;
			}
		}
		// szukaj JDK 1.8.0_?
		javas.clear();
		for (File f : javahome.listFiles()) {
			if (f.getName().startsWith("jdk1.8.0_")) {
				javas.add(f);
			}
		}
		Collections.sort(javas, Collections.reverseOrder());
		for (File java : javas) {
			if (new File(java.getAbsolutePath() + "/jre/bin/java.exe").exists()) {
				return new File(java.getAbsolutePath() + "/jre");
			}
		}
		return null;
	}

	public static float map(float value, float start1, float stop1, float start2, float stop2) {
		float outgoing = start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
		String badness = null;
		if (outgoing != outgoing) {
			badness = "NaN (not a number)";

		} else if (outgoing == Float.NEGATIVE_INFINITY || outgoing == Float.POSITIVE_INFINITY) {
			badness = "infinity";
		}
		if (badness != null) {
			// final String msg = String.format("map(%s, %s, %s, %s, %s) called, which
			// returns %s", nf(value), nf(start1), nf(stop1), nf(start2), nf(stop2),
			// badness);
			// PGraphics.showWarning(msg);
		}
		return outgoing;
	}

	public static String getAppData() {
		String path = "";
		String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
			path = System.getProperty("user.home") + "/Library/";
		} else if (OS.indexOf("win") >= 0) {
			path = System.getenv("APPDATA");
		} else if (OS.indexOf("nux") >= 0) {
			path = System.getProperty("user.home");
		} else {
			path = System.getProperty("user.dir");
		}
		return path;
	}

	/*public static Path getJarFile() throws URISyntaxException, FileNotFoundException, UnsupportedEncodingException {
		// return new
		// java.io.File(EndiMcLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath()).toPath();
		String path = EndiLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		path = URLDecoder.decode(path, "UTF-8");
		return new java.io.File(path).toPath();
	}*/
	public static File getJarFileF() throws URISyntaxException, FileNotFoundException, UnsupportedEncodingException {
		// return new
		// java.io.File(EndiMcLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath()).toPath();
		String path = EndiLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		path = URLDecoder.decode(path, "UTF-8");
		File f = new File(path);
		if(!f.exists() || !f.getName().endsWith(".jar")) {
			return null;
		}
		return f;
	}

	public static void unzip(String zipFile, String destinationFolder) {
		File directory = new File(destinationFolder);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		byte[] buffer = new byte[1024];
		try {
			FileInputStream fInput = new FileInputStream(zipFile);
			ZipInputStream zipInput = new ZipInputStream(fInput);
			ZipEntry entry = zipInput.getNextEntry();
			while (entry != null) {
				String entryName = entry.getName();
				File file = new File(destinationFolder + File.separator + entryName);
				// System.out.println("Unzip file " + entryName + " to " +
				// file.getAbsolutePath());
				if (entry.isDirectory()) {
					File newDir = new File(file.getAbsolutePath());
					if (!newDir.exists()) {
						boolean success = newDir.mkdirs();
						if (!success) {
							System.out.println("Problem creating Folder");
						}
					}
				} else {
					FileOutputStream fOutput = new FileOutputStream(file);
					int count = 0;
					while ((count = zipInput.read(buffer)) > 0) {
						fOutput.write(buffer, 0, count);
					}
					fOutput.close();
				}
				zipInput.closeEntry();
				entry = zipInput.getNextEntry();
			}
			zipInput.closeEntry();
			zipInput.close();
			fInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyDirectory(File sourceLocation, File targetLocation) throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				// targetLocation.mkdir();
				targetLocation.mkdirs();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public static void unpackFileFromJar(String path, File targetLocation) throws IOException {
		InputStream in = EndiLauncher.getInstance().getClass().getResourceAsStream(path);
		OutputStream out = new FileOutputStream(targetLocation);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static void deleteDirectory(File dir) throws IOException {

		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				deleteDirectory(new File(dir, children[i]));
			}
			dir.delete();
		} else {
			dir.delete();
		}
	}

	public static String readEverythingFromFile(File f) throws IOException {
		FileReader reader = new FileReader(f);
		BufferedReader br = new BufferedReader(reader);

		String s = "";

		String line = "";
		while ((line = br.readLine()) != null) {
			s += line;
		}

		br.close();
		reader.close();

		return s;
	}

	public static List<String> readLinesFromFile(File f) throws IOException {
		FileReader reader = new FileReader(f);
		BufferedReader br = new BufferedReader(reader);

		String line;
		List<String> lines = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}

		br.close();
		reader.close();

		return lines;
	}

	public static String readEverythingFromURL(String s) throws IOException {
		URL url = new URL(s);
		URLConnection con = url.openConnection();
		con.setConnectTimeout(3000);
		con.setReadTimeout(3000);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

		String entirefile = "";

		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			entirefile += inputLine;
		}
		in.close();

		return entirefile;
	}

	public static void replaceFileContents(File f, String s) throws IOException {
		/*FileWriter writer = new FileWriter(f);
		writer.write(s);
		writer.close();*/
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
		writer.write(s);
		writer.close();
	}

	public static int calculatePasswordStrength(String password) {

		int iPasswordScore = 0;

		if (password.length() < 8)
			return 0;
		else if (password.length() >= 10)
			iPasswordScore += 2;
		else
			iPasswordScore += 1;

		/*
		 * if password contains 2 digits, add 2 to score. if contains 1 digit add 1 to
		 * score
		 */
		if (password.matches("(?=.*[0-9].*[0-9]).*"))
			iPasswordScore += 2;
		else if (password.matches("(?=.*[0-9]).*"))
			iPasswordScore += 1;

		// if password contains 1 lower case letter, add 2 to score
		if (password.matches("(?=.*[a-z]).*"))
			iPasswordScore += 2;

		/*
		 * if password contains 2 upper case letters, add 2 to score. if contains only 1
		 * then add 1 to score.
		 */
		if (password.matches("(?=.*[A-Z].*[A-Z]).*"))
			iPasswordScore += 2;
		else if (password.matches("(?=.*[A-Z]).*"))
			iPasswordScore += 1;

		/*
		 * if password contains 2 special characters, add 2 to score. if contains only 1
		 * special character then add 1 to score.
		 */
		if (password.matches("(?=.*[~!@#$%^&*()_-].*[~!@#$%^&*()_-]).*"))
			iPasswordScore += 2;
		else if (password.matches("(?=.*[~!@#$%^&*()_-]).*"))
			iPasswordScore += 1;

		return iPasswordScore;

	}

	public static void replaceInFile(File file, String replace, String with) {
		Path path = file.toPath();
		Charset charset = StandardCharsets.UTF_8;
		try {
			String content = new String(Files.readAllBytes(path), charset);
			content = content.replaceAll(replace, with);
			Files.write(path, content.getBytes(charset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage scaleBufferedImage(BufferedImage original, int w, int h) {
		BufferedImage resized = new BufferedImage(w, h, original.getType());
		Graphics2D g = resized.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(original, 0, 0, w, h, 0, 0, original.getWidth(), original.getHeight(), null);
		g.dispose();
		return resized;
	}

	public static BufferedImage generateTrustFactorImage(int w, int h, Color color, int trustPercentage) {
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Random rand = new Random(100);
		for (int i = 0; i < w; i++) {
			for (int k = 0; k < h; k++) {
				if (rand.nextInt(100) < trustPercentage) {
					img.setRGB(i, k, color.getRGB());
				}
			}
		}
		return img;
	}

	static Random rand = new Random();
	public static BufferedImage generateTrustFactorImageWithPartiallyPreview(int w, int h, Color color, int trPer, BufferedImage preview) {
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < w; i++) {
			for (int k = 0; k < h; k++) {
				if (rand.nextInt(100) < trPer) {
					if (new Color(preview.getRGB(i, k), true).getAlpha() != 0 && rand.nextInt(5) > 0) {
						img.setRGB(i, k, color.getRGB());// in line
					} else {
						// img.setRGB(i, k, color.getRGB());//out line
					}
				}
			}
		}
		rand = null;
		return img;
	}

	public static boolean stringArrayContains(String[] arr, String s) {
		for (String a : arr) {
			if (a.equals(s)) {
				return true;
			}
		}
		return false;
	}

	public static boolean stringArrayContainsKey(String[] arr, String s) {
		for (String a : arr) {
			if (a.startsWith(s)) {
				return true;
			}
		}
		return false;
	}

	public static String getValueFromStringArrayWithKey(String[] arr, String key) {
		for (String a : arr) {
			if (a.startsWith(key + ":")) {
				return a.substring(key.length() + 1);
			}
		}
		return null;
	}

	public static List<Component> getAllComponents(final Container c) {
		Component[] comps = c.getComponents();
		List<Component> compList = new ArrayList<Component>();
		for (Component comp : comps) {
			compList.add(comp);
			if (comp instanceof Container) {
				compList.addAll(getAllComponents((Container) comp));
			}
		}
		return compList;
	}

	public static BufferedImage iconToBufferedImage(Icon icon) {
		BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		return bi;
	}

	/*
	 * public static void main(String args[]) { Vector2D a = new Vector2D(1,1);
	 * Vector2D b = new Vector2D(2,2); Vector2D x =
	 * a.getAdded(b.getSubtracted(a).getNormalized().getMultiplied(3d));
	 * System.out.println(x.toString()); }
	 */
}
