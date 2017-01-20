package io.questcompany.mustlist.entity;

/**
 * Created by kimkkikki on 2017. 1. 17..
 * Server Object Pay
 */

public class Pay {
    public String product_id;
    public String order_id;
    public String token;
    public String date;

    @Override
    public String toString() {
        return "Pay{" +
                "product_id='" + product_id + '\'' +
                ", order_id='" + order_id + '\'' +
                ", token='" + token + '\'' +
                ", date=" + date +
                '}';
    }
}
