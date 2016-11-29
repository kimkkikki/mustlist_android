package io.questcompany.mustlist.entity;

/**
 * Created by kimkkikki on 2016. 10. 12..
 * Server User Object
 */
public class User {
    private String id;
    private String key;
    private String deviceId;
    private Integer point;

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", point=" + point +
                '}';
    }
}
