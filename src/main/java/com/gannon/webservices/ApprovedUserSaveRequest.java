package com.gannon.webservices;


class ApprovedUserSaveRequest
{
    private int registrationId;
    private String approved;
    private int userId;
    private String denyReason;

	public String getDenyReason() {
		return denyReason;
	}

	public void setDenyReason(String denyReason) {
		this.denyReason = denyReason;
	}
    
    public int getUserId() {
        return this.userId;
    }
    
    public void setUserId(final int userId) {
        this.userId = userId;
    }
    
    public String getApproved() {
        return this.approved;
    }
    
    public void setApproved(final String approved) {
        this.approved = approved;
    }
    
    public int getRegistrationId() {
        return this.registrationId;
    }
    
    public void setRegistrationId(final int registrationId) {
        this.registrationId = registrationId;
    }
}
