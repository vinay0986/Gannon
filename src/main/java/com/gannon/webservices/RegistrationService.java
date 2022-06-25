package com.gannon.webservices;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.gannon.entity.Users;

@Path("/registration")
public class RegistrationService {
	@Path("/save")
	@POST
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public Response save(RegistrationSaveRequest input) {
		try {
			EntityManager em = PersistenceManager.getEntityManagerFactory().createEntityManager();
			em.getTransaction().begin();
			if (!em.createQuery("from Users where email=:em").setParameter("em", input.getEmail()).getResultList()
					.isEmpty()) {
				ErrorMessagePojo pojo = new ErrorMessagePojo();
				pojo.setError("Provided Email is already registred");
				pojo.setStatus("failure");
				pojo.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
				return Response.ok(pojo).build();
			}
			Users reg = new Users();
			reg.setEmail(input.getEmail());
			reg.setFirstName(input.getFirstName());
			reg.setLastName(input.getLastName());
			reg.setPhoneNumber(input.getPhoneNumber());
			reg.setPassWord(input.getPassword());
			reg.setRegisteredDate(new Date());
			reg.setStudentId(input.getStudentId());
			em.persist(reg);
			StringBuilder sb = new StringBuilder();
			sb.append("Dear Admin\n\n");
			sb.append(
					"\n\n<html><table border='2'><tr><td style='text-align:center;'>Student Id</td><td style='text-align:center;'>Email</td><td style='text-align:center;'>First Name</td><td style='text-align:center;'>Last Name</td><td style='text-align:center;'>Phone Number</td></tr><tr><td style='text-align:center;'>"
							+ reg.getStudentId() + "</td><td style='text-align:center;'>" + reg.getEmail()
							+ "</td><td style='text-align:center;'>" + reg.getFirstName()
							+ "</td><td style='text-align:center;'>" + reg.getLastName()
							+ "</td><td style='text-align:center;'>" + reg.getPhoneNumber()
							+ "</td></tr></table></html>");
			sb.append("\n\n\tNOTE:This is a system-generated e-mail, Please don't reply to this message");
			new EmailSend().emailSend(em, "New Registration Details", sb.toString(),
					((Users) em.createQuery("from Users where fAdmin=1").getSingleResult()).getEmail());
			em.getTransaction().commit();
			PersistenceManager.closeEntityManagerFactory();
			SuccessMessagePojo pojo2 = new SuccessMessagePojo();
			pojo2.setMessage("Actiovation mail sent successfully");
			pojo2.setStatusCode(Response.Status.OK.getStatusCode());
			pojo2.setStatus("Success");
			return Response.ok(pojo2).build();
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMessagePojo pojo3 = new ErrorMessagePojo();
			pojo3.setError("Unable to send the mail or something went wrong");
			pojo3.setStatus("failure");
			pojo3.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok(pojo3).build();
		}
	}

	
}
