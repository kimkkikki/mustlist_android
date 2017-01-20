package io.questcompany.mustlist.entity;

/**
 * Created by kimkkikki on 2016. 10. 6..
 * preview result object
 */
public class Must {
    public Integer index;
    public String title;
    public Integer deposit;
    public Integer default_point;
    public boolean check;
    public boolean success;
    public boolean end;
    public int days;
    public int total_count;
    public int check_count;

    public String start_date;
    public String end_date;

    public Must() {
    }

    @Override
    public String toString() {
        return "Must{" +
                "index=" + index +
                ", title='" + title + '\'' +
                ", deposit=" + deposit +
                ", default_point=" + default_point +
                ", check=" + check +
                ", success=" + success +
                ", end=" + end +
                ", days=" + days +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                '}';
    }
}
