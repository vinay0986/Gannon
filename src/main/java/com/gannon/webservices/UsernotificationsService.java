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
import com.gannon.entity.Users;

@Path("/usernotificationsService")
public class UsernotificationsService {

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

			Long count = (Long) em
					.createQuery("select count(*) from Users where fActive is null and notificationRead=0 ")
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
			List<Users> list = em.createQuery("from Users where fActive is null order by userId desc")
					.setParameter("uid", input.getUserId()).getResultList();

			List<UserNotificationDetailsResponse> pojoList = new ArrayList<>(0);
			for (Users n : list) {
				UserNotificationDetailsResponse res = new UserNotificationDetailsResponse();
				StringBuilder sd = new StringBuilder();
				sd.append("\tNew Registration Request: \n");
				sd.append("Email: " + n.getEmail() + "\n");
				sd.append("Name: " + n.getFirstName() + " " + (n.getLastName() != null ? n.getLastName() : "") + "\n");
				sd.append("Student ID:" + n.getStudentId());
				res.setMessage(sd.toString());
				if (!n.isNotificationRead())
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
	public Response update(final UserNotificationUpdateRequest input) {
		PersistenceManager manager = new PersistenceManager();
		try {
			final EntityManagerFactory emf = manager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			Users notificationObj = em.find(Users.class, input.getRegisterId());

			notificationObj.setNotificationRead(true);
			em.merge(notificationObj);

			Long count = (Long) em
					.createQuery("select count(*) from Users where fActive is null and notificationRead=0 ")
					.setParameter("uid", input.getRegisterId()).getSingleResult();
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

class UserNotificationUpdateRequest {
	private Integer registerId;
	private int read;

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public Integer getRegisterId() {
		return registerId;
	}

	public void setRegisterId(Integer registerId) {
		this.registerId = registerId;
	}
}

class UserNotificationDetailsResponse {
	private String message;
	private Integer registerId;
	private String status;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getRegisterId() {
		return registerId;
	}

	public void setRegisterId(Integer registerId) {
		this.registerId = registerId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
