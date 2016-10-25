package com.questcompany.mustlist.util;

/**
 * Created by kimkkikki on 2016. 10. 21..
 * Singleton Pattern
 */

public class Singleton {
    private static Singleton uniqueInstance = new Singleton();
    private String _id;
    private String _key;

    private Singleton() {}

    public static Singleton getInstance() {
        return uniqueInstance;
    }

    public void setIdAndKey(String id, String key) {
        _id = id;
        _key = key;
    }

    public String getId() {
        return _id;
    }

    public String getKey() {
        return _key;
    }
}
