package me.catzy44.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Locale;

public class SystemInfo {
	public static final int WINDOWS = 0;
	public static final int LINUX = 1;
	public static final int MACOS = 2;
	public static final int OTHER = 3;
	
	public static final int MISSING = -1;
	public static final int QTERMINAL = 0;
	public static final int GNOMETERMINAL = 1;
	public static final int LXTERMINAL = 2;
	public static final int XFCE4TERM = 3;
	public static final int XTERM = 4;

	private static int os = -1;
	private static int memorySize = 0;
	private static int javaVersion = 0;
	private static int javaSubVersion = 0;
	private static int terminal = 0;
	private static boolean zenityPresent = false;

	public static void load() {
		String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
			os = 2;
		} else if (OS.indexOf("win") >= 0) {
			os = 0;
		} else if (OS.indexOf("nux") >= 0) {
			os = 1;
		} else {
			os = 3;
		}
		/*if (os == 2) {
			try {
				Process process = Runtime.getRuntime().exec("sysctl -n hw.memsize");
				BufferedReader systemInformationReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				long bytes = 0;
				while ((line = systemInformationReader.readLine()) != null) {
					bytes = Long.parseLong(line);
				}
				memorySize = (int) (bytes / 1024 / 1024);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (os == 0) {
			try {
				Process process = Runtime.getRuntime().exec("wmic MEMORYCHIP get Capacity");
				BufferedReader systemInformationReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				long bytes = 0;
				while ((line = systemInformationReader.readLine()) != null) {
					if (line != null && !line.equals("") && !line.contains("Capacity")) {
						bytes += Long.parseLong(line.replace(" ", ""));
					}
				}
				memorySize = (int) (bytes / 1024 / 1024);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (os == 1) {
			try {
				List<String> lines = Utils.readLinesFromFile(new File("/proc/meminfo"));
				for (String s : lines) {
					if (s.startsWith("MemTotal")) {
						memorySize = (int) (Long.parseLong(s.replace("MemTotal:", "").replace(" ", "").replace("kB", "")) / 1024);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		long bytes = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize();
		memorySize = (int) (bytes / 1024 / 1024);
		
		String version = System.getProperty("java.version");
	    if(version.startsWith("1.")) {
	    	javaSubVersion = Integer.parseInt(version.split("_")[1]);
	        version = version.substring(2, 3);
	    } else {
	    	javaSubVersion = Integer.parseInt(version.split("\\.")[version.split("\\.").length-1]);
	        int dot = version.indexOf(".");
	        if(dot != -1) { version = version.substring(0, dot); }
	    } 
	    javaVersion = Integer.parseInt(version);
	    
	    if(new File("/bin/qterminal").exists()) {
	    	terminal = QTERMINAL;
	    } else if(new File("/bin/gnome-terminal").exists()) {
	    	terminal = GNOMETERMINAL;
	    } else if(new File("/bin/lxterminal").exists()) {
	    	terminal = LXTERMINAL;
	    } else if(new File("/bin/xfce4-terminal").exists()) {
	    	terminal = XFCE4TERM;
	    } else if(new File("/bin/xterm").exists()) {
	    	terminal = XTERM;
	    } else {
	    	terminal = MISSING;
	    }
	    if(new File("/bin/zenity").exists()) {
	    	zenityPresent = true;
	    }
	}
	
	public static int getWindowsVersion() {
		return Integer.valueOf(System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).replace("windows ",""));
	}
	
	public static boolean isSystemRuntimeCompatibleWithGame() {
		if(javaVersion == 7 || javaVersion == 8) {
			return true;
		}
		return false;
	}

	public static int getOS() {
		return os;
	}

	public static long getMemorySize() {
		return memorySize;
	}
	
	public static int getJavaVersion() {
	    return javaVersion;
	}
	
	public static int getJavaSubVersion() {
	    return javaSubVersion;
	}
	
	public static int getTerminal() {
		return terminal;
	}
	
	public static boolean zenityPresent() {
		return zenityPresent;
	}
}
