package me.catzy44.utils;

import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HBRUSH;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.INT_PTR;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.WINDOWPLACEMENT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface User32Ext extends StdCallLibrary {

	public static final User32Ext INSTANCE = Native.load("user32", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);

	public interface Accent {
		public static final int ACCENT_DISABLED = 0;
		public static final int ACCENT_ENABLE_GRADIENT = 1;
		public static final int ACCENT_ENABLE_TRANSPARENTGRADIENT = 2;
		public static final int ACCENT_ENABLE_BLURBEHIND = 3;
		public static final int ACCENT_ENABLE_ACRYLIC = 4; // YES, available on build 17063
		public static final int ACCENT_INVALID_STATE = 5;
	}

	public interface SPI {
		public static final int SPI_SETWORKAREA = 0x002F;
		public static final int SPI_GETWORKAREA = 0x0030;
		public static final int SPI_GETMINIMIZEDMETRICS = 0x002B;
		public static final int SPI_SETMINIMIZEDMETRICS = 0x002C;
		public static final int SPI_GETDESKWALLPAPER = 0x73;
	}

	public interface HSHELL {
		public static final int HSHELL_WINDOWCREATED = 1;
		public static final int HSHELL_WINDOWDESTROYED = 2;
		public static final int HSHELL_ACTIVATESHELLWINDOW = 3;
		public static final int HSHELL_WINDOWACTIVATED = 32772;
		public static final int HSHELL_GETMINRECT = 5;
		public static final int HSHELL_REDRAW = 6;
		public static final int HSHELL_TASKMAN = 7;
		public static final int HSHELL_LANGUAGE = 8;
		public static final int HSHELL_ACCESSIBILITYSTATE = 11;
		public static final int HSHELL_APPCOMMAND = 12;
		public static final int HSHELL_HIGHBIT = 0x8000;
		public static final int HSHELL_FLASH = (HSHELL_REDRAW | HSHELL_HIGHBIT);
		public static final int HSHELL_WINDOWFULLSCREEN = 53;
		public static final int HSHELL_WINDOWNORMAL = 54;
	}

	public interface ARW {
		public static final int ARW_BOTTOMLEFT = 0x0000;
		public static final int ARW_HIDE = 0x0008;
	}

	public interface WindowCompositionAttribute {
		public static final int WCA_ACCENT_POLICY = 19;
	}

	public interface AccentFlags {
		public static final int DrawLeftBorder = 0x20;
		public static final int DrawTopBorder = 0x40;
		public static final int DrawRightBorder = 0x80;
		public static final int DrawBottomBorder = 0x100;
		public static final int DrawAllBorders = (DrawLeftBorder | DrawTopBorder | DrawRightBorder | DrawBottomBorder);
	}

	public interface NIM {
		public static final int NIM_ADD = 0x00000000;
		public static final int NIM_MODIFY = 0x00000001;
		public static final int NIM_DELETE = 0x00000002;
	}

	public interface NIS {
		public static final int Visible = 0x00;
		public static final int NIS_HIDDEN = 0x01;
		public static final int NIS_SHAREDICON = 0x02;
	}

	public interface NIF {
		public static final int NIF_MESSAGE = 0x01;
		public static final int NIF_ICON = 0x02;
		public static final int NIF_TIP = 0x04;
		public static final int NIF_STATE = 0x08;
		public static final int NIF_INFO = 0x10;
		public static final int NIF_GUID = 0x20;
		public static final int NIF_REALTIME = 0x40;
		public static final int NIF_SHOWTIP = 0x80;
	}

	public class AccentPolicy extends Structure implements Structure.ByReference {
		public static final List<String> FIELDS = createFieldsOrder("AccentState", "AccentFlags", "GradientColor",
				"AnimationId");
		public int AccentState;
		public int AccentFlags;
		public int GradientColor;
		public int AnimationId;

		@Override
		protected List<String> getFieldOrder() {
			return FIELDS;
		}
	}

	public class WindowCompositionAttributeData extends Structure implements Structure.ByReference {
		public static final List<String> FIELDS = createFieldsOrder("Attribute", "Data", "SizeOfData");
		public int Attribute;
		public Pointer Data;
		public int SizeOfData;

		@Override
		protected List<String> getFieldOrder() {
			return FIELDS;
		}
	}

	public class MINIMIZEDMETRICS extends Structure implements Structure.ByReference {
		public static final List<String> FIELDS = createFieldsOrder("cbSize", "iWidth", "iHorzGap", "iVertGap",
				"iArrange");
		public int cbSize;
		public int iWidth;
		public int iHorzGap;
		public int iVertGap;
		public int iArrange;

		@Override
		protected List<String> getFieldOrder() {
			return FIELDS;
		}
	}

	public class RECTSHORT extends Structure {
		public static final List<String> FIELDS = createFieldsOrder("left", "top", "right", "bottom");
		public short left;
		public short top;
		public short right;
		public short bottom;

		@Override
		protected List<String> getFieldOrder() {
			return FIELDS;
		}

		@Override
		public String toString() {
			return "[(" + left + "," + top + ")(" + right + "," + bottom + ")]";
		}
	}

	public class SHELLHOOKINFO extends Structure implements Structure.ByReference {
		public static final List<String> FIELDS = createFieldsOrder("hWnd", "rc");
		public HWND hWnd;
		public RECTSHORT rc;

		public SHELLHOOKINFO(Pointer pointer) {
			super(pointer);
			read();
		}

		@Override
		protected List<String> getFieldOrder() {
			return FIELDS;
		}
	}

	public class COPYDATASTRUCT extends Structure implements Structure.ByReference {
		public static final List<String> FIELDS = createFieldsOrder("dwData", "cbData", "lpData");

		public COPYDATASTRUCT(Pointer pointer) {
			super(pointer);
			read();
		}

		public INT_PTR dwData;
		public int cbData;
		public Pointer lpData;

		@Override
		protected List<String> getFieldOrder() {
			return FIELDS;
		}

	}

	public class NOTIFYICONDATA extends Structure implements Structure.ByValue {
		public static final List<String> FIELDS = createFieldsOrder("cbSize", "hWnd", "uID", "uFlags",
				"uCallbackMessage", "hIcon", "szTip", "dwState", "dwStateMask", "szInfo", "uTimeout", "szInfoTitle",
				"dwInfoFlags", "guidItem", "hBalloonIcon");

		public DWORD cbSize;
		public HWND hWnd;
		public UINT uID;
		public UINT uFlags;
		public UINT uCallbackMessage;
		public HICON hIcon;
		public byte[] szTip = new byte[64];
		public DWORD dwState;
		public DWORD dwStateMask;
		public byte[] szInfo = new byte[256];
		public UINT uTimeout;
		public byte[] szInfoTitle = new byte[64];
		public DWORD dwInfoFlags;
		public GUID guidItem;
		public HICON hBalloonIcon;

		@Override
		protected List<String> getFieldOrder() {
			return FIELDS;
		}
	}

	public class SHELLTRAYDATA extends Structure implements Structure.ByReference {
		public static final List<String> FIELDS = createFieldsOrder("dwHz", "dwMessage", "nicon_data");

		public SHELLTRAYDATA(Pointer pointer) {
			super(pointer);
			read();
		}

		public int dwHz;
		public int dwMessage;
		public NOTIFYICONDATA nicon_data;

		@Override
		protected List<String> getFieldOrder() {
			return FIELDS;
		}

	}

	public class PAINTSTRUCT extends Structure implements Structure.ByReference {
		public static final List<String> FIELDS = createFieldsOrder("hdc", "fErase", "rcPaint", "fRestore",
				"fIncUpdate", "rgbReserved");

		public PAINTSTRUCT() {
			super();
		}

		public PAINTSTRUCT(Pointer pointer) {
			super(pointer);
			read();
		}

		public HDC hdc;
		public boolean fErase;
		public RECT rcPaint;
		public boolean fRestore;
		public boolean fIncUpdate;
		public byte rgbReserved[] = new byte[32];

		@Override
		protected List<String> getFieldOrder() {
			return FIELDS;
		}

	}

	public static final int MAX_PATH = 260;

	public static final int KEYEVENTF_KEYDOWN = 0;
	public static final int KEYEVENTF_EXTENDEDKEY = 1;
	public static final int KEYEVENTF_KEYUP = 2;

	public static final int VK_A = 0x41;
	public static final int VK_D = 0x44;
	public static final int VK_E = 0x45;
	public static final int VK_S = 0x53;

	public static final int WS_OVERLAPPED = 0x0, WS_POPUP = 0x80000000, WS_CHILD = 0x40000000, WS_MINIMIZE = 0x20000000,
			WS_VISIBLE = 0x10000000, WS_DISABLED = 0x8000000, WS_CLIPSIBLINGS = 0x4000000, WS_CLIPCHILDREN = 0x2000000,
			WS_MAXIMIZE = 0x1000000, WS_CAPTION = 0xC00000, WS_BORDER = 0x800000, WS_DLGFRAME = 0x400000,
			WS_VSCROLL = 0x200000, WS_HSCROLL = 0x100000, WS_SYSMENU = 0x80000, WS_THICKFRAME = 0x40000,
			WS_GROUP = 0x20000, WS_TABSTOP = 0x10000, WS_MINIMIZEBOX = 0x20000, WS_MAXIMIZEBOX = 0x10000,
			WS_OVERLAPPEDWINDOW = WS_OVERLAPPED | WS_CAPTION | WS_SYSMENU | WS_THICKFRAME | WS_MINIMIZEBOX
					| WS_MAXIMIZEBOX,
			WS_POPUPWINDOW = WS_POPUP | WS_BORDER | WS_SYSMENU, WS_CHILDWINDOW = WS_CHILD, WS_TILED = WS_OVERLAPPED,
			WS_ICONIC = WS_MINIMIZE, WS_SIZEBOX = WS_THICKFRAME, WS_TILEDWINDOW = WS_OVERLAPPEDWINDOW;

	public static final int WS_EX_DLGMODALFRAME = 0x1, WS_EX_NOPARENTNOTIFY = 0x4, WS_EX_TOPMOST = 0x8,
			WS_EX_ACCEPTFILES = 0x10, WS_EX_TRANSPARENT = 0x20, WS_EX_MDICHILD = 0x40, WS_EX_TOOLWINDOW = 0x80,
			WS_EX_WINDOWEDGE = 0x100, WS_EX_CLIENTEDGE = 0x200, WS_EX_CONTEXTHELP = 0x400, WS_EX_RIGHT = 0x1000,
			WS_EX_LEFT = 0x0, WS_EX_RTLREADING = 0x2000, WS_EX_LTRREADING = 0x0, WS_EX_LEFTSCROLLBAR = 0x4000,
			WS_EX_RIGHTSCROLLBAR = 0x0, WS_EX_CONTROLPARENT = 0x10000, WS_EX_STATICEDGE = 0x20000,
			WS_EX_APPWINDOW = 0x40000, WS_EX_OVERLAPPEDWINDOW = WS_EX_WINDOWEDGE | WS_EX_CLIENTEDGE,
			WS_EX_PALETTEWINDOW = WS_EX_WINDOWEDGE | WS_EX_TOOLWINDOW | WS_EX_TOPMOST, WS_EX_LAYERED = 0x80000,
			WS_EX_NOINHERITLAYOUT = 0x100000, WS_EX_LAYOUTRTL = 0x400000, WS_EX_COMPOSITED = 0x2000000,
			WS_EX_NOACTIVATE = 0x8000000;

	public static final int CW_USEDEFAULT = 0x80000000;

	public static final int CS_VREDRAW = 0x1, CS_HREDRAW = 0x2, CS_DBLCLKS = 0x8, CS_OWNDC = 0x20, CS_CLASSDC = 0x40,
			CS_PARENTDC = 0x80, CS_NOCLOSE = 0x200, CS_SAVEBITS = 0x800, CS_BYTEALIGNCLIENT = 0x1000,
			CS_BYTEALIGNWINDOW = 0x2000, CS_GLOBALCLASS = 0x4000, CS_IME = 0x10000, CS_DROPSHADOW = 0x20000;

	public static final int WM_NULL = 0x0, WM_CREATE = 0x1, WM_DESTROY = 0x2, WM_MOVE = 0x3, WM_SIZE = 0x5,
			WM_ACTIVATE = 0x6, WM_SETFOCUS = 0x7, WM_KILLFOCUS = 0x8, WM_ENABLE = 0xA, WM_SETREDRAW = 0xB,
			WM_SETTEXT = 0xC, WM_GETTEXT = 0xD, WM_GETTEXTLENGTH = 0xE, WM_PAINT = 0xF, WM_CLOSE = 0x10,
			WM_QUERYENDSESSION = 0x11, WM_QUERYOPEN = 0x13, WM_ENDSESSION = 0x16, WM_QUIT = 0x12, WM_ERASEBKGND = 0x14,
			WM_SYSCOLORCHANGE = 0x15, WM_SHOWWINDOW = 0x18, WM_WININICHANGE = 0x1A, WM_SETTINGCHANGE = WM_WININICHANGE,
			WM_DEVMODECHANGE = 0x1B, WM_ACTIVATEAPP = 0x1C, WM_FONTCHANGE = 0x1D, WM_TIMECHANGE = 0x1E,
			WM_CANCELMODE = 0x1F, WM_SETCURSOR = 0x20, WM_MOUSEACTIVATE = 0x21, WM_CHILDACTIVATE = 0x22,
			WM_QUEUESYNC = 0x23, WM_GETMINMAXINFO = 0x24, WM_PAINTICON = 0x26, WM_ICONERASEBKGND = 0x27,
			WM_NEXTDLGCTL = 0x28, WM_SPOOLERSTATUS = 0x2A, WM_DRAWITEM = 0x2B, WM_MEASUREITEM = 0x2C,
			WM_DELETEITEM = 0x2D, WM_VKEYTOITEM = 0x2E, WM_CHARTOITEM = 0x2F, WM_SETFONT = 0x30, WM_GETFONT = 0x31,
			WM_SETHOTKEY = 0x32, WM_GETHOTKEY = 0x33, WM_QUERYDRAGICON = 0x37, WM_COMPAREITEM = 0x39,
			WM_GETOBJECT = 0x3D, WM_COMPACTING = 0x41, WM_COMMNOTIFY = 0x44, WM_WINDOWPOSCHANGING = 0x46,
			WM_WINDOWPOSCHANGED = 0x47, WM_POWER = 0x48, WM_COPYDATA = 0x4A, WM_CANCELJOURNAL = 0x4B, WM_NOTIFY = 0x4E,
			WM_INPUTLANGCHANGEREQUEST = 0x50, WM_INPUTLANGCHANGE = 0x51, WM_TCARD = 0x52, WM_HELP = 0x53,
			WM_USERCHANGED = 0x54, WM_NOTIFYFORMAT = 0x55, WM_CONTEXTMENU = 0x7B, WM_STYLECHANGING = 0x7C,
			WM_STYLECHANGED = 0x7D, WM_DISPLAYCHANGE = 0x7E, WM_GETICON = 0x7F, WM_SETICON = 0x80, WM_NCCREATE = 0x81,
			WM_NCDESTROY = 0x82, WM_NCCALCSIZE = 0x83, WM_NCHITTEST = 0x84, WM_NCPAINT = 0x85, WM_NCACTIVATE = 0x86,
			WM_GETDLGCODE = 0x87, WM_SYNCPAINT = 0x88, WM_NCMOUSEMOVE = 0xA0, WM_NCLBUTTONDOWN = 0xA1,
			WM_NCLBUTTONUP = 0xA2, WM_NCLBUTTONDBLCLK = 0xA3, WM_NCRBUTTONDOWN = 0xA4, WM_NCRBUTTONUP = 0xA5,
			WM_NCRBUTTONDBLCLK = 0xA6, WM_NCMBUTTONDOWN = 0xA7, WM_NCMBUTTONUP = 0xA8, WM_NCMBUTTONDBLCLK = 0xA9,
			WM_NCXBUTTONDOWN = 0xAB, WM_NCXBUTTONUP = 0xAC, WM_NCXBUTTONDBLCLK = 0xAD, WM_INPUT_DEVICE_CHANGE = 0xFE,
			WM_INPUT = 0xFF, WM_KEYFIRST = 0x100, WM_KEYDOWN = 0x100, WM_KEYUP = 0x101, WM_CHAR = 0x102,
			WM_DEADCHAR = 0x103, WM_SYSKEYDOWN = 0x104, WM_SYSKEYUP = 0x105, WM_SYSCHAR = 0x106, WM_SYSDEADCHAR = 0x107,
			WM_UNICHAR = 0x109, UNICODE_NOCHAR = 0xFFFF, WM_IME_STARTCOMPOSITION = 0x10D, WM_IME_ENDCOMPOSITION = 0x10E,
			WM_IME_COMPOSITION = 0x10F, WM_IME_KEYLAST = 0x10F, WM_INITDIALOG = 0x110, WM_COMMAND = 0x111,
			WM_SYSCOMMAND = 0x112, WM_TIMER = 0x113, WM_HSCROLL = 0x114, WM_VSCROLL = 0x115, WM_INITMENU = 0x116,
			WM_INITMENUPOPUP = 0x117, WM_GESTURE = 0x119, WM_GESTURENOTIFY = 0x11A, WM_MENUSELECT = 0x11F,
			WM_MENUCHAR = 0x120, WM_ENTERIDLE = 0x121, WM_MENURBUTTONUP = 0x122, WM_MENUDRAG = 0x123,
			WM_MENUGETOBJECT = 0x124, WM_UNINITMENUPOPUP = 0x125, WM_MENUCOMMAND = 0x126, WM_CHANGEUISTATE = 0x127,
			WM_UPDATEUISTATE = 0x128, WM_QUERYUISTATE = 0x129, WM_CTLCOLORMSGBOX = 0x132, WM_CTLCOLOREDIT = 0x133,
			WM_CTLCOLORLISTBOX = 0x134, WM_CTLCOLORBTN = 0x135, WM_CTLCOLORDLG = 0x136, WM_CTLCOLORSCROLLBAR = 0x137,
			WM_CTLCOLORSTATIC = 0x138, MN_GETHMENU = 0x1E1, WM_MOUSEFIRST = 0x200, WM_MOUSEMOVE = 0x200,
			WM_LBUTTONDOWN = 0x201, WM_LBUTTONUP = 0x202, WM_LBUTTONDBLCLK = 0x203, WM_RBUTTONDOWN = 0x204,
			WM_RBUTTONUP = 0x205, WM_RBUTTONDBLCLK = 0x206, WM_MBUTTONDOWN = 0x207, WM_MBUTTONUP = 0x208,
			WM_MBUTTONDBLCLK = 0x209, WM_MOUSEWHEEL = 0x20A, WM_XBUTTONDOWN = 0x20B, WM_XBUTTONUP = 0x20C,
			WM_XBUTTONDBLCLK = 0x20D, WM_MOUSEHWHEEL = 0x20E, WM_PARENTNOTIFY = 0x210, WM_ENTERMENULOOP = 0x211,
			WM_EXITMENULOOP = 0x212, WM_NEXTMENU = 0x213, WM_SIZING = 0x214, WM_CAPTURECHANGED = 0x215,
			WM_MOVING = 0x216, WM_POWERBROADCAST = 0x218, WM_DEVICECHANGE = 0x219, WM_MDICREATE = 0x220,
			WM_MDIDESTROY = 0x221, WM_MDIACTIVATE = 0x222, WM_MDIRESTORE = 0x223, WM_MDINEXT = 0x224,
			WM_MDIMAXIMIZE = 0x225, WM_MDITILE = 0x226, WM_MDICASCADE = 0x227, WM_MDIICONARRANGE = 0x228,
			WM_MDIGETACTIVE = 0x229, WM_MDISETMENU = 0x230, WM_ENTERSIZEMOVE = 0x231, WM_EXITSIZEMOVE = 0x232,
			WM_DROPFILES = 0x233, WM_MDIREFRESHMENU = 0x234, WM_TOUCH = 0x240, WM_IME_SETCONTEXT = 0x281,
			WM_IME_NOTIFY = 0x282, WM_IME_CONTROL = 0x283, WM_IME_COMPOSITIONFULL = 0x284, WM_IME_SELECT = 0x285,
			WM_IME_CHAR = 0x286, WM_IME_REQUEST = 0x288, WM_IME_KEYDOWN = 0x290, WM_IME_KEYUP = 0x291,
			WM_MOUSEHOVER = 0x2A1, WM_MOUSELEAVE = 0x2A3, WM_NCMOUSEHOVER = 0x2A0, WM_NCMOUSELEAVE = 0x2A2,
			WM_WTSSESSION_CHANGE = 0x2B1, WM_TABLET_FIRST = 0x2C0, WM_TABLET_LAST = 0x2DF, WM_CUT = 0x300,
			WM_COPY = 0x301, WM_PASTE = 0x302, WM_CLEAR = 0x303, WM_UNDO = 0x304, WM_RENDERFORMAT = 0x305,
			WM_RENDERALLFORMATS = 0x306, WM_DESTROYCLIPBOARD = 0x307, WM_DRAWCLIPBOARD = 0x308,
			WM_PAINTCLIPBOARD = 0x309, WM_VSCROLLCLIPBOARD = 0x30A, WM_SIZECLIPBOARD = 0x30B,
			WM_ASKCBFORMATNAME = 0x30C, WM_CHANGECBCHAIN = 0x30D, WM_HSCROLLCLIPBOARD = 0x30E,
			WM_QUERYNEWPALETTE = 0x30F, WM_PALETTEISCHANGING = 0x310, WM_PALETTECHANGED = 0x311, WM_HOTKEY = 0x312,
			WM_PRINT = 0x317, WM_PRINTCLIENT = 0x318, WM_APPCOMMAND = 0x319, WM_THEMECHANGED = 0x31A,
			WM_CLIPBOARDUPDATE = 0x31D, WM_DWMCOMPOSITIONCHANGED = 0x31E, WM_DWMNCRENDERINGCHANGED = 0x31F,
			WM_DWMCOLORIZATIONCOLORCHANGED = 0x320, WM_DWMWINDOWMAXIMIZEDCHANGE = 0x321,
			WM_DWMSENDICONICTHUMBNAIL = 0x323, WM_DWMSENDICONICLIVEPREVIEWBITMAP = 0x326, WM_GETTITLEBARINFOEX = 0x33F,
			WM_HANDHELDFIRST = 0x358, WM_HANDHELDLAST = 0x35F, WM_AFXFIRST = 0x360, WM_AFXLAST = 0x37F,
			WM_PENWINFIRST = 0x380, WM_PENWINLAST = 0x38F, WM_APP = 0x8000, WM_USER = 0x400;

	public static final int WA_ACTIVE = 1, WA_CLICKACTIVE = 2, WA_INACTIVE = 0;

	public static final int SIZE_RESTORED = 0, SIZE_MINIMIZED = 1, SIZE_MAXIMIZED = 2, SIZE_MAXSHOW = 3,
			SIZE_MAXHIDE = 4;

	public static final int DBT_APPYBEGIN = 0x0, DBT_APPYEND = 0x1, DBT_DEVNODES_CHANGED = 0x7,
			DBT_QUERYCHANGECONFIG = 0x17, DBT_CONFIGCHANGED = 0x18, DBT_CONFIGCHANGECANCELED = 0x19,
			DBT_MONITORCHANGE = 0x1B;

	public static final int SC_SIZE = 0xF000, SC_MOVE = 0xF010, SC_MINIMIZE = 0xF020, SC_MAXIMIZE = 0xF030,
			SC_NEXTWINDOW = 0xF040, SC_PREVWINDOW = 0xF050, SC_CLOSE = 0xF060, SC_VSCROLL = 0xF070, SC_HSCROLL = 0xF080,
			SC_MOUSEMENU = 0xF090, SC_KEYMENU = 0xF100, SC_ARRANGE = 0xF110, SC_RESTORE = 0xF120, SC_TASKLIST = 0xF130,
			SC_SCREENSAVE = 0xF140, SC_HOTKEY = 0xF150, SC_DEFAULT = 0xF160, SC_MONITORPOWER = 0xF170,
			SC_CONTEXTHELP = 0xF180, SC_SEPARATOR = 0xF00F;

	public static final int MK_LBUTTON = 0x1, MK_RBUTTON = 0x2, MK_SHIFT = 0x4, MK_CONTROL = 0x8, MK_MBUTTON = 0x10,
			MK_XBUTTON1 = 0x20, MK_XBUTTON2 = 0x40;

	public static final int HTERROR = -2, HTTRANSPARENT = -1, HTNOWHERE = 0, HTCLIENT = 1, HTCAPTION = 2, HTSYSMENU = 3,
			HTGROWBOX = 4, HTSIZE = HTGROWBOX, HTMENU = 5, HTHSCROLL = 6, HTVSCROLL = 7, HTMINBUTTON = 8,
			HTMAXBUTTON = 9, HTLEFT = 10, HTRIGHT = 11, HTTOP = 12, HTTOPLEFT = 13, HTTOPRIGHT = 14, HTBOTTOM = 15,
			HTBOTTOMLEFT = 16, HTBOTTOMRIGHT = 17, HTBORDER = 18, HTREDUCE = HTMINBUTTON, HTZOOM = HTMAXBUTTON,
			HTSIZEFIRST = HTLEFT, HTSIZELAST = HTBOTTOMRIGHT, HTOBJECT = 19, HTCLOSE = 20, HTHELP = 21;

	public static final int GWL_WNDPROC = -4, GWL_HINSTANCE = -6, GWL_HWNDPARENT = -8, GWL_STYLE = -16,
			GWL_EXSTYLE = -20, GWL_USERDATA = -21, GWL_ID = -12;

	public static final int SW_HIDE = 0, SW_SHOWNORMAL = 1, SW_NORMAL = 1, SW_SHOWMINIMIZED = 2, SW_SHOWMAXIMIZED = 3,
			SW_MAXIMIZE = 3, SW_SHOWNOACTIVATE = 4, SW_SHOW = 5, SW_MINIMIZE = 6, SW_SHOWMINNOACTIVE = 7, SW_SHOWNA = 8,
			SW_RESTORE = 9, SW_SHOWDEFAULT = 10, SW_FORCEMINIMIZE = 11, SW_MAX = 11;

	public static final long HWND_TOP = 0x0L, HWND_BOTTOM = 0x1L, HWND_TOPMOST = 0xFFFFFFFFFFFFFFFFL,
			HWND_NOTOPMOST = 0xFFFFFFFFFFFFFFFEL;

	public static final long HWND_BROADCAST = 0xFFFFL;

	public static final int SWP_NOSIZE = 0x1, SWP_NOMOVE = 0x2, SWP_NOZORDER = 0x4, SWP_NOREDRAW = 0x8,
			SWP_NOACTIVATE = 0x10, SWP_FRAMECHANGED = 0x20, SWP_SHOWWINDOW = 0x40, SWP_HIDEWINDOW = 0x80,
			SWP_NOCOPYBITS = 0x100, SWP_NOOWNERZORDER = 0x200, SWP_NOSENDCHANGING = 0x400,
			SWP_DRAWFRAME = SWP_FRAMECHANGED, SWP_NOREPOSITION = SWP_NOOWNERZORDER, SWP_DEFERERASE = 0x2000,
			SWP_ASYNCWINDOWPOS = 0x4000;

	public static final int IDI_APPLICATION = 32512, IDI_HAND = 32513, IDI_QUESTION = 32514, IDI_EXCLAMATION = 32515,
			IDI_ASTERISK = 32516, IDI_WINLOGO = 32517, IDI_SHIELD = 32518, IDI_WARNING = IDI_EXCLAMATION,
			IDI_ERROR = IDI_HAND, IDI_INFORMATION = IDI_ASTERISK;

	public static final int IDC_ARROW = 32512, IDC_IBEAM = 32513, IDC_WAIT = 32514, IDC_CROSS = 32515,
			IDC_UPARROW = 32516, IDC_SIZE = 32640, IDC_ICON = 32641, IDC_SIZENWSE = 32642, IDC_SIZENESW = 32643,
			IDC_SIZEWE = 32644, IDC_SIZENS = 32645, IDC_SIZEALL = 32646, IDC_NO = 32648, IDC_HAND = 32649,
			IDC_APPSTARTING = 32650, IDC_HELP = 32651;

	public static final int GCL_MENUNAME = -8, GCL_HBRBACKGROUND = -10, GCL_HCURSOR = -12, GCL_HICON = -14,
			GCL_HMODULE = -16, GCL_CBWNDEXTRA = -18, GCL_CBCLSEXTRA = -20, GCL_WNDPROC = -24, GCL_STYLE = -26,
			GCW_ATOM = -32, GCL_HICONSM = -34;

	public static final int QS_KEY = 0x1, QS_MOUSEMOVE = 0x2, QS_MOUSEBUTTON = 0x4, QS_POSTMESSAGE = 0x8,
			QS_TIMER = 0x10, QS_PAINT = 0x20, QS_SENDMESSAGE = 0x40, QS_HOTKEY = 0x80, QS_ALLPOSTMESSAGE = 0x100,
			QS_RAWINPUT = 0x400, QS_MOUSE = QS_MOUSEMOVE | QS_MOUSEBUTTON, QS_INPUT = QS_MOUSE | QS_KEY,
			QS_ALLEVENTS = QS_INPUT | QS_POSTMESSAGE | QS_TIMER | QS_PAINT | QS_HOTKEY,
			QS_ALLINPUT = QS_INPUT | QS_POSTMESSAGE | QS_TIMER | QS_PAINT | QS_HOTKEY | QS_SENDMESSAGE;

	public static final int PM_NOREMOVE = 0x0, PM_REMOVE = 0x1, PM_NOYIELD = 0x2, PM_QS_INPUT = QS_INPUT << 16,
			PM_QS_POSTMESSAGE = (QS_POSTMESSAGE | QS_HOTKEY | QS_TIMER) << 16, PM_QS_PAINT = QS_PAINT << 16,
			PM_QS_SENDMESSAGE = QS_SENDMESSAGE << 16;

	public static final int VK_LBUTTON = 0x1, VK_RBUTTON = 0x2, VK_CANCEL = 0x3, VK_MBUTTON = 0x4, VK_XBUTTON1 = 0x5,
			VK_XBUTTON2 = 0x6, VK_BACK = 0x8, VK_TAB = 0x9, VK_CLEAR = 0xC, VK_RETURN = 0xD, VK_SHIFT = 0x10,
			VK_CONTROL = 0x11, VK_MENU = 0x12, VK_PAUSE = 0x13, VK_CAPITAL = 0x14, VK_KANA = 0x15, VK_HANGEUL = 0x15,
			VK_HANGUL = 0x15, VK_JUNJA = 0x17, VK_FINAL = 0x18, VK_HANJA = 0x19, VK_KANJI = 0x19, VK_ESCAPE = 0x1B,
			VK_CONVERT = 0x1C, VK_NONCONVERT = 0x1D, VK_ACCEPT = 0x1E, VK_MODECHANGE = 0x1F, VK_SPACE = 0x20,
			VK_PRIOR = 0x21, VK_NEXT = 0x22, VK_END = 0x23, VK_HOME = 0x24, VK_LEFT = 0x25, VK_UP = 0x26,
			VK_RIGHT = 0x27, VK_DOWN = 0x28, VK_SELECT = 0x29, VK_PRINT = 0x2A, VK_EXECUTE = 0x2B, VK_SNAPSHOT = 0x2C,
			VK_INSERT = 0x2D, VK_DELETE = 0x2E, VK_HELP = 0x2F, VK_LWIN = 0x5B, VK_RWIN = 0x5C, VK_APPS = 0x5D,
			VK_SLEEP = 0x5F, VK_NUMPAD0 = 0x60, VK_NUMPAD1 = 0x61, VK_NUMPAD2 = 0x62, VK_NUMPAD3 = 0x63,
			VK_NUMPAD4 = 0x64, VK_NUMPAD5 = 0x65, VK_NUMPAD6 = 0x66, VK_NUMPAD7 = 0x67, VK_NUMPAD8 = 0x68,
			VK_NUMPAD9 = 0x69, VK_MULTIPLY = 0x6A, VK_ADD = 0x6B, VK_SEPARATOR = 0x6C, VK_SUBTRACT = 0x6D,
			VK_DECIMAL = 0x6E, VK_DIVIDE = 0x6F, VK_F1 = 0x70, VK_F2 = 0x71, VK_F3 = 0x72, VK_F4 = 0x73, VK_F5 = 0x74,
			VK_F6 = 0x75, VK_F7 = 0x76, VK_F8 = 0x77, VK_F9 = 0x78, VK_F10 = 0x79, VK_F11 = 0x7A, VK_F12 = 0x7B,
			VK_F13 = 0x7C, VK_F14 = 0x7D, VK_F15 = 0x7E, VK_F16 = 0x7F, VK_F17 = 0x80, VK_F18 = 0x81, VK_F19 = 0x82,
			VK_F20 = 0x83, VK_F21 = 0x84, VK_F22 = 0x85, VK_F23 = 0x86, VK_F24 = 0x87, VK_NUMLOCK = 0x90,
			VK_SCROLL = 0x91, VK_OEM_NEC_EQUAL = 0x92, VK_OEM_FJ_JISHO = 0x92, VK_OEM_FJ_MASSHOU = 0x93,
			VK_OEM_FJ_TOUROKU = 0x94, VK_OEM_FJ_LOYA = 0x95, VK_OEM_FJ_ROYA = 0x96, VK_LSHIFT = 0xA0, VK_RSHIFT = 0xA1,
			VK_LCONTROL = 0xA2, VK_RCONTROL = 0xA3, VK_LMENU = 0xA4, VK_RMENU = 0xA5, VK_BROWSER_BACK = 0xA6,
			VK_BROWSER_FORWARD = 0xA7, VK_BROWSER_REFRESH = 0xA8, VK_BROWSER_STOP = 0xA9, VK_BROWSER_SEARCH = 0xAA,
			VK_BROWSER_FAVORITES = 0xAB, VK_BROWSER_HOME = 0xAC, VK_VOLUME_MUTE = 0xAD, VK_VOLUME_DOWN = 0xAE,
			VK_VOLUME_UP = 0xAF, VK_MEDIA_NEXT_TRACK = 0xB0, VK_MEDIA_PREV_TRACK = 0xB1, VK_MEDIA_STOP = 0xB2,
			VK_MEDIA_PLAY_PAUSE = 0xB3, VK_LAUNCH_MAIL = 0xB4, VK_LAUNCH_MEDIA_SELECT = 0xB5, VK_LAUNCH_APP1 = 0xB6,
			VK_LAUNCH_APP2 = 0xB7, VK_OEM_1 = 0xBA, VK_OEM_PLUS = 0xBB, VK_OEM_COMMA = 0xBC, VK_OEM_MINUS = 0xBD,
			VK_OEM_PERIOD = 0xBE, VK_OEM_2 = 0xBF, VK_OEM_3 = 0xC0, VK_OEM_4 = 0xDB, VK_OEM_5 = 0xDC, VK_OEM_6 = 0xDD,
			VK_OEM_7 = 0xDE, VK_OEM_8 = 0xDF, VK_OEM_AX = 0xE1, VK_OEM_102 = 0xE2, VK_ICO_HELP = 0xE3, VK_ICO_00 = 0xE4,
			VK_PROCESSKEY = 0xE5, VK_ICO_CLEAR = 0xE6, VK_PACKET = 0xE7, VK_OEM_RESET = 0xE9, VK_OEM_JUMP = 0xEA,
			VK_OEM_PA1 = 0xEB, VK_OEM_PA2 = 0xEC, VK_OEM_PA3 = 0xED, VK_OEM_WSCTRL = 0xEE, VK_OEM_CUSEL = 0xEF,
			VK_OEM_ATTN = 0xF0, VK_OEM_FINISH = 0xF1, VK_OEM_COPY = 0xF2, VK_OEM_AUTO = 0xF3, VK_OEM_ENLW = 0xF4,
			VK_OEM_BACKTAB = 0xF5, VK_ATTN = 0xF6, VK_CRSEL = 0xF7, VK_EXSEL = 0xF8, VK_EREOF = 0xF9, VK_PLAY = 0xFA,
			VK_ZOOM = 0xFB, VK_NONAME = 0xFC, VK_PA1 = 0xFD, VK_OEM_CLEAR = 0xFE;

	public static final int XBUTTON1 = 0x1, XBUTTON2 = 0x2;

	public static final int WHEEL_DELTA = 120;

	public static final int DPI_AWARENESS_INVALID = -1, DPI_AWARENESS_UNAWARE = 0, DPI_AWARENESS_SYSTEM_AWARE = 1,
			DPI_AWARENESS_PER_MONITOR_AWARE = 2;

	public static final long DPI_AWARENESS_CONTEXT_UNAWARE = -1L;

	public static final long DPI_AWARENESS_CONTEXT_SYSTEM_AWARE = -2L;

	public static final long DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE = -3L;

	public static final long DPI_AWARENESS_CONTEXT_PER_MONITOR_AWARE_V2 = -4L;

	public static final int WPF_SETMINPOSITION = 0x1, WPF_RESTORETOMAXIMIZED = 0x2, WPF_ASYNCWINDOWPLACEMENT = 0x4;

	public static final int LWA_COLORKEY = 0x1, LWA_ALPHA = 0x2;

	public static final int SM_CXSCREEN = 0, SM_CYSCREEN = 1, SM_CXVSCROLL = 2, SM_CYHSCROLL = 3, SM_CYCAPTION = 4,
			SM_CXBORDER = 5, SM_CYBORDER = 6, SM_CXDLGFRAME = 7, SM_CYDLGFRAME = 8, SM_CYVTHUMB = 9, SM_CXHTHUMB = 10,
			SM_CXICON = 11, SM_CYICON = 12, SM_CXCURSOR = 13, SM_CYCURSOR = 14, SM_CYMENU = 15, SM_CXFULLSCREEN = 16,
			SM_CYFULLSCREEN = 17, SM_CYKANJIWINDOW = 18, SM_MOUSEPRESENT = 19, SM_CYVSCROLL = 20, SM_CXHSCROLL = 21,
			SM_DEBUG = 22, SM_SWAPBUTTON = 23, SM_RESERVED1 = 24, SM_RESERVED2 = 25, SM_RESERVED3 = 26,
			SM_RESERVED4 = 27, SM_CXMIN = 28, SM_CYMIN = 29, SM_CXSIZE = 30, SM_CYSIZE = 31, SM_CXFRAME = 32,
			SM_CYFRAME = 33, SM_CXMINTRACK = 34, SM_CYMINTRACK = 35, SM_CXDOUBLECLK = 36, SM_CYDOUBLECLK = 37,
			SM_CXICONSPACING = 38, SM_CYICONSPACING = 39, SM_MENUDROPALIGNMENT = 40, SM_PENWINDOWS = 41,
			SM_DBCSENABLED = 42, SM_CMOUSEBUTTONS = 43, SM_CXFIXEDFRAME = SM_CXDLGFRAME,
			SM_CYFIXEDFRAME = SM_CYDLGFRAME, SM_CXSIZEFRAME = SM_CXFRAME, SM_CYSIZEFRAME = SM_CYFRAME, SM_SECURE = 44,
			SM_CXEDGE = 45, SM_CYEDGE = 46, SM_CXMINSPACING = 47, SM_CYMINSPACING = 48, SM_CXSMICON = 49,
			SM_CYSMICON = 50, SM_CYSMCAPTION = 51, SM_CXSMSIZE = 52, SM_CYSMSIZE = 53, SM_CXMENUSIZE = 54,
			SM_CYMENUSIZE = 55, SM_ARRANGE = 56, SM_CXMINIMIZED = 57, SM_CYMINIMIZED = 58, SM_CXMAXTRACK = 59,
			SM_CYMAXTRACK = 60, SM_CXMAXIMIZED = 61, SM_CYMAXIMIZED = 62, SM_NETWORK = 63, SM_CLEANBOOT = 67,
			SM_CXDRAG = 68, SM_CYDRAG = 69, SM_SHOWSOUNDS = 70, SM_CXMENUCHECK = 71, SM_CYMENUCHECK = 72,
			SM_SLOWMACHINE = 73, SM_MIDEASTENABLED = 74, SM_MOUSEWHEELPRESENT = 75, SM_XVIRTUALSCREEN = 76,
			SM_YVIRTUALSCREEN = 77, SM_CXVIRTUALSCREEN = 78, SM_CYVIRTUALSCREEN = 79, SM_CMONITORS = 80,
			SM_SAMEDISPLAYFORMAT = 81, SM_IMMENABLED = 82, SM_REMOTESESSION = 0x1000, SM_SHUTTINGDOWN = 0x2000,
			SM_REMOTECONTROL = 0x2001, SM_CARETBLINKINGENABLED = 0x2002, SM_CXFOCUSBORDER = 83, SM_CYFOCUSBORDER = 84,
			SM_TABLETPC = 86, SM_MEDIACENTER = 87, SM_STARTER = 88, SM_SERVERR2 = 89,
			SM_MOUSEHORIZONTALWHEELPRESENT = 91, SM_CXPADDEDBORDER = 92, SM_DIGITIZER = 94, SM_MAXIMUMTOUCHES = 95;

	public static final int TWF_FINETOUCH = 0x1, TWF_WANTPALM = 0x2;

	public static final int TOUCHEVENTF_MOVE = 0x1, TOUCHEVENTF_DOWN = 0x2, TOUCHEVENTF_UP = 0x4,
			TOUCHEVENTF_INRANGE = 0x8, TOUCHEVENTF_PRIMARY = 0x10, TOUCHEVENTF_NOCOALESCE = 0x20,
			TOUCHEVENTF_PEN = 0x40, TOUCHEVENTF_PALM = 0x80;

	public static final int TOUCHINPUTMASKF_TIMEFROMSYSTEM = 0x1, TOUCHINPUTMASKF_EXTRAINFO = 0x2,
			TOUCHINPUTMASKF_CONTACTAREA = 0x4;

	public static final int MONITOR_DEFAULTTONULL = 0x0, MONITOR_DEFAULTTOPRIMARY = 0x1, MONITOR_DEFAULTTONEAREST = 0x2;

	public static final int MONITORINFOF_PRIMARY = 0x1;

	public static final int EDD_GET_DEVICE_INTERFACE_NAME = 0x1;

	public static final int ENUM_CURRENT_SETTINGS = -1, ENUM_REGISTRY_SETTINGS = -2;

	public static final int EDS_RAWMODE = 0x2, EDS_ROTATEDMODE = 0x4;

	public static final int CDS_UPDATEREGISTRY = 0x1, CDS_TEST = 0x2, CDS_FULLSCREEN = 0x4, CDS_GLOBAL = 0x8,
			CDS_SET_PRIMARY = 0x10, CDS_VIDEOPARAMETERS = 0x20, CDS_ENABLE_UNSAFE_MODES = 0x100,
			CDS_DISABLE_UNSAFE_MODES = 0x200, CDS_RESET = 0x40000000, CDS_RESET_EX = 0x20000000,
			CDS_NORESET = 0x10000000;

	public static final int DISP_CHANGE_SUCCESSFUL = 0, DISP_CHANGE_RESTART = 1, DISP_CHANGE_FAILED = -1,
			DISP_CHANGE_BADMODE = -2, DISP_CHANGE_NOTUPDATED = -3, DISP_CHANGE_BADFLAGS = -4, DISP_CHANGE_BADPARAM = -5,
			DISP_CHANGE_BADDUALVIEW = -6;

	public int RegisterWindowMessage(String lpString);

	public int DeregisterShellHookWindow(HWND hWnd);

	public boolean RegisterShellHookWindow(HWND hWnd);

	public HRESULT SetWindowCompositionAttribute(HWND hwnd, WindowCompositionAttributeData data);

	public HRESULT GetWindowCompositionAttribute(HWND hwnd, WindowCompositionAttributeData task);

	public boolean SystemParametersInfo(int uiAction, int uiParam, Pointer pvParam, int fWinIni);

	boolean EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer ptr);

	public int GetWindowTextW(HWND hWnd, char[] buffer, int nMaxCount);

	public LRESULT SendMessage(HWND hwnd, int msg, WPARAM wParam, LPARAM lParam);

	public void keybd_event(int bVk, int bScan, int dwFlags, int dwExtraInfo);

	public boolean GetWindowPlacement(HWND hwnd, WINDOWPLACEMENT winpl);

	public long GetWindowLongPtr(HWND hWnd, int nIndex);

	public long SetWindowLongPtr(HWND hWnd, int nIndex, long dwNewLong);

	public long GetClassLongPtr(HWND hwnd, int nIndex);

	public boolean ReleaseCapture();

	public boolean SetShellWindow(HWND hwnd);

	public boolean SetTaskmanWindow(HWND hwnd);

	public boolean SendNotifyMessage(HWND hwnd, int msg, WPARAM wParam, LPARAM lParam);

	public boolean ShowWindowAsync(HWND hwnd, int msg);

	public boolean IsZoomed(HWND hwnd);

	public HWND GetShellWindow();

	public HDC BeginPaint(HWND hwnd, PAINTSTRUCT paint);

	public HDC EndPaint(HWND hwnd, PAINTSTRUCT paint);

	public int FillRect(HDC hDC, RECT lprc, HBRUSH hbr);

	public int DrawText(HDC hdc, String lpchText, int cchText, RECT lprc, UINT format);

	public boolean SetWindowText(HWND hWnd, String lpString);

}