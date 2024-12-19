package me.catzy44.tools;

import java.io.File;
import java.io.IOException;

import me.catzy44.utils.Args;
import me.catzy44.utils.SystemInfo;
import me.catzy44.utils.Utils;

public class Starter {
	public static void check(Args args) {
		try {
			if (args.checkArg("properstart") != -1) {
				return;
			}
			if(Utils.getJarFileF() == null) {
				return;
			}
			startLauncherNohup(Utils.getJarFileF(),args.getAll());
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean startLauncherNohup(File jarFileLocation,String[] additionalLauncherFlags) {
		try {
			if (SystemInfo.getOS() == SystemInfo.WINDOWS) {
				Runtime.getRuntime().exec(Utils.joinArrays(new String[] { 
						Java.getJavaBinaryStr(), 
					"-Xms20M", 
					"-Xmx50M", 
					"-XX:MaxHeapFreeRatio=40", 
					"-XX:+UseParallelGC", 
					"-XX:ParallelGCThreads=2", 
					"-jar", 
					jarFileLocation.getAbsolutePath(),
					"properstart" 
					},additionalLauncherFlags));
			} else {
				Runtime.getRuntime().exec(Utils.joinArrays(new String[] { 
						"nohup",
						Java.getJavaBinaryStr(), 
						"-Xms20M", 
						"-Xmx50M", 
						"-XX:MaxHeapFreeRatio=40", 
						"-XX:+UseParallelGC", 
						"-XX:ParallelGCThreads=2", 
						"-Dawt.useSystemAAFontSettings=gasp", //font fix on linux
						"-jar", 
						jarFileLocation.getAbsolutePath(),
						"properstart" 
						},additionalLauncherFlags));
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
