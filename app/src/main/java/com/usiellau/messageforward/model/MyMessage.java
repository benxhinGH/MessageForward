package com.usiellau.messageforward.model;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public class MyMessage {

    private String id;
    private String address;
    private String body;
    private long dateSent;

    public MyMessage(String id,String address, String body,long dateSent) {
        this.id=id;
        this.address = address;
        this.body = body;
        this.dateSent=dateSent;
    }

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
