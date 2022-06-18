// 
// Decompiled by Procyon v0.5.36
// 

package com.vinni.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import java.util.Date;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.vinni.entity.Users;
import java.util.ArrayList;
import javax.ws.rs.core.Response;
import javax.ws.rs.Path;

@Path("/approvedService")
public class ApprovedUsersService
{
    private String passWord;
    
    @Path("/list")
    @GET
    @Produces({ "application/json" })
    public Response list() {
        try {
            final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
            final EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            final List<Users> list = (List<Users>)em.createQuery("from Users where fActive is  null order by registeredDate desc").getResultList();
            final List<ApprovedUserListResponse> pojoList = new ArrayList<ApprovedUserListResponse>(0);
            for (final Users reg : list) {
                final ApprovedUserListResponse p = new ApprovedUserListResponse();
                p.setEmail(reg.getEmail());
                p.setFirstName(reg.getFirstName());
                p.setLastName(reg.getLastName());
                p.setPhoneNumber(reg.getPhoneNumber());
                p.setRegistrationId(reg.getUserId());
                p.setStudentId(reg.getStudentId());
                pojoList.add(p);
            }
            em.getTransaction().commit();
            final SuccessMessagePojo pojo = new SuccessMessagePojo();
            pojo.setMessage((Object)pojoList);
            pojo.setStatusCode(Response.Status.OK.getStatusCode());
            pojo.setStatus("Success");
            return Response.ok((Object)pojo).build();
        }
        catch (Exception e) {
            e.printStackTrace();
            final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
            pojo2.setMessage("Unable to process the request");
            pojo2.setStatus("failure");
            pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            return Response.ok((Object)pojo2).build();
        }
    }
    
    @Path("/save")
    @POST
    @Produces({ "application/json" })
    @Consumes({ "application/json" })
    public Response save(final ApprovedUserSaveRequest input) {
        try {
            final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
            final EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            final Users reg = (Users)em.find((Class)Users.class, (Object)input.getRegistrationId());
            reg.setfActive(input.getApproved());
            if (input.getApproved().equalsIgnoreCase("Y")) {
                reg.setActivatedDate(new Date());
                reg.setActivatedUser(input.getUserId());
                final StringBuilder sb = new StringBuilder();
                sb.append("Dear  " + reg.getFirstName() + "\n\n");
                sb.append("Welcome to Online Auction Shop for Campus Students!!! \n\n");
                sb.append("Please use below credentails to login \n\n");
                sb.append("User Name:" + reg.getEmail() + "\n\n");
                sb.append("Password:" + reg.getPassWord() + "\n\n");
                sb.append("Regards:\n Online Auction Shop for campus studes team");
                sb.append("\n\n\tNOTE:This is a system-generated e-mail, Please don't reply to this message");
                new EmailSend().emailSend(em, "User Account Details", sb.toString(), reg.getEmail());
            }
            else {
                reg.setDeActivatedDate(new Date());
                reg.setDeActivatedUser(input.getUserId());
            }
            em.merge((Object)reg);
            em.getTransaction().commit();
            final SuccessMessagePojo pojo = new SuccessMessagePojo();
            pojo.setMessage((Object)"Successfully Saved and user details sent to user");
            pojo.setStatusCode(Response.Status.OK.getStatusCode());
            pojo.setStatus("Success");
            return Response.ok((Object)pojo).build();
        }
        catch (Exception e) {
            e.printStackTrace();
            final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
            pojo2.setMessage("Unable to send the mail or something went wrong");
            pojo2.setStatus("failure");
            pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
            return Response.ok((Object)pojo2).build();
        }
    }
}
