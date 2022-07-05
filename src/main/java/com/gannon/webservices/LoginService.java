package com.gannon.webservices;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.gannon.entity.Users;

@Path("/login")
public class LoginService {
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	@POST
	public Response save(LoginServiceRequest input) {
		try {
			EntityManager em = PersistenceManager.getEntityManagerFactory().createEntityManager();
			em.getTransaction().begin();
			Users user = (Users) em.createQuery("from Users where email=:em and passWord=:pwd")
					.setParameter("em", input.getUserName()).setParameter("pwd", input.getPassword()).getSingleResult();
			LoginServiceResponse res = new LoginServiceResponse();
			res.setUserId(user.getUserId().intValue());
			res.setMessage("Login Success");
			res.setUserName(user.getFirstName());
			res.setAdminFlag(user.isfAdmin());

			user.setToken(input.getToken());
			em.merge(user);

			em.getTransaction().commit();
			PersistenceManager.closeEntityManagerFactory();
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(res);
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok(pojo).build();
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setError("Invalid UserName/Password");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok(pojo2).build();
		}
	}
}
