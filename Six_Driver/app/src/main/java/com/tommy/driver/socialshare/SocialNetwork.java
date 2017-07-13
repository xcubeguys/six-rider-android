package com.tommy.driver.socialshare;

/**
 * Created by javier on 24/04/15.
 */
public class SocialNetwork {
    /**
     * CONSTANTS
     */
    public static final int WHATSAPP = 1;
    public static final int FACEBOOK = 2;
    public static final int TWITTER = 3;
    public static final int INSTAGRAM = 4;
    public static final int PLUS_GOOGLE = 5;
    public static final int TELEGRAM = 6;
    public static final int GMAIL = 7;
    public static final int LINKEDIN = 8;
    public static final int VINE = 9;
    public static final int HANGOUTS = 10;
    public static final int PINTEREST = 11;
    public static final int LINE = 12;
    public static final int SNAPCHAT = 13;

    public static final String WHATSAPP_PACKAGE = "com.whatsapp";
    public static final String FACEBOOK_PACKAGE = "com.facebook.katana";
    public static final String TWITTER_PACKAGE = "com.twitter.android";
    public static final String INSTAGRAM_PACKAGE = "com.instagram.android";
    public static final String PLUS_GOOGLE_PACKAGE = "com.google.android.apps.plus";
    public static final String TELEGRAM_PACKAGE = "org.telegram.messenger";
    public static final String GMAIL_PACKAGE = "com.google.android.gm";
    public static final String LINKEDIN_PACKAGE = "com.linkedin.android";
    public static final String VINE_PACKAGE = "co.vine.android";
    public static final String HANGOUTS_PACKAGE = "com.google.android.talk";
    public static final String PINTEREST_PACKAGE = "com.pinterest";
    public static final String LINE_PACKAGE = "jp.naver.line.android";
    public static final String SNAPCHAT_PACKAGE = "com.snapchat.android";

    /**
     * OBJECT
     */
    private int id;
    private int image;

    public SocialNetwork(int id, int image) {
        this.id = id;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
