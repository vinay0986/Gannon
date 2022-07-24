package com.gannon.webservices;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.gannon.entity.Users;

@Path("/forgotPasswordService")
public class ForgotPasswordService {
	private String passWord;

	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@POST
	public Response save(ForgotPasswordServiceRequest input) {
		PersistenceManager manager = new PersistenceManager();
		try {
			EntityManager em = manager.getEntityManagerFactory().createEntityManager();
			em.getTransaction().begin();
			try {
				Users users = (Users) em.createQuery("from Users where email=:e and fActive='Y'")
						.setParameter("e", input.getEmail()).getSingleResult();
				StringBuilder sb = new StringBuilder();
				sb.append("Dear " + users.getFirstName() + " " + users.getLastName() + ",\n");
				sb.append("Thank you for contacting Gannon Auction Shop support. Please find your password details.\n");
				sb.append("Password:" + users.getPassWord() + "\n");
				sb.append("Please do not share your login ID or password to anyone.\n");
				sb.append("\n\tNOTE: This is a system generated email. Please do not reply.");
				new EmailSend().emailSend(em, "Gannon Auction Shop – Forgot Password Request.", sb.toString(),
						users.getEmail());
				em.getTransaction().commit();
				SuccessMessagePojo pojo = new SuccessMessagePojo();
				pojo.setMessage("Password successfully sent to registered email.");
				pojo.setStatusCode(Response.Status.OK.getStatusCode());
				pojo.setStatus("Success");
				return Response.ok(pojo).build();
			} catch (Exception e) {
				ErrorMessagePojo pojo2 = new ErrorMessagePojo();
				pojo2.setError("Email ID not found! Contact Gannon IT Support.");
				pojo2.setStatus("failure");
				pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
				return Response.ok(pojo2).build();
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			ErrorMessagePojo pojo3 = new ErrorMessagePojo();
			pojo3.setError("Unable to send the mail or something went wrong");
			pojo3.setStatus("failure");
			pojo3.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok(pojo3).build();
		} finally {
			manager.closeEntityManagerFactory();
		}
	}
}
