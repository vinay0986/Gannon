package com.vinni.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the auction_transaction_history database table.
 * 
 */
@Entity
@Table(name = "auction_transaction_history")
@NamedQuery(name = "AuctionTransactionHistory.findAll", query = "SELECT a FROM AuctionTransactionHistory a")
public class AuctionTransactionHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auction_transaction_history_id")
	private int auctionTransactionHistoryId;

	@Column(name = "auction_amount")
	private String auctionAmount;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "auction_price_change_date")
	private Date auctionPriceChangeDate;

	// bi-directional many-to-one association to AuctionTransaction
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_transaction_id")
	private AuctionTransaction auctionTransaction;

	// bi-directional many-to-one association to User
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_user")
	private Users aictionUser;

	public AuctionTransactionHistory() {
	}

	public int getAuctionTransactionHistoryId() {
		return this.auctionTransactionHistoryId;
	}

	public void setAuctionTransactionHistoryId(int auctionTransactionHistoryId) {
		this.auctionTransactionHistoryId = auctionTransactionHistoryId;
	}

	public String getAuctionAmount() {
		return this.auctionAmount;
	}

	public void setAuctionAmount(String auctionAmount) {
		this.auctionAmount = auctionAmount;
	}

	public Date getAuctionPriceChangeDate() {
		return this.auctionPriceChangeDate;
	}

	public void setAuctionPriceChangeDate(Date auctionPriceChangeDate) {
		this.auctionPriceChangeDate = auctionPriceChangeDate;
	}

	public AuctionTransaction getAuctionTransaction() {
		return this.auctionTransaction;
	}

	public void setAuctionTransaction(AuctionTransaction auctionTransaction) {
		this.auctionTransaction = auctionTransaction;
	}

	public Users getAictionUser() {
		return aictionUser;
	}

	public void setAictionUser(Users aictionUser) {
		this.aictionUser = aictionUser;
	}

}