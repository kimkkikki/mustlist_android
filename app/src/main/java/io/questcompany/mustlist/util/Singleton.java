package io.questcompany.mustlist.util;

import io.questcompany.mustlist.entity.User;

/**
 * Created by kimkkikki on 2016. 10. 21..
 * Singleton Pattern
 */

public class Singleton {
    private static Singleton uniqueInstance = new Singleton();

    private User user;

    private Singleton() {}

    public static Singleton getInstance() {
        return uniqueInstance;
    }

    public void setIdAndKey(String id, String key) {
        user = new User();
        user.setId(id);
        user.setKey(key);
    }

    public String getId() {
        return user.getId();
    }

    public String getKey() {
        return user.getKey();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
