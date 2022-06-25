package com.gannon.webservices;

public class ErrorMessagePojo {
    private String error;
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

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

   
}
