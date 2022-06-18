package com.vinni.webservices;

class LoginServiceResponse {
    private boolean adminFlag;
    private String message;
    private int userId;
    private String userName;

    LoginServiceResponse() {
    }

    public boolean isAdminFlag() {
        return this.adminFlag;
    }

    public void setAdminFlag(boolean adminFlag2) {
        this.adminFlag = adminFlag2;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName2) {
        this.userName = userName2;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId2) {
        this.userId = userId2;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message2) {
        this.message = message2;
    }
}
