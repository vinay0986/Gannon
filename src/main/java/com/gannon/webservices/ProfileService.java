package com.gannon.webservices;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.gannon.entity.Users;

@Path("/profileService")
public class ProfileService {
    @Path("/update")
    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response save(ProfileServiceUpdateRequest input) {
        try {
            EntityManager em = PersistenceManager.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();
            Users user = (Users) em.find(Users.class, Integer.valueOf(input.getUserId()));
            user.setPassWord(input.getPassword());
            user.setPhoneNumber(input.getPhoneNumber());
            em.merge(user);
            em.getTransaction().commit();
            PersistenceManager.closeEntityManagerFactory();
            SuccessMessagePojo pojo = new SuccessMessagePojo();
            pojo.setMessage("Profile successfully updated");
            pojo.setStatusCode(Response.Status.OK.getStatusCode());
            pojo.setStatus("Success");
            return Response.ok(pojo).build();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorMessagePojo pojo2 = new ErrorMessagePojo();
            pojo2.setError("something went wrong");
            pojo2.setStatus("failure");
            pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            return Response.ok(pojo2).build();
        }
    }

    @Path("/userInfo")
    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response save(ProfileServiceRequest input) {
        try {
            EntityManager em = PersistenceManager.getEntityManagerFactory().createEntityManager();
            em.getTransaction().begin();
            Users user = (Users) em.find(Users.class, Integer.valueOf(input.getUserId()));
            ProfileServiceResponse res = new ProfileServiceResponse();
            res.setEmail(user.getEmail());
            res.setFirstName(user.getFirstName());
            res.setLastName(user.getLastName());
            res.setPassword(user.getPassWord());
            res.setPhoneNumber(user.getPhoneNumber());
            res.setStudentId(user.getStudentId());
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
            pojo2.setError("something went wrong");
            pojo2.setStatus("failure");
            pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            return Response.ok(pojo2).build();
        }
    }
}