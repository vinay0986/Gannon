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
import com.gannon.entity.DonationTransaction;
import com.gannon.entity.ProductImage;
import com.gannon.entity.Users;

@Path("/myFavouriteService")
public class MyFavouriteService {

	@Path("/update")
	@POST
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public Response save(final FavouriteUpateServiceReq input) {
		try {
			final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			Users user = em.find(Users.class, input.getUserId());
			if (input.getAuctionId() != 0) {
				AuctionTransaction at = em.find(AuctionTransaction.class, input.getAuctionId());
				if (input.isOnOffFlag()) {
					AuctionDonationFavouriteHistory his = new AuctionDonationFavouriteHistory();
					his.setAuctionTransaction(at);
					his.setCreatedDate(new Date());
					his.setDonationTransaction(null);
					his.setUsers(user);
					em.persist(his);
				} else {
					int his = em.createQuery(
							"delete from AuctionDonationFavouriteHistory where auctionTransaction.auctionTransactionId=:aid and users.userId=:uid")
							.setParameter("aid", at.getAuctionTransactionId()).setParameter("uid", user.getUserId())
							.executeUpdate();
				}
			} else {
				DonationTransaction dt = em.find(DonationTransaction.class, input.getDonationId());
				if (input.isOnOffFlag()) {
					AuctionDonationFavouriteHistory his = new AuctionDonationFavouriteHistory();
					his.setAuctionTransaction(null);
					his.setCreatedDate(new Date());
					his.setDonationTransaction(dt);
					his.setUsers(user);
					em.persist(his);
				} else {
					int his = em.createQuery(
							"delete from AuctionDonationFavouriteHistory where donationTransaction.donationTransactionId=:aid and users.userId=:uid")
							.setParameter("aid", dt.getDonationTransactionId()).setParameter("uid", user.getUserId())
							.executeUpdate();
				}
			}
			em.getTransaction().commit();
			PersistenceManager.closeEntityManagerFactory();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage("Successfully Updated the favourite details");
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok((Object) pojo).build();

		} catch (Exception e) {
			e.printStackTrace();
			final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setError("something went wrong");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok((Object) pojo2).build();
		}

	}

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
				List<AuctionTransaction> list = em.createNativeQuery(
						"select * from auction_transaction where auction_status=:st and  auction_transaction_id IN (select auction_transaction_id from  "
								+ " auction_donation_favourite_history where user_id=:cby) "
								+ "order by auction_close_date asc LIMIT :lim OFFSET :offset",
						AuctionTransaction.class).setParameter("st", "OPEN").setParameter("cby", input.getUserId())
						.setParameter("lim", input.getLimit()).setParameter("offset", input.getOffset())
						.getResultList();

				List<Integer> ids = list.stream().map(it -> it.getAuctionTransactionId()).collect(Collectors.toList());
				Map<Integer, String> imgMap = new HashMap<>(0);
				if (!ids.isEmpty()) {
					List<ProductImage> imgList = em
							.createQuery("from ProductImage where auctionTransaction is not null "
									+ "and auctionTransaction.auctionTransactionId IN (:ids) order by productImageId asc")
							.setParameter("ids", ids).getResultList();
					for (ProductImage img : imgList) {
						if (!imgMap.containsKey(img.getAuctionTransaction().getAuctionTransactionId()))
							imgMap.put(img.getAuctionTransaction().getAuctionTransactionId(), img.getImagePath());
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
					results.add(res);
				}
			} else {
				List<DonationTransaction> list = em
						.createNativeQuery("select * from donation_transaction where  donation_product_status=:st "
								+ "and donation_transaction_id IN (select donation_transaction_id from auction_donation_favourite_history where user_id=:cby) "
								+ "order by donation_close_date desc LIMIT :lim OFFSET :offset",
								DonationTransaction.class)
						.setParameter("st", "OPEN").setParameter("cby", input.getUserId())
						.setParameter("lim", input.getLimit()).setParameter("offset", input.getOffset())
						.getResultList();

				List<Integer> ids = list.stream().map(it -> it.getDonationTransactionId()).collect(Collectors.toList());
				Map<Integer, String> imgMap = new HashMap<>(0);
				if (!ids.isEmpty()) {
					List<ProductImage> imgList = em
							.createQuery("from ProductImage where donationTransaction is not null "
									+ "and donationTransaction.donationTransactionId IN (:ids) order by productImageId asc")
							.setParameter("ids", ids).getResultList();
					for (ProductImage img : imgList) {
						if (!imgMap.containsKey(img.getDonationTransaction().getDonationTransactionId()))
							imgMap.put(img.getDonationTransaction().getDonationTransactionId(), img.getImagePath());
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
		} catch (Exception e) {
			e.printStackTrace();
			final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setError("Unable to process the request");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok((Object) pojo2).build();
		}
	}
}

class FavouriteUpateServiceReq {
	private int donationId;
	private int auctionId;
	private boolean onOffFlag;
	private int userId;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getDonationId() {
		return donationId;
	}

	public void setDonationId(int donationId) {
		this.donationId = donationId;
	}

	public int getAuctionId() {
		return auctionId;
	}

	public void setAuctionId(int auctionId) {
		this.auctionId = auctionId;
	}

	public boolean isOnOffFlag() {
		return onOffFlag;
	}

	public void setOnOffFlag(boolean onOffFlag) {
		this.onOffFlag = onOffFlag;
	}

}
