package com.gannon.webservices;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.gannon.FirebaseMessaging.FirebseCloudMessagingClass;
import com.gannon.entity.ConstantSettings;
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

			ConstantSettings settings = null;
			try {
				settings = (ConstantSettings) em
						.createQuery("from ConstantSettings where key='accepted domain' and fActive='Y'")
						.getSingleResult();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String arr[] = input.getEmail().split("@");
			if (arr.length == 1) {
				ErrorMessagePojo pojo = new ErrorMessagePojo();
				pojo.setError("Invalid email format");
				pojo.setStatus("failure");
				pojo.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
				return Response.ok(pojo).build();
			}
			if (settings != null && settings.getValue() != null && !settings.getValue().equalsIgnoreCase("")) {
				if (!arr[1].equalsIgnoreCase(settings.getValue())) {
					ErrorMessagePojo pojo = new ErrorMessagePojo();
					pojo.setError("Only  " + settings.getValue() + " domain is allowed");
					pojo.setStatus("failure");
					pojo.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
					return Response.ok(pojo).build();
				}
			}

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

			// Email Code
			StringBuilder sb = new StringBuilder();
			sb.append("Dear Admin\n");
			sb.append(reg.getFirstName() + " " + reg.getLastName()
					+ ", has requested you to Activate his/her account in Gannon Auction Shop."
					+ "Please verify below details and for approving or denying user, please login to Gannon Auction Shop mobile app.");
			sb.append(
					"\n <html><table border='2'><tr><td style='text-align:center;'>Student Id</td><td style='text-align:center;'>Email</td><td style='text-align:center;'>First Name</td><td style='text-align:center;'>Last Name</td><td style='text-align:center;'>Phone Number</td></tr><tr><td style='text-align:center;'>"
							+ reg.getStudentId() + "</td><td style='text-align:center;'>" + reg.getEmail()
							+ "</td><td style='text-align:center;'>" + reg.getFirstName()
							+ "</td><td style='text-align:center;'>" + reg.getLastName()
							+ "</td><td style='text-align:center;'>" + reg.getPhoneNumber()
							+ "</td></tr></table></html>");
			sb.append("\n Thank you.");
			sb.append("\n NOTE: This is a system generated email. Please do not reply.");

			Users adminUser = ((Users) em.createQuery("from Users where fAdmin=1").getSingleResult());
			new EmailSend().emailSend(em, "Gannon Auction Shop – User Activation Request.", sb.toString(),
					adminUser.getEmail());

			// Notification Code

			FirebseCloudMessagingClass fcm = new FirebseCloudMessagingClass();
			List<String> tokenList = new ArrayList<String>(0);
			tokenList.add(adminUser.getToken());
			StringBuilder sd = new StringBuilder();
			sd.append("Email: " + reg.getEmail()+"\n");
			sd.append("Name: " + reg.getFirstName() + " " + reg.getLastName()+"\n");
			sd.append("Student ID:" + reg.getStudentId());

			fcm.sendPushNotificationToMultiple(tokenList, "New AuctionNew Registeration Request", sd.toString(), null);

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
