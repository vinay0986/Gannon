package com.gannon.webservices;

class ApprovedUserListResponse
{
    private int registrationId;
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String status;
    
    public String getStudentId() {
        return this.studentId;
    }
    
    public void setStudentId(final String studentId) {
        this.studentId = studentId;
    }
    
    public int getRegistrationId() {
        return this.registrationId;
    }
    
    public void setRegistrationId(final int registrationId) {
        this.registrationId = registrationId;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    public String getFirstName() {
        return this.firstName;
    }
    
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return this.lastName;
    }
    
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(final String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
