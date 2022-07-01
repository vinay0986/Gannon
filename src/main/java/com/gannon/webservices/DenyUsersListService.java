package com.gannon.webservices;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.gannon.entity.ConstantSettings;
import com.gannon.entity.Users;

@Path("/denyUsersListService")
public class DenyUsersListService {
	/* access modifiers changed from: private */
	public String passWord;

	@Path("/list")
	@Produces({ "application/json" })
	@GET
	public Response list() {
		try {
			EntityManager em = PersistenceManager.getEntityManagerFactory().createEntityManager();
			em.getTransaction().begin();
			List<Users> list = em.createQuery("from Users where fActive is not null and fAdmin=0 order by userId desc")
					.getResultList();
			List<ApprovedUserListResponse> pojoList = new ArrayList<>(0);
			for (Users reg : list) {
				ApprovedUserListResponse p = new ApprovedUserListResponse();
				p.setEmail(reg.getEmail());
				p.setFirstName(reg.getFirstName());
				p.setLastName(reg.getLastName());
				p.setPhoneNumber(reg.getPhoneNumber());
				p.setRegistrationId(reg.getUserId().intValue());
				p.setStatus(reg.getfActive().equalsIgnoreCase("Y") ? "Activated" : "Deactivated");
				p.setStudentId(reg.getStudentId());
				pojoList.add(p);
			}
			em.getTransaction().commit();
			PersistenceManager.closeEntityManagerFactory();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(pojoList);
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok(pojo).build();
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setError("Unable to process the request");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok(pojo2).build();
		}
	}

	@Path("/save")
	@POST
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public Response save(ApprovedUserSaveRequest input) {
		try {
			EntityManager em = PersistenceManager.getEntityManagerFactory().createEntityManager();
			em.getTransaction().begin();
			Users reg = (Users) em.find(Users.class, Integer.valueOf(input.getRegistrationId()));
			reg.setfActive(input.getApproved());
			if (input.getApproved().equalsIgnoreCase("Y")) {
				reg.setActivatedDate(new Date());
				reg.setActivatedUser(input.getUserId());
			} else {
				reg.setDeActivatedDate(new Date());
				reg.setDeActivatedUser(input.getUserId());
			}
			em.merge(reg);
			em.getTransaction().commit();
			PersistenceManager.closeEntityManagerFactory();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage("Successfully Updated");
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok(pojo).build();
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setError("Unable to send the mail or something went wrong");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok(pojo2).build();
		}
	}

}
