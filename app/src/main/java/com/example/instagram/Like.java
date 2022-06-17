package com.example.instagram;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject {

    public static final String KEY_USER = "userLiked";
    public static final String KEY_POST = "postLiked";

    public ParseObject getUser() {
        return getParseObject(KEY_USER);
    }

    public void setUser(ParseObject user) {
        put(KEY_USER, user);
    }

    public ParseObject getPost() {
        return getParseObject(KEY_POST);
    }

    public void setPost(ParseObject post) {
        put(KEY_POST, post);
    }
}
