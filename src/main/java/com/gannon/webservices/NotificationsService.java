package com.gannon.webservices;

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

import com.gannon.entity.Notifications;
import com.gannon.entity.NotificationsHistory;

@Path("/notificationsService")
public class NotificationsService {

	@Path("/count")
	@POST
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public Response count(final NotificationServiceRequest input) {
		PersistenceManager manager = new PersistenceManager();
		try {
			final EntityManagerFactory emf = manager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			Long count = (Long) em.createQuery(
					"select count(*) from Notifications where createdBy!=:uid and notificationsId not in (select notifications.notificationsId from"
							+ " NotificationsHistory where readby=:uid) ")
					.setParameter("uid", input.getUserId()).getSingleResult();
			if (count == null) {
				count = 0L;
			}
			NotificationServiceCountResponse response = new NotificationServiceCountResponse();
			response.setCount(count.intValue());
			em.getTransaction().commit();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(response);
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
		} finally {
			manager.closeEntityManagerFactory();
		}
	}

	@Path("/list")
	@POST
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public Response list(final NotificationServiceRequest input) {
		PersistenceManager manager = new PersistenceManager();
		try {
			final EntityManagerFactory emf = manager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			List<Notifications> list = em
					.createQuery("from Notifications where createdBy!=:uid order by notificationsId desc")
					.setParameter("uid", input.getUserId()).getResultList();

			List<Integer> ids = list.stream().map(it -> it.getNotificationsId()).collect(Collectors.toList());
			Map<Integer, List<NotificationsHistory>> map = new HashMap<Integer, List<NotificationsHistory>>(0);
			if (!ids.isEmpty()) {
				List<NotificationsHistory> hisList = em.createQuery(
						"from NotificationsHistory where notifications.notificationsId IN (:ids) and readby=:uid")
						.setParameter("uid", input.getUserId()).setParameter("ids", ids).getResultList();
				for (NotificationsHistory his : hisList) {
					if (!map.containsKey(his.getNotifications().getNotificationsId()))
						map.put(his.getNotifications().getNotificationsId(), new ArrayList<NotificationsHistory>(0));

					map.get(his.getNotifications().getNotificationsId()).add(his);
				}
			}
			List<NotificationServiceListResponse> pojoList = new ArrayList<>(0);
			for (Notifications n : list) {
				NotificationServiceListResponse res = new NotificationServiceListResponse();
				if (n.getAuctionTransaction() != null) {
					res.setAuctionId(n.getAuctionTransaction().getAuctionTransactionId());
					res.setAuctionOrDonation("AUCTION");
				}
				if (n.getDonationTransaction() != null) {
					res.setDonationId(n.getDonationTransaction().getDonationTransactionId());
					res.setAuctionOrDonation("DONATION");
				}
				res.setMessage(n.getMessage());
				res.setImageUrl(n.getImageUrl());
				if (map.isEmpty() || !map.containsKey(n.getNotificationsId()))
					res.setStatus("UNREAD");
				else
					res.setStatus("READ");
				pojoList.add(res);
			}

			em.getTransaction().commit();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(pojoList);
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
		} finally {
			manager.closeEntityManagerFactory();
		}
	}

	@Path("/update")
	@POST
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public Response update(final NotificationServiceReadUpdateReq input) {
		PersistenceManager manager = new PersistenceManager();
		try {
			final EntityManagerFactory emf = manager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			Notifications notificationObj = null;
			if (input.getAuctionId() != 0)
				notificationObj = (Notifications) em.createQuery(
						"from Notifications where auctionTransaction is not null and auctionTransaction.auctionTransactionId=:id")
						.setParameter("id", input.getAuctionId()).getSingleResult();
			if (input.getDonationId() != 0)
				notificationObj = (Notifications) em.createQuery(
						"from Notifications where donationTransaction is not null and donationTransaction.donationTransactionId=:id")
						.setParameter("id", input.getDonationId()).getSingleResult();

			NotificationsHistory his = new NotificationsHistory();
			his.setNotifications(notificationObj);
			his.setReadby(input.getUserId());
			his.setReadDate(new Date());
			em.persist(his);

			Long count = (Long) em.createQuery(
					"select count(*) from Notifications where createdBy!=:uid and notificationsId not in (select notifications.notificationsId from"
							+ " NotificationsHistory where readby=:uid) ")
					.setParameter("uid", input.getUserId()).getSingleResult();
			if (count == null) {
				count = 0L;
			}
			NotificationServiceCountResponse response = new NotificationServiceCountResponse();
			response.setCount(count.intValue());

			em.getTransaction().commit();

			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(response);
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
		} finally {
			manager.closeEntityManagerFactory();
		}
	}
}

class NotificationServiceReadUpdateReq {
	private boolean read;
	private int auctionId;
	private int donationId;
	private int userId;

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}

class NotificationServiceListResponse {
	private String message;
	private String imageUrl;
	private String status;
	private int auctionId;
	private int donationId;
	private String auctionOrDonation;

	public String getAuctionOrDonation() {
		return auctionOrDonation;
	}

	public void setAuctionOrDonation(String auctionOrDonation) {
		this.auctionOrDonation = auctionOrDonation;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

}

class NotificationServiceRequest {
	private int userId;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}

class NotificationServiceCountResponse {
	private int count;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
