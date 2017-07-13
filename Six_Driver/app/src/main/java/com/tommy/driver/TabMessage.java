package com.tommy.driver;

/**
 * Created by iiro on 7.6.2016.
 */
public class TabMessage {
    public static String get(int menuItemId, boolean isReselection) {
        String message = "Content for ";

        switch (menuItemId) {

           
            case R.id.tab_earning:
                message += "nearby";
                break;
            case R.id.tab_rating:
                message += "friends";
                break;
            case R.id.tab_profile:
                message +="profile";
                break;

        }

        if (isReselection) {
            message += " WAS RESELECTED! YAY!";
        }

        return message;
    }
}
