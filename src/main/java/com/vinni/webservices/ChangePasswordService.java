package com.vinni.webservices;


import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.vinni.entity.Users;
import javax.ws.rs.core.Response;
import javax.ws.rs.Path;

@Path("/changePasswordService")
public class ChangePasswordService
{
    @Path("/update")
    @POST
    @Produces({ "application/json" })
    @Consumes({ "application/json" })
    public Response save(final ChangePassWordRequest input) {
        try {
            final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
            final EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            final Users user = (Users)em.find((Class)Users.class, (Object)input.getUserId());
            if (input.getNewPassword() == null || input.getConfirmPassword() == null || input.getOldPassword() == null) {
                final ErrorMessagePojo error = new ErrorMessagePojo();
                error.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
                error.setStatus("Failure");
                error.setError("Old/New/Confirm password values are required");
                return Response.ok((Object)error).build();
            }
            if (!user.getPassWord().equalsIgnoreCase(input.getOldPassword())) {
                final ErrorMessagePojo error = new ErrorMessagePojo();
                error.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
                error.setStatus("Failure");
                error.setError("Old password was wrong");
                return Response.ok((Object)error).build();
            }
            if (!input.getConfirmPassword().equalsIgnoreCase(input.getNewPassword())) {
                final ErrorMessagePojo error = new ErrorMessagePojo();
                error.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
                error.setStatus("Failure");
                error.setError("Old password and new password didn't match");
                return Response.ok((Object)error).build();
            }
            user.setPassWord(input.getConfirmPassword());
            em.merge((Object)user);
            em.getTransaction().commit();
            final SuccessMessagePojo pojo = new SuccessMessagePojo();
            pojo.setMessage((Object)"Successfully updated password");
            pojo.setStatusCode(Response.Status.OK.getStatusCode());
            pojo.setStatus("Success");
            return Response.ok((Object)pojo).build();
        }
        catch (Exception e) {
            final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
            pojo2.setError("Unable to process the request");
            pojo2.setStatus("failure");
            pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            return Response.ok((Object)pojo2).build();
        }
    }
}

