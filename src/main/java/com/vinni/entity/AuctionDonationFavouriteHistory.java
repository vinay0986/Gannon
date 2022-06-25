package com.vinni.entity;

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
@Table(name = "auction_donation_favourite_history")
public class AuctionDonationFavouriteHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auction_donation_favourite_history_id")
	private Integer auctionDonationFavouriteHistoryId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_transaction_id")
	private AuctionTransaction auctionTransaction;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "donation_transaction_id")
	private DonationTransaction donationTransaction;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users users;

	@Column(name = "created_date")
	private Date createdDate;

	public Integer getAuctionDonationFavouriteHistoryId() {
		return auctionDonationFavouriteHistoryId;
	}

	public void setAuctionDonationFavouriteHistoryId(Integer auctionDonationFavouriteHistoryId) {
		this.auctionDonationFavouriteHistoryId = auctionDonationFavouriteHistoryId;
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

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
