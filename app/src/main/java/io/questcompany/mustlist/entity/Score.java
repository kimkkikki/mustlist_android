package io.questcompany.mustlist.entity;

/**
 * Created by kimkkikki on 2017. 1. 20..
 * Score DB Object
 */

public class Score {
    public String must_title;
    // C : Created, S : Success
    public Character type;
    public int score;

    @Override
    public String toString() {
        return "Score{" +
                "must_title='" + must_title + '\'' +
                ", type=" + type +
                ", score=" + score +
                '}';
    }
}
