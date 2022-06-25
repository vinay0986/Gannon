package com.gannon.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the product_images database table.
 * 
 */
@Entity
@Table(name = "product_images")
@NamedQuery(name = "ProductImage.findAll", query = "SELECT p FROM ProductImage p")
public class ProductImage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_image_id")
	private int productImageId;

	@Column(name = "image_path")
	private String imagePath;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "uploaded_date")
	private Date uploadedDate;

	// bi-directional many-to-one association to AuctionTransaction
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_trasaction_id")
	private AuctionTransaction auctionTransaction;

	// bi-directional many-to-one association to DonationTransaction
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "donation_transaction_id")
	private DonationTransaction donationTransaction;

	public ProductImage() {
	}

	public int getProductImageId() {
		return this.productImageId;
	}

	public void setProductImageId(int productImageId) {
		this.productImageId = productImageId;
	}

	public Date getUploadedDate() {
		return this.uploadedDate;
	}

	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public AuctionTransaction getAuctionTransaction() {
		return this.auctionTransaction;
	}

	public void setAuctionTransaction(AuctionTransaction auctionTransaction) {
		this.auctionTransaction = auctionTransaction;
	}

	public DonationTransaction getDonationTransaction() {
		return this.donationTransaction;
	}

	public void setDonationTransaction(DonationTransaction donationTransaction) {
		this.donationTransaction = donationTransaction;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}