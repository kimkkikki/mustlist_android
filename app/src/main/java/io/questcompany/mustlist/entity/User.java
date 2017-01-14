package io.questcompany.mustlist.entity;

/**
 * Created by kimkkikki on 2016. 10. 12..
 * Server User Object
 */
public class User {
    public String id;
    public String key;
    public String email;
    public String deviceId;
    public Integer point;

    public User() {
    }

    public User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", email='" + email + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", point=" + point +
                '}';
    }
}
