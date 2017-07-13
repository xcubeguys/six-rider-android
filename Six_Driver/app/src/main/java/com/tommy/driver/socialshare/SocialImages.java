package com.tommy.driver.socialshare;

import com.tommy.driver.R;

/**
 * Created by javier on 24/04/15.
 */
public class SocialImages {
    private int whatsappImage, facebookImage, twitterImage,
            instagramImage, googleImage, telegramImage, gmailImage,
            linkedinImage, vineImage, hangoutsImage, pinterestImage,
            lineImage, snapchatImage;
    public SocialImages() {
        this.whatsappImage = R.drawable.whatsapp;
        this.facebookImage = R.drawable.facebook;
        this.twitterImage = R.drawable.twitter;
        this.instagramImage = R.drawable.instagram;
        this.googleImage = R.drawable.google;
        this.telegramImage = R.drawable.telegram;
        this.gmailImage = R.drawable.gmail;
        this.linkedinImage = R.drawable.linkedin;
        this.vineImage = R.drawable.vine;
        this.hangoutsImage = R.drawable.hangouts;
        this.pinterestImage = R.drawable.pinterest;
        this.lineImage = R.drawable.line;
        this.snapchatImage = R.drawable.phonebook;
    }
    public SocialImages(int whatsappImage, int facebookImage, int twitterImage, int instagramImage, int googleImage, int telegramImage, int gmailImage, int linkedinImage, int vineImage, int hangoutsImage, int pinterestImage, int lineImage, int snapchatImage) {
        this.whatsappImage = whatsappImage;
        this.facebookImage = facebookImage;
        this.twitterImage = twitterImage;
        this.instagramImage = instagramImage;
        this.googleImage = googleImage;
        this.telegramImage = telegramImage;
        this.gmailImage = gmailImage;
        this.linkedinImage = linkedinImage;
        this.vineImage = vineImage;
        this.hangoutsImage = hangoutsImage;
        this.pinterestImage = pinterestImage;
        this.lineImage = lineImage;
        this.snapchatImage = snapchatImage;
    }

    public int getImageById(int id) {
        switch (id) {
            case SocialNetwork.WHATSAPP:
                return whatsappImage;
            case SocialNetwork.FACEBOOK:
                return facebookImage;
            case SocialNetwork.TWITTER:
                return twitterImage;
            case SocialNetwork.INSTAGRAM:
                return instagramImage;
            case SocialNetwork.PLUS_GOOGLE:
                return googleImage;
            case SocialNetwork.TELEGRAM:
                return telegramImage;
            case SocialNetwork.GMAIL:
                return gmailImage;
            case SocialNetwork.LINKEDIN:
                return linkedinImage;
            case SocialNetwork.VINE:
                return vineImage;
            case SocialNetwork.HANGOUTS:
                return hangoutsImage;
            case SocialNetwork.PINTEREST:
                return pinterestImage;
            case SocialNetwork.LINE:
                return lineImage;
            case SocialNetwork.SNAPCHAT:
                return snapchatImage;
            default:
                return 0;
        }
    }

    public int getWhatsappImage() {
        return whatsappImage;
    }

    public void setWhatsappImage(int whatsappImage) {
        this.whatsappImage = whatsappImage;
    }

    public int getFacebookImage() {
        return facebookImage;
    }

    public void setFacebookImage(int facebookImage) {
        this.facebookImage = facebookImage;
    }

    public int getTwitterImage() {
        return twitterImage;
    }

    public void setTwitterImage(int twitterImage) {
        this.twitterImage = twitterImage;
    }

    public int getInstagramImage() {
        return instagramImage;
    }

    public void setInstagramImage(int instagramImage) {
        this.instagramImage = instagramImage;
    }

    public int getGoogleImage() {
        return googleImage;
    }

    public void setGoogleImage(int googleImage) {
        this.googleImage = googleImage;
    }

    public int getTelegramImage() {
        return telegramImage;
    }

    public void setTelegramImage(int telegramImage) {
        this.telegramImage = telegramImage;
    }

    public int getGmailImage() {
        return gmailImage;
    }

    public void setGmailImage(int gmailImage) {
        this.gmailImage = gmailImage;
    }

    public int getLinkedinImage() {
        return linkedinImage;
    }

    public void setLinkedinImage(int linkedinImage) {
        this.linkedinImage = linkedinImage;
    }

    public int getVineImage() {
        return vineImage;
    }

    public void setVineImage(int vineImage) {
        this.vineImage = vineImage;
    }

    public int getHangoutsImage() {
        return hangoutsImage;
    }

    public void setHangoutsImage(int hangoutsImage) {
        this.hangoutsImage = hangoutsImage;
    }

    public int getPinterestImage() {
        return pinterestImage;
    }

    public void setPinterestImage(int pinterestImage) {
        this.pinterestImage = pinterestImage;
    }

    public int getLineImage() {
        return lineImage;
    }

    public void setLineImage(int lineImage) {
        this.lineImage = lineImage;
    }

    public int getSnapchatImage() {
        return snapchatImage;
    }

    public void setSnapchatImage(int snapchatImage) {
        this.snapchatImage = snapchatImage;
    }
}
