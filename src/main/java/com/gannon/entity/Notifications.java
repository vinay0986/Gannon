package com.gannon.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notifications implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notifications_id", unique = true, nullable = false)
	private Integer notificationsId;

	@Column(name = "message")
	private String message;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "created_by")
	private int createdBy;

	@Column(name = "created_date")
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_transaction_id")
	private AuctionTransaction auctionTransaction;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "donation_transaction_id")
	private DonationTransaction donationTransaction;

	public Integer getNotificationsId() {
		return notificationsId;
	}

	public void setNotificationsId(Integer notificationsId) {
		this.notificationsId = notificationsId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public AuctionTransaction getAuctionTransaction() {
		return auctionTransaction;
	}

	public void setAuctionTransaction(AuctionTransaction auctionTransaction) {
		this.auctionTransaction = auctionTransaction;
	}

	public DonationTransaction getDonationTransaction() {
		return donationTransaction;
	}

	public void setDonationTransaction(DonationTransaction donationTransaction) {
		this.donationTransaction = donationTransaction;
	}

}
