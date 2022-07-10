package com.gannon.webservices;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.gannon.entity.Users;

@Path("/usersListDropDownService")
public class UsersListDropDownService {

	@POST
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public Response save(final UsersListDropDownServiceRequest input) {
		try {
			final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			List<Users> list = new ArrayList<>(0);
			if (input.getType().equalsIgnoreCase("Newly")) {
				if (input.getSearchValue() == null) {
					list = em.createQuery("from Users where fActive is null and fAdmin=0 order by registeredDate desc")
							.getResultList();
				} else {
					list = em.createQuery(
							"from Users where fActive is  null and fAdmin=0 and (firstName like :sea or lastName like :sea or email like :sea or studentId like :sea) "
									+ " order by registeredDate desc")
							.setParameter("sea", "%" + input.getSearchValue() + "%").getResultList();
				}
			} else {
				if (input.getSearchValue() == null) {
					list = em.createQuery("from Users where fActive is not null and fAdmin=0 order by userId desc")
							.getResultList();
				} else {
					list = em.createQuery(
							"from Users where fActive is not null and fAdmin=0 and (firstName like :sea or lastName like :sea or email like :sea or studentId like :sea)"
									+ " order by userId desc")
							.setParameter("sea", "%" + input.getSearchValue() + "%").getResultList();
				}
			}
			em.getTransaction().commit();
			PersistenceManager.closeEntityManagerFactory();
			List<String> names = list.stream().map(it -> it.getFirstName() + " " + it.getLastName())
					.collect(Collectors.toList());
			SuccessMessagePojo pojo = new SuccessMessagePojo();
			pojo.setMessage(names);
			pojo.setStatusCode(Response.Status.OK.getStatusCode());
			pojo.setStatus("Success");
			return Response.ok((Object) pojo).build();
		} catch (Exception e) {
			e.printStackTrace();
			final ErrorMessagePojo pojo2 = new ErrorMessagePojo();
			pojo2.setError("Unable to process the request");
			pojo2.setStatus("failure");
			pojo2.setStatusCode(Response.Status.BAD_REQUEST.getStatusCode());
			return Response.ok((Object) pojo2).build();
		}
	}
}

class UsersListDropDownServiceRequest {
	private String type;
	private String searchValue;
	private int userId;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

}