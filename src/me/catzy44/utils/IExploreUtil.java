package me.catzy44.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;

import me.catzy44.EndiLauncher;

public class IExploreUtil {
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
		
		closeIfPossible();
		//stop();
	}
	

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
		
		Utils.openUrl(url);
		return true;
	}
	
	private static HWND edgeWindow = null;
	private static Thread finder = null;
	private static final List<HWND> existingEdgeWindows = new ArrayList<HWND>();
	private static void startFinder() {
		existingEdgeWindows.clear();
		edgeWindow = null;
		
		u32.EnumWindows((window, data) -> {
            if (!u32.IsWindowVisible(window) || !isEdgeWindow(window)) {
                return true;
            }
            existingEdgeWindows.add(window);
            return true;
        }, null);
		
		finder = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(!Thread.interrupted()) {
						u32.EnumWindows((window, data) -> {
							if(edgeWindow != null) {
								return true;
							}
							if (!u32.IsWindowVisible(window) || !isEdgeWindow(window)) {
				                return true;
				            }
							if(existingEdgeWindows.contains(window)) {
								return true;
							}
			                edgeWindow = window;
			                finder.interrupt();
			                System.out.println("edge window found");
			                gotHandle();
			                return true;
			            }, null);
						Thread.sleep(200);
					}
				} catch (InterruptedException e) {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		finder.start();
	}
	
	public static void closeIfPossible() {
		System.out.println("closing browser");
		if(edgeWindow != null) {
			u32.CloseWindow(edgeWindow); //MINIMIZE window!
			u32.SendMessage(edgeWindow, WinUser.WM_CLOSE, new WPARAM(),new LPARAM()); //send destroy command
		}
		edgeWindow = null;
	}
	
	private static boolean goEdge(String url) {
		try {
			int width = 440;
			int height = 338 + 140 + 40;
			
			Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
			int posX = (int) scr.getWidth() / 2 - width / 2;
			int posY = (int) scr.getHeight() / 2 - height / 2;
			
			startFinder();
			
			Runtime.getRuntime().exec(new String[] {
					edgeBin.getAbsolutePath(),
					"--window-size=" + width + "," + height,
					"--window-position=" + posX + "," + posY,
					"--app=\"" + url + "\"",
					"--user-data-dir=\"" + EndiLauncher.getWorkingdir() + "/edgeUserDataDir\""
			});

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static void gotHandle() {
		HWND topmost = new HWND(Pointer.createConstant(-1L)); // HWND_TOPMOST, pointer-sized -1
		boolean success = u32.SetWindowPos(edgeWindow, topmost, 0, 0, 0, 0,
				WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE | WinUser.SWP_NOACTIVATE);
		if (!success) {
			int error = com.sun.jna.Native.getLastError();
			System.err.println("Cannot set Edge window topmost: Windows error " + error);
		}
	}
	
	
//	
//	private static Set<Long> windowHandles() {
//        Set<Long> handles = new HashSet<>();
//        u32.EnumWindows((window, data) -> {
//            handles.add(Pointer.nativeValue(window.getPointer()));
//            return true;
//        }, null);
//        return handles;
//    }

    private static boolean isEdgeWindow(HWND window) {
        IntByReference pid = new IntByReference();
        if (u32.GetWindowThreadProcessId(window, pid) == 0) return false;
        return ProcessHandle.of(Integer.toUnsignedLong(pid.getValue()))
                .flatMap(process -> process.info().command())
                .map(command -> new File(command).getName().equalsIgnoreCase("msedge.exe"))
                .orElse(false);
    }
}
