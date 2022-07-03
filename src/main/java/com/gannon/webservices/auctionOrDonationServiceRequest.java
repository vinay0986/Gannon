package com.gannon.webservices;

import java.util.ArrayList;
import java.util.List;

public class auctionOrDonationServiceRequest {
	private String productName;
	private String productDescription;
	private String auctionOrDonation;
	private String closeDate;
	private int userId;
	private List<String> imagesList = new ArrayList<String>(0);
	private int auctionAmount;

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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public List<String> getImagesList() {
		return imagesList;
	}

	public void setImagesList(List<String> imagesList) {
		this.imagesList = imagesList;
	}

	public int getAuctionAmount() {
		return auctionAmount;
	}

	public void setAuctionAmount(int auctionAmount) {
		this.auctionAmount = auctionAmount;
	}

}