package com.vinni.webservices;

import java.util.ArrayList;
import java.util.List;

public class auctionOrDonationServiceRequest {
	private String productName;
	private String productDescription;
	private String auctionOrDonation;
	private String closeDate;
	private String userId;
	private List<String> imagesList = new ArrayList<String>(0);
	private float auctionAmount;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getAuctionOrDonation() {
		return auctionOrDonation;
	}

	public void setAuctionOrDonation(String auctionOrDonation) {
		this.auctionOrDonation = auctionOrDonation;
	}

	public String getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getImagesList() {
		return imagesList;
	}

	public void setImagesList(List<String> imagesList) {
		this.imagesList = imagesList;
	}

	public float getAuctionAmount() {
		return auctionAmount;
	}

	public void setAuctionAmount(float auctionAmount) {
		this.auctionAmount = auctionAmount;
	}

}