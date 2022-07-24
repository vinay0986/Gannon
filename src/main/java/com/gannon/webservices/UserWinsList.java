package com.gannon.webservices;

import java.util.ArrayList;
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

import com.gannon.entity.AuctionTransaction;
import com.gannon.entity.DonationTransaction;
import com.gannon.entity.ProductImage;

@Path("/userWinsList")
public class UserWinsList {

	@Path("/list")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	@POST
	public Response list(final AuctionOrDonationListServiceReq input) {
		PersistenceManager manager = new PersistenceManager();
		try {
			final EntityManagerFactory emf = manager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			List<AuctionDOnationListServiceRes> results = new ArrayList<AuctionDOnationListServiceRes>(0);
			if (input.getAuctionOrDonation().equalsIgnoreCase("auction")) {
				List<AuctionTransaction> list = em.createNativeQuery(
						"select * from auction_transaction where latest_auction_user=:usr and auction_status='CLOSED' "
								+ " order by auction_transaction_id desc LIMIT :lim OFFSET :offset",
						AuctionTransaction.class).setParameter("usr", input.getUserId())
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
					AuctionDOnationListServiceRes res = new AuctionDOnationListServiceRes();
					res.setProductName(at.getProductName());
					res.setAuctionId(at.getAuctionTransactionId());
					if (!imgMap.isEmpty()) {
						if (imgMap.containsKey(at.getAuctionTransactionId())) {
							res.setImageUrl("img/" + imgMap.get(at.getAuctionTransactionId()));
						}
					}
					results.add(res);
				}
			} else {
				List<DonationTransaction> list = em
						.createNativeQuery(
								"select * from donation_transaction where donation_created_by=:usr "
										+ " order by donation_transaction_id desc LIMIT :lim OFFSET :offset",
								DonationTransaction.class)
						.setParameter("usr", input.getUserId()).setParameter("lim", input.getLimit())
						.setParameter("offset", input.getOffset()).getResultList();

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
					AuctionDOnationListServiceRes res = new AuctionDOnationListServiceRes();
					res.setProductName(at.getProductName());
					res.setDonationId(at.getDonationTransactionId());

					if (!imgMap.isEmpty()) {
						if (imgMap.containsKey(at.getDonationTransactionId())) {
							res.setImageUrl("img/" + imgMap.get(at.getDonationTransactionId()));
						}
					}
					results.add(res);
				}

			}
			em.getTransaction().commit();
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
		} finally {
			manager.closeEntityManagerFactory();
		}
	}
}
