package com.gannon.webservices;

class LoginServiceRequest {
	private String password;
	private String userName;
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	LoginServiceRequest() {
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName2) {
		this.userName = userName2;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password2) {
		this.password = password2;
	}
}