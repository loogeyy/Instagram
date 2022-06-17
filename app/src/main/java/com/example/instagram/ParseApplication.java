package com.example.instagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Like.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("SoAmSLE5WPj49xmzgQAl4XZNyfSepjsNNPDDnxoY")
                .clientKey("J9gzWaJEmNfOq6VGdOOllO4OwgK6rLOegkQ2buIG")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
