package com.vinni.webservices;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude
public class SuccessMessagePojo {
    @JsonProperty("Data")
    private Object message;
    private String status;
    private int statusCode;

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode2) {
        this.statusCode = statusCode2;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status2) {
        this.status = status2;
    }

    public Object getMessage() {
        return this.message;
    }

    public void setMessage(Object message2) {
        this.message = message2;
    }
}