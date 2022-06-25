package com.vinni.webservices;

import com.vinni.entity.Users;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/forgotPasswordService")
public class ForgotPasswordService {
    private String passWord;

    @Consumes({"application/json"})
    @Produces({"application/json"})
    @POST
    public Response save(ForgotPasswordServiceRequest input) {
        try {
            EntityManager em = PersistenceManager.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();
            try {
                Users users = (Users) em.createQuery("from Users where email=:e and fActive='Y'").setParameter("e", input.getEmail()).getSingleResult();
                StringBuilder sb = new StringBuilder();
                sb.append("Dear " + users.getFirstName() + ",\n\n");
                sb.append("Your password is :" + users.getPassWord());
                sb.append("\n\n\tNOTE:This is a system-generated e-mail, Please don't reply to this message");
                new EmailSend().emailSend(em, "Password Details", sb.toString(), users.getEmail());
                em.getTransaction().commit();
                SuccessMessagePojo pojo = new SuccessMessagePojo();
                pojo.setMessage("Password successfully sent to your registered email");
                pojo.setStatusCode(Response.Status.OK.getStatusCode());
                pojo.setStatus("Success");
                return Response.ok(pojo).build();
            } catch (Exception e) {
                ErrorMessagePojo pojo2 = new ErrorMessagePojo();
                pojo2.setError("Provided Email was not registered/Invalid");
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
        }
    }
}
