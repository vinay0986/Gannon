package com.vinni.webservices;

class ProfileServiceResponse {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String phoneNumber;
    private String studentId;

    ProfileServiceResponse() {
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId2) {
        this.studentId = studentId2;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName2) {
        this.firstName = firstName2;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName2) {
        this.lastName = lastName2;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber2) {
        this.phoneNumber = phoneNumber2;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email2) {
        this.email = email2;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password2) {
        this.password = password2;
    }
}
