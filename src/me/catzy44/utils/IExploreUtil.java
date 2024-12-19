package me.catzy44.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.COM.util.Factory;

import eu.doppel_helix.jna.tlb.shdocvw1.InternetExplorer;
import eu.doppel_helix.jna.tlb.shdocvw1.tagREADYSTATE;

public class IExploreUtil {
	private static Factory fact = new Factory();
	
	private static File edgeBin = new File("C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe");
	
	private static User32 u32 = User32.INSTANCE;
	//private static User32Ext u32x = User32Ext.INSTANCE;
	//private static Ole32 o32 = Ole32.INSTANCE;
	
	public static void main(String args[]) {
		String clientId = "c506971c-5daf-47e2-9f8a-0c79e3d5f5b0";
		String redUrl = "http://localhost:22420/login";
		String stage1Str = "https://login.live.com/oauth20_authorize.srf" + 
				"?client_id=" + 
				clientId + 
				"&response_type=code" +
				"&redirect_uri=" +
				redUrl + 
				"&scope=XboxLive.signin%20offline_access" + 
				"&state=123456" + 
				"&prompt=select_account";
		//start(stage1Str, true,1,true,true,440, 338+140+40,true);
		SystemInfo.load();
		start(stage1Str);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stop();
	}
	
	private static HWND hWnd;
	private static InternetExplorer ex;
	
	public static void stop() {
		System.out.println("closing browser");
		if(hWnd != null) {
			u32.CloseWindow(hWnd); //MINIMIZE window!
			u32.SendMessage(hWnd, WinUser.WM_CLOSE, new WPARAM(),new LPARAM()); //send destroy command
		}
		hWnd = null;
		if(ieInitiated) {
			tuneReg(false);
		}
	}
	
	private static boolean ieInitiated = false;
	public static boolean start(String url) {
		boolean edgeInstalled = (edgeBin.exists());
		System.out.println("WIN: "+SystemInfo.getWindowsVersion()+" edgeInstalled:"+(edgeInstalled ? "Yes" : "No"));

		if (SystemInfo.getOS() != SystemInfo.WINDOWS) {
			// open
			Utils.openUrl(url);
			return true;
		}
		if (SystemInfo.getWindowsVersion() == 11 && !edgeInstalled) {
			// open
			Utils.openUrl(url);
			return true;
		}
		if (edgeInstalled) {
			// edge
			if (!goEdge(url)) {
				Utils.openUrl(url);
			}
			return true;
		}
		
		if (!goIE(url)) {
			Utils.openUrl(url);
		}
		return true;
	}
	
	private static boolean goEdge(String url) {
		try {
			int width = 440;
			int height = 338 + 140 + 40;
			String command = "\"" + edgeBin.getAbsolutePath() + "\" --windows-size=" + width + "," + height + " --app=\"" + url + "\"";
			Runtime.getRuntime().exec(command);

			hWnd = null;
			long time = System.currentTimeMillis();
			while (hWnd == null && (System.currentTimeMillis() - time < 10000)) {
				hWnd = User32.INSTANCE.FindWindow(null, "Logowanie na koncie Microsoft");
				Thread.sleep(100);
			}
			if (hWnd == null) {
				return false;
			}

			boolean forcetop = true;
			HWND HWND_TOPMOST = new HWND(Pointer.createConstant(forcetop ? -1 : -2));
			Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
			u32.SetWindowPos(hWnd, HWND_TOPMOST, (int) scr.getWidth() / 2 - width / 2, (int) scr.getHeight() / 2 - height / 2, width, height, 0);

			u32.SetWindowLong(hWnd, WinUser.GWL_STYLE, u32.GetWindowLong(hWnd, WinUser.GWL_STYLE) & ~WinUser.WS_SIZEBOX);

			u32.ShowWindow(hWnd, 9); // SW_RESTORE
			u32.SetForegroundWindow(hWnd); // bring to front

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean goIE(String url) {
		try {
			boolean noui = true;
			int header = 1;
			boolean silent = true;
			boolean forcetop = true;
			int width = 440;
			int height = 338 + 140;
			boolean bringToFrontAfterStart = true;
			
			Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);
			
			tuneReg(true);
			ex = fact.createObject(InternetExplorer.class);
			
			if (noui) {
				ex.setTheaterMode(true);
				ex.setFullScreen(false);
				//x.setToolBar(0);
				// x.setStatusBar(false);
				ex.setAddressBar(false);
				// x.setMenuBar(false);
			}
			ex.setSilent(silent);
			
			
			ex.setWidth(width);
			ex.setHeight(height);
			
			ex.setVisible(true);
			
			hWnd = new WinDef.HWND(new Pointer(ex.getHWND()));
			
			//force on top
			HWND HWND_TOPMOST = new HWND(Pointer.createConstant(forcetop ? -1 : -2));
			//u32.SetWindowPos(hWnd, HWND_TOPMOST,  0, 0, 0, 0, WinUser.WS_POPUPWINDOW);
			Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
			u32.SetWindowPos(hWnd, HWND_TOPMOST, (int) scr.getWidth() / 2 - width / 2, (int) scr.getHeight() / 2 - height / 2, width, height, 0);
			
			//remove header
			//styles list: https://docs.microsoft.com/en-us/windows/win32/winmsg/window-styles
			if(header == 0) {
				u32.SetWindowLong(hWnd, WinUser.GWL_STYLE, WinUser.WS_BORDER);
			} else if(header == 1) {
				u32.SetWindowLong(hWnd, WinUser.GWL_STYLE, WinUser.WS_SYSMENU | WinUser.WS_CAPTION);
			}
			if(bringToFrontAfterStart) {
				u32.ShowWindow(hWnd, 9); // SW_RESTORE
	        	u32.SetForegroundWindow(hWnd); // bring to front
			}
			
			ex.Navigate(url, null, null, null, null);
			
			while(ex.getReadyState() != tagREADYSTATE.READYSTATE_COMPLETE) {
				Thread.sleep(100);
			}
			Thread.sleep(1000);
			
			ex.Refresh();
			
			return true;
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return false;
	}
	public static void tuneReg(boolean en) {
		ieInitiated = true;
		//https://admx.help/?Category=InternetExplorer&Policy=Microsoft.Policies.InternetExplorer::IZ_PolicyWindowsRestrictionsURLaction_1
		//https://serverfault.com/questions/436837/internet-explorer-9-title-bar-the-url-of-the-page-is-display-before-page-title
		//FIX FOR URL ON EXPLORER HEADER INSTEAD OF TITLE
		
		if (en) {
			Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\3", "2102", 0);
			Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "DisablePasswordCaching", 1);
			Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Internet Explorer\\Main", "Window Title", "Launcher Endi");
			
			Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Internet Explorer\\Main", "Enable Browser Extensions", 0);
		} else {
			Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Internet Explorer\\Main", "Window Title");
			Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "DisablePasswordCaching");
			Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\3", "2102", 3);

			Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\Microsoft\\Internet Explorer\\Main", "Enable Browser Extensions", 1);
		}
	}
}
