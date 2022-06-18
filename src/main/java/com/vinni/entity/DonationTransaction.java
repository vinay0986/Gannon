package com.vinni.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
 * The persistent class for the donation_transaction database table.
 * 
 */
@Entity
@Table(name = "donation_transaction")
@NamedQuery(name = "DonationTransaction.findAll", query = "SELECT d FROM DonationTransaction d")
public class DonationTransaction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "donation_transaction_id")
	private int donationTransactionId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "donation_close_date")
	private Date donationCloseDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "donation_created_date")
	private Date donationCreatedDate;

	@Column(name = "donation_product_status")
	private String donationProductStatus;

	@Lob
	@Column(name = "product_description")
	private String productDescription;

	@Column(name = "product_name")
	private String productName;

	// bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name = "donation_created_by")
	private Users donationCreatedBy;

	public DonationTransaction() {
	}

	public int getDonationTransactionId() {
		return this.donationTransactionId;
	}

	public void setDonationTransactionId(int donationTransactionId) {
		this.donationTransactionId = donationTransactionId;
	}

	public Date getDonationCloseDate() {
		return this.donationCloseDate;
	}

	public void setDonationCloseDate(Date donationCloseDate) {
		this.donationCloseDate = donationCloseDate;
	}

	public Date getDonationCreatedDate() {
		return this.donationCreatedDate;
	}

	public void setDonationCreatedDate(Date donationCreatedDate) {
		this.donationCreatedDate = donationCreatedDate;
	}

	public String getDonationProductStatus() {
		return this.donationProductStatus;
	}

	public void setDonationProductStatus(String donationProductStatus) {
		this.donationProductStatus = donationProductStatus;
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

	public Users getDonationCreatedBy() {
		return donationCreatedBy;
	}

	public void setDonationCreatedBy(Users donationCreatedBy) {
		this.donationCreatedBy = donationCreatedBy;
	}

}