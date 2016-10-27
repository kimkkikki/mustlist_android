package io.questcompany.mustlist.entity;

/**
 * Created by kimkkikki on 2016. 10. 10..
 * 공지사항 Object
 */
public class Notice {
    private Integer index;
    private String title;
    private String contents;
    private String regDate;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "index=" + index +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", regDate='" + regDate + '\'' +
                '}';
    }
}
