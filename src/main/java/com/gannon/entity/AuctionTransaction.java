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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the auction_transaction database table.
 * 
 */
@Entity
@Table(name = "auction_transaction")
@NamedQuery(name = "AuctionTransaction.findAll", query = "SELECT a FROM AuctionTransaction a")
public class AuctionTransaction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auction_transaction_id")
	private int auctionTransactionId;

	@Column(name = "auction_amount")
	private float auctionAmount;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "auction_close_date")
	private Date auctionCloseDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "auction_created_date")
	private Date auctionCreatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "auction_price_change_date")
	private Date auctionPriceChangeDate;

	@Column(name = "auction_status")
	private String auctionStatus;

	@Column(name = "initial_auction_amount")
	private float initialAuctionAmount;

	@Lob
	@Column(name = "product_description")
	private String productDescription;

	@Column(name = "product_name")
	private String productName;

	// bi-directional many-to-one association to User
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_created_by")
	private Users auctionCreatedBy;

	// bi-directional many-to-one association to User
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "latest_auction_user")
	private Users latestAuctionUser;

	public Users getAuctionCreatedBy() {
		return auctionCreatedBy;
	}

	public void setAuctionCreatedBy(Users auctionCreatedBy) {
		this.auctionCreatedBy = auctionCreatedBy;
	}

	public Users getLatestAuctionUser() {
		return latestAuctionUser;
	}

	public void setLatestAuctionUser(Users latestAuctionUser) {
		this.latestAuctionUser = latestAuctionUser;
	}

	public AuctionTransaction() {
	}

	public int getAuctionTransactionId() {
		return this.auctionTransactionId;
	}

	public void setAuctionTransactionId(int auctionTransactionId) {
		this.auctionTransactionId = auctionTransactionId;
	}

	public float getAuctionAmount() {
		return this.auctionAmount;
	}

	public void setAuctionAmount(float auctionAmount) {
		this.auctionAmount = auctionAmount;
	}

	public Date getAuctionCloseDate() {
		return this.auctionCloseDate;
	}

	public void setAuctionCloseDate(Date auctionCloseDate) {
		this.auctionCloseDate = auctionCloseDate;
	}

	public Date getAuctionCreatedDate() {
		return this.auctionCreatedDate;
	}

	public void setAuctionCreatedDate(Date auctionCreatedDate) {
		this.auctionCreatedDate = auctionCreatedDate;
	}

	public Date getAuctionPriceChangeDate() {
		return this.auctionPriceChangeDate;
	}

	public void setAuctionPriceChangeDate(Date auctionPriceChangeDate) {
		this.auctionPriceChangeDate = auctionPriceChangeDate;
	}

	public String getAuctionStatus() {
		return this.auctionStatus;
	}

	public void setAuctionStatus(String auctionStatus) {
		this.auctionStatus = auctionStatus;
	}

	public float getInitialAuctionAmount() {
		return this.initialAuctionAmount;
	}

	public void setInitialAuctionAmount(float initialAuctionAmount) {
		this.initialAuctionAmount = initialAuctionAmount;
	}

	public String getProductDescription() {
		return this.productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getProductName() {
		return this.productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

}