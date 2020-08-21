package me.catzy44.tools;

import java.io.File;

import me.catzy44.utils.SystemInfo;
import me.catzy44.utils.Version;

public class Java {
	/*public static File getJava() {
		if(java_home == null) {
			search();
		}
		return javaW;
	}*/
	public static String getJavaBinaryStr() {
		if(SystemInfo.getOS() == SystemInfo.WINDOWS) {
			if(java_home == null) {
				search();
			}
			if(javaW == null) {
				return "javaw";//there is no java but launcher is running so there is jre. Maybe user added it to PATH?
			}
			return javaW.getAbsolutePath();
		}
		return "java";//no javaw on linux etc
	}
	
	private static File java_home = null;
	private static File javaW = null;
	private static File java = null;
	
	public static void search() {
		File container = new File("C:\\Program Files\\Java");
		if(!container.exists()) {
			return;
		}
		
		File latestF = null;
		Version latest = null;
		
		for(File f : container.listFiles()) {
			if(!f.isDirectory()) {
				continue;
			}
			String name = f.getName().replace("jre-", "").replace("jdk-", "");
			Version thisVer = new Version(name);
			if(latest == null || latest.compareTo(thisVer) == -1) {
				latest = thisVer;
				latestF = f;
			}
		}
		if(latest == null || latestF == null) {
			return;
		}
		
		java_home = latestF;
		javaW = new File(java_home,"bin/javaw.exe");
		java = new File(java_home,"bin/java.exe");
	}
}
