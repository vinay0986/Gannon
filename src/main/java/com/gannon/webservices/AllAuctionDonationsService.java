package com.gannon.webservices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.gannon.entity.AuctionDonationFavouriteHistory;
import com.gannon.entity.AuctionTransaction;
import com.gannon.entity.AuctionTransactionHistory;
import com.gannon.entity.DonationTransaction;
import com.gannon.entity.ProductImage;
import com.gannon.entity.Users;

@Path("/allAuctionDonationsService")
public class AllAuctionDonationsService {

	@Path("/list")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	@POST
	public Response list(final AuctionOrDonationListServiceReq input) {
		try {
			final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			List<AllAuctionDOnationListServiceRes> results = new ArrayList<AllAuctionDOnationListServiceRes>(0);
			final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (input.getAuctionOrDonation().equalsIgnoreCase("auction")) {
				List<AuctionTransaction> list = new ArrayList<>(0);
				if (input.getSearchString() == null) {
					list = em.createNativeQuery(
							"select * from auction_transaction where auction_status=:st and auction_created_by!=:cby and "
									+ "auction_created_by IN (select user_id from users where f_active='Y') "
									+ "order by auction_close_date asc LIMIT :lim OFFSET :offset",
							AuctionTransaction.class).setParameter("st", "OPEN").setParameter("cby", input.getUserId())
							.setParameter("lim", input.getLimit()).setParameter("offset", input.getOffset())
							.getResultList();
				} else {
					list = em.createNativeQuery(
							"select * from auction_transaction where auction_status=:st and auction_created_by!=:cby "
									+ " and auction_created_by IN (select user_id from users where f_active='Y') "
									+ " and product_name like '%" + input.getSearchString() + "%'"
									+ "order by auction_close_date asc LIMIT :lim OFFSET :offset",
							AuctionTransaction.class).setParameter("st", "OPEN").setParameter("cby", input.getUserId())
							.setParameter("lim", input.getLimit()).setParameter("offset", input.getOffset())
							.getResultList();
				}

				List<Integer> ids = list.stream().map(it -> it.getAuctionTransactionId()).collect(Collectors.toList());
				Map<Integer, String> imgMap = new HashMap<>(0);
				Map<Integer, Boolean> favMap = new HashMap<>(0);
				if (!ids.isEmpty()) {
					List<ProductImage> imgList = em
							.createQuery("from ProductImage where auctionTransaction is not null "
									+ "and auctionTransaction.auctionTransactionId IN (:ids) order by productImageId asc")
							.setParameter("ids", ids).getResultList();
					for (ProductImage img : imgList) {
						if (!imgMap.containsKey(img.getAuctionTransaction().getAuctionTransactionId()))
							imgMap.put(img.getAuctionTransaction().getAuctionTransactionId(), img.getImagePath());
					}

					List<AuctionDonationFavouriteHistory> favList = em
							.createQuery("from AuctionDonationFavouriteHistory where auctionTransaction is not null "
									+ " and auctionTransaction.auctionTransactionId IN (:ids) and users.userId=:uid order by auctionDonationFavouriteHistoryId desc")
							.setParameter("uid", input.getUserId()).setParameter("ids", ids).getResultList();

					for (AuctionDonationFavouriteHistory his : favList) {
						if (!favMap.containsKey(his.getAuctionTransaction().getAuctionTransactionId())) {
							favMap.put(his.getAuctionTransaction().getAuctionTransactionId(), true);
						}
					}

				}

				for (AuctionTransaction at : list) {
					AllAuctionDOnationListServiceRes res = new AllAuctionDOnationListServiceRes();
					res.setProductName(at.getProductName());
					res.setAuctionId(at.getAuctionTransactionId());
					res.setClosingDate(sdf.format(at.getAuctionCloseDate()));
					res.setAuctionAmount(at.getAuctionAmount());
					if (!imgMap.isEmpty()) {
						if (imgMap.containsKey(at.getAuctionTransactionId())) {
							res.setImageUrl("img/" + imgMap.get(at.getAuctionTransactionId()));
						}
					}
					if (!favMap.isEmpty()) {
						if (favMap.containsKey(at.getAuctionTransactionId())) {
							res.setFavouriteCheck(true);
						}
					}

					results.add(res);
				}
			} else {
				List<DonationTransaction> list = new ArrayList<>(0);
				if (input.getSearchString() == null) {
					list = em.createNativeQuery(
							"select * from donation_transaction where  donation_product_status=:st and donation_created_by!=:dcb"
									+ " and donation_created_by IN (select user_id from users where f_active='Y') order by donation_close_date desc LIMIT :lim OFFSET :offset",
							DonationTransaction.class).setParameter("st", "OPEN").setParameter("dcb", input.getUserId())
							.setParameter("lim", input.getLimit()).setParameter("offset", input.getOffset())
							.getResultList();
				} else {
					list = em.createNativeQuery(
							"select * from donation_transaction where  donation_product_status=:st and donation_created_by!=:dcb and "
									+ " donation_created_by IN (select user_id from users where f_active='Y') "
									+ " and product_name like '%" + input.getSearchString() + "%'"
									+ " order by donation_close_date desc LIMIT :lim OFFSET :offset",
							DonationTransaction.class).setParameter("st", "OPEN").setParameter("dcb", input.getUserId())
							.setParameter("lim", input.getLimit()).setParameter("offset", input.getOffset())
							.getResultList();
				}

				List<Integer> ids = list.stream().map(it -> it.getDonationTransactionId()).collect(Collectors.toList());
				Map<Integer, String> imgMap = new HashMap<>(0);
				Map<Integer, Boolean> favMap = new HashMap<>(0);
				if (!ids.isEmpty()) {
					List<ProductImage> imgList = em
							.createQuery("from ProductImage where donationTransaction is not null "
									+ "and donationTransaction.donationTransactionId IN (:ids) order by productImageId asc")
							.setParameter("ids", ids).getResultList();
					for (ProductImage img : imgList) {
						if (!imgMap.containsKey(img.getDonationTransaction().getDonationTransactionId()))
							imgMap.put(img.getDonationTransaction().getDonationTransactionId(), img.getImagePath());
					}

					List<AuctionDonationFavouriteHistory> favList = em
							.createQuery("from AuctionDonationFavouriteHistory where donationTransaction is not null "
									+ " and donationTransaction.donationTransactionId IN (:ids) and users.userId=:uid order by auctionDonationFavouriteHistoryId desc")
							.setParameter("uid", input.getUserId()).setParameter("ids", ids).getResultList();

					for (AuctionDonationFavouriteHistory his : favList) {
						if (!favMap.containsKey(his.getDonationTransaction().getDonationTransactionId())) {
							favMap.put(his.getDonationTransaction().getDonationTransactionId(), true);
						}
					}
				}
				for (DonationTransaction at : list) {
					AllAuctionDOnationListServiceRes res = new AllAuctionDOnationListServiceRes();
					res.setProductName(at.getProductName());
					res.setDonationId(at.getDonationTransactionId());
					res.setClosingDate(sdf.format(at.getDonationCloseDate()));
					if (!imgMap.isEmpty()) {
						if (imgMap.containsKey(at.getDonationTransactionId())) {
							res.setImageUrl("img/" + imgMap.get(at.getDonationTransactionId()));
						}
					}
					if (!favMap.isEmpty()) {
						if (favMap.containsKey(at.getDonationTransactionId())) {
							res.setFavouriteCheck(true);
						}
					}
					results.add(res);
				}

			}
			em.getTransaction().commit();
			PersistenceManager.closeEntityManagerFactory();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(results);
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok((Object) pojo).build();
		} catch (

		Exception e) {
			e.printStackTrace();
			final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setError("Unable to process the request");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok((Object) pojo2).build();
		}
	}

	@Path("/update")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	@POST
	public Response list(final AllAuctionAmountUpdateRequest input) {
		try {
			final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			Users user = em.find(Users.class, input.getUserId());

			if (user.isfAdmin()) {
				ErrorMessagePojo pojo2 = new ErrorMessagePojo();
				pojo2.setError("No permission to admin to sell the products");
				pojo2.setStatus("failure");
				pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
				return Response.ok((Object) pojo2).build();
			}

			final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			AuctionTransaction at = em.find(AuctionTransaction.class, input.getAuctionId());
			if (input.getAuctionAmount() < at.getAuctionAmount()) {
				ErrorMessagePojo pojo2 = new ErrorMessagePojo();
				pojo2.setError("Auction amount should be greather than the current auction amount");
				pojo2.setStatus("failure");
				pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
				return Response.ok((Object) pojo2).build();
			}
			at.setAuctionAmount(input.getAuctionAmount());
			at.setAuctionPriceChangeDate(new Date());
			at.setLatestAuctionUser(user);
			em.merge(at);

			AuctionTransactionHistory his = new AuctionTransactionHistory();
			his.setAictionUser(user);
			his.setAuctionAmount(input.getAuctionAmount());
			his.setAuctionPriceChangeDate(new Date());
			his.setAuctionTransaction(at);
			em.persist(his);

			em.getTransaction().commit();
			PersistenceManager.closeEntityManagerFactory();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage("Successfully updated the auction amount");
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok((Object) pojo).build();
		} catch (Exception e) {
			e.printStackTrace();
			final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setError("Unable to process the request");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok((Object) pojo2).build();
		}
	}

	@Path("/details")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	@POST
	public Response details(final AuctionDonationDetailsRequest input) {
		try {
			final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			AllAuctionDonationDetailsResponse res = new AllAuctionDonationDetailsResponse();
			if (input.getAuctionOrDonation().equalsIgnoreCase("auction")) {
				AuctionTransaction at = em.find(AuctionTransaction.class, input.getAuctionId());

				Users user = at.getAuctionCreatedBy();

				List<ProductImage> imgList = em
						.createQuery("from ProductImage where auctionTransaction is not null "
								+ "and auctionTransaction.auctionTransactionId =:ids order by productImageId asc")
						.setParameter("ids", input.getAuctionId()).getResultList();

				res.setProductName(at.getProductName());
				res.setAuctionAmount(at.getAuctionAmount());
				res.setAuctionCloseDate(sdf.format(at.getAuctionCloseDate()));
				res.setProductDescription(at.getProductDescription());
				res.setSellerName(user.getFirstName() + "  " + user.getLastName() != null ? user.getLastName() : "");
				res.setSellerEMail(user.getEmail());
				res.setSellerPhoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");

				List<String> images = new ArrayList<>(0);
				for (ProductImage img : imgList) {
					images.add("img/" + img.getImagePath());
				}
				res.setImagesList(images);
			} else {
				DonationTransaction at = em.find(DonationTransaction.class, input.getDonationId());
				Users user = at.getDonationCreatedBy();

				List<ProductImage> imgList = em
						.createQuery("from ProductImage where donationTransaction is not null "
								+ "and donationTransaction.donationTransactionId =:ids order by productImageId asc")
						.setParameter("ids", input.getDonationId()).getResultList();

				res.setProductName(at.getProductName());
				res.setAuctionCloseDate(sdf.format(at.getDonationCloseDate()));
				res.setProductDescription(at.getProductDescription());
				res.setSellerName(user.getFirstName() + "  " + user.getLastName() != null ? user.getLastName() : "");
				res.setSellerEMail(user.getEmail());
				res.setSellerPhoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
				List<String> images = new ArrayList<>(0);
				for (ProductImage img : imgList) {
					images.add("img/" + img.getImagePath());
				}
				res.setImagesList(images);
			}
			em.getTransaction().commit();
			PersistenceManager.closeEntityManagerFactory();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(res);
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok((Object) pojo).build();
		} catch (

		Exception e) {
			e.printStackTrace();
			final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setError("Unable to process the request");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok((Object) pojo2).build();
		}
	}

}

class AllAuctionDonationDetailsResponse {
	private String productName;
	private String productDescription;
	private String auctionCloseDate;
	private float auctionAmount;
	private List<String> imagesList = new ArrayList<>(0);
	private String sellerName;
	private String sellerEMail;
	private String sellerPhoneNumber;

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getSellerEMail() {
		return sellerEMail;
	}

	public void setSellerEMail(String sellerEMail) {
		this.sellerEMail = sellerEMail;
	}

	public String getSellerPhoneNumber() {
		return sellerPhoneNumber;
	}

	public void setSellerPhoneNumber(String sellerPhoneNumber) {
		this.sellerPhoneNumber = sellerPhoneNumber;
	}

	public List<String> getImagesList() {
		return imagesList;
	}

	public void setImagesList(List<String> imagesList) {
		this.imagesList = imagesList;
	}

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

	public String getAuctionCloseDate() {
		return auctionCloseDate;
	}

	public void setAuctionCloseDate(String auctionCloseDate) {
		this.auctionCloseDate = auctionCloseDate;
	}

	public float getAuctionAmount() {
		return auctionAmount;
	}

	public void setAuctionAmount(float auctionAmount) {
		this.auctionAmount = auctionAmount;
	}

}

class AllAuctionAmountUpdateRequest {
	private int auctionId;
	private float auctionAmount;
	private int userId;

	public int getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}

	public float getAuctionAmount() {
		return auctionAmount;
	}

	public void setAuctionAmount(float auctionAmount) {
		this.auctionAmount = auctionAmount;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}

class AllAuctionDOnationListServiceRes {
	private String productName;
	private String imageUrl;
	private int totalCount;
	private int auctionId;
	private int donationId;
	private float auctionAmount;
	private String closingDate;
	private boolean favouriteCheck;

	public boolean isFavouriteCheck() {
		return favouriteCheck;
	}

	public void setFavouriteCheck(boolean favouriteCheck) {
		this.favouriteCheck = favouriteCheck;
	}

	public float getAuctionAmount() {
		return auctionAmount;
	}

	public void setAuctionAmount(float auctionAmount) {
		this.auctionAmount = auctionAmount;
	}

	public String getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(String closingDate) {
		this.closingDate = closingDate;
	}

	public int getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}

	public int getDonationId() {
		return donationId;
	}

	public void setDonationId(int donationId) {
		this.donationId = donationId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
