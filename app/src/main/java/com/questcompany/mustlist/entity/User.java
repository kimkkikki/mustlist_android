package com.questcompany.mustlist.entity;

/**
 * Created by kimkkikki on 2016. 10. 12..
 * Server User Object
 */
public class User {
    private String id;
    private String key;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
