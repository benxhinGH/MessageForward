package com.usiellau.messageforward.model;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public class MyMessage {
    private String address;
    private String body;

    public MyMessage(String address, String body) {
        this.address = address;
        this.body = body;
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
