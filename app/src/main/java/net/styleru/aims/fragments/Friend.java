package net.styleru.aims.fragments;

import android.graphics.Bitmap;

/**
 * Created by LeonidL on 12.10.16.
 */
public class Friend {

    public Bitmap friend_avatar;
    public String friend_name;
    public String friend_login;


    public Friend(String name, String login, Bitmap avatar) {
        friend_avatar = avatar;
        friend_name = name;
        friend_login = login;
    }
}
