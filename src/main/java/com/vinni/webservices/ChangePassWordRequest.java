package com.vinni.webservices;

class ChangePassWordRequest
{
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private int userId;
    
    public int getUserId() {
        return this.userId;
    }
    
    public void setUserId(final int userId) {
        this.userId = userId;
    }
    
    public String getOldPassword() {
        return this.oldPassword;
    }
    
    public void setOldPassword(final String oldPassword) {
        this.oldPassword = oldPassword;
    }
    
    public String getNewPassword() {
        return this.newPassword;
    }
    
    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String getConfirmPassword() {
        return this.confirmPassword;
    }
    
    public void setConfirmPassword(final String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
