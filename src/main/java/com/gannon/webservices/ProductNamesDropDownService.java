package com.gannon.webservices;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.gannon.entity.AuctionTransaction;
import com.gannon.entity.DonationTransaction;
import com.gannon.entity.Users;

@Path("/productNamesDropDownService")
public class ProductNamesDropDownService {

	@POST
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public Response save(final UsersListDropDownServiceRequest input) {
		try {
			final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			List<AuctionTransaction> list = new ArrayList<>(0);
			List<DonationTransaction> list1 = new ArrayList<>(0);

			Users user = em.find(Users.class, input.getUserId());
			if (input.getType().equalsIgnoreCase("Auction")) {
				StringBuilder sb = new StringBuilder(
						"select * from auction_transaction where auction_created_by IN (select user_id from users where f_active='Y') "
								+ "and auction_created_by!=:cby ");
				if (!user.isfAdmin())
					sb.append(" and auction_status =:st ");
				if (input.getSearchValue() != null)
					sb.append(" and product_name like '%" + input.getSearchValue() + "%'");

				if (!user.isfAdmin())
					sb.append(" order by auction_close_date asc");
				else
					sb.append(" order by auction_close_date desc");

				Query q = em.createNativeQuery(sb.toString(), AuctionTransaction.class);

				if (!user.isfAdmin())
					q.setParameter("st", "OPEN");

				q.setParameter("cby", input.getUserId());

				list = q.getResultList();
			} else {
				StringBuilder sb = new StringBuilder(
						"select * from donation_transaction where donation_created_by IN (select user_id from users where f_active='Y') "
								+ "and donation_created_by!=:dcb ");
				if (!user.isfAdmin())
					sb.append(" and donation_product_status =:st ");
				if (input.getSearchValue() != null)
					sb.append(" and product_name like '%" + input.getSearchValue() + "%'");

				if (!user.isfAdmin())
					sb.append(" order by donation_close_date asc");
				else
					sb.append(" order by donation_close_date desc");

				Query q = em.createNativeQuery(sb.toString(), DonationTransaction.class);

				if (!user.isfAdmin())
					q.setParameter("st", "OPEN");

				q.setParameter("dcb", input.getUserId());

				list1 = q.getResultList();
			}
			em.getTransaction().commit();
			PersistenceManager.closeEntityManagerFactory();
			List<String> names = new ArrayList<>(0);
			if (input.getType().equalsIgnoreCase("Auction")) {
				names = list.stream().map(it -> it.getProductName()).collect(Collectors.toList());
			} else {
				names = list1.stream().map(it -> it.getProductName()).collect(Collectors.toList());
			}
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
