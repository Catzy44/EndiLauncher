package me.catzy44.skin.skin2d;

import java.net.MalformedURLException;
import java.net.URL;

public final class Constants {

    public static final int SRC_FRONT_HEAD_X           =  8;
    public static final int SRC_FRONT_HEAD_Y           =  8;
    public static final int SRC_FRONT_HEAD_W           =  8;
    public static final int SRC_FRONT_HEAD_H           =  8;

    public static final int SRC_FRONT_HELMET_X         = 40;
    public static final int SRC_FRONT_HELMET_Y         =  8;
    public static final int SRC_FRONT_HELMET_W         =  8;
    public static final int SRC_FRONT_HELMET_H         =  8;

    public static final int SRC_FRONT_BODY_X           = 20;
    public static final int SRC_FRONT_BODY_Y           = 20;
    public static final int SRC_FRONT_BODY_W           =  8;
    public static final int SRC_FRONT_BODY_H           = 12;

    public static final int SRC_FRONT_ARM_X            = 44;
    public static final int SRC_FRONT_ARM_Y            = 20;
    public static final int SRC_FRONT_ARM_W            =  4;
    public static final int SRC_FRONT_ARM_H            = 12;

    public static final int SRC_FRONT_LEG_X            =  4;
    public static final int SRC_FRONT_LEG_Y            = 20;
    public static final int SRC_FRONT_LEG_W            =  4;
    public static final int SRC_FRONT_LEG_H            = 12;
    
    public static final int SRC_BACK_HEAD_X           =  24;
    public static final int SRC_BACK_HEAD_Y           =  8;
    public static final int SRC_BACK_HEAD_W           =  8;
    public static final int SRC_BACK_HEAD_H           =  8;

    public static final int SRC_BACK_HELMET_X         = 40;
    public static final int SRC_BACK_HELMET_Y         =  8;
    public static final int SRC_BACK_HELMET_W         =  8;
    public static final int SRC_BACK_HELMET_H         =  8;

    public static final int SRC_BACK_BODY_X           = 32;
    public static final int SRC_BACK_BODY_Y           = 20;
    public static final int SRC_BACK_BODY_W           =  8;
    public static final int SRC_BACK_BODY_H           = 12;

    public static final int SRC_BACK_ARM_X            = 52;
    public static final int SRC_BACK_ARM_Y            = 20;
    public static final int SRC_BACK_ARM_W            =  4;
    public static final int SRC_BACK_ARM_H            = 12;

    public static final int SRC_BACK_LEG_X            =  12;
    public static final int SRC_BACK_LEG_Y            = 20;
    public static final int SRC_BACK_LEG_W            =  4;
    public static final int SRC_BACK_LEG_H            = 12;

    public static final int PREVIEW_WIDTH        = 16;
    public static final int PREVIEW_HEIGHT       = 32;

    public static final int PREVIEW_HEAD_X       =  4;
    public static final int PREVIEW_HEAD_Y       =  0;

    public static final int PREVIEW_HELMET_X     =  4;
    public static final int PREVIEW_HELMET_Y     =  0;

    public static final int PREVIEW_BODY_X       =  4;
    public static final int PREVIEW_BODY_Y       =  8;

    public static final int PREVIEW_LEFT_ARM_X   =  0;
    public static final int PREVIEW_LEFT_ARM_Y   =  8;

    public static final int PREVIEW_RIGHT_ARM_X  = 12;
    public static final int PREVIEW_RIGHT_ARM_Y  =  8;

    public static final int PREVIEW_LEFT_LEG_X   =  4;
    public static final int PREVIEW_LEFT_LEG_Y   = 20;

    public static final int PREVIEW_RIGHT_LEG_X  =  8;
    public static final int PREVIEW_RIGHT_LEG_Y  = 20;

    public static final int HEAD_WIDTH           =  8;
    public static final int HEAD_HEIGHT          =  8;

    public static final int HEAD_HEAD_X          =  0;
    public static final int HEAD_HEAD_Y          =  0;

    public static final int HEAD_HELMET_X        =  0;
    public static final int HEAD_HELMET_Y        =  0;

    private static final String SKINS_BASE_URL =
            "http://s3.amazonaws.com/MinecraftSkins/";
    private static final String SKINS_FILE_EXT = ".png";

    public static URL getSkinUrl(String username)
            throws MalformedURLException {
        return new URL(SKINS_BASE_URL + username + SKINS_FILE_EXT);
    }

    private Constants() {
    }
}
