package com.gannon.Quartz;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.gannon.entity.AuctionTransaction;
import com.gannon.webservices.PersistenceManager;

public class AuctionCloseJob implements Job {

	@Override
	public void execute(final JobExecutionContext ctx) throws JobExecutionException {

		final EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();
		final EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		List<AuctionTransaction> list = em.createNativeQuery(
				"select * from auction_transaction where auction_status=:st and DATE(auction_close_date) <= CURDATE()",
				AuctionTransaction.class).setParameter("st", "OPEN").getResultList();
		System.out.println("--------------size is ------------------" + list.size());

		if (!list.isEmpty()) {
			List<Integer> ids = list.stream().map(it -> it.getAuctionTransactionId()).collect(Collectors.toList());
			int qu = em
					.createQuery(
							"update AuctionTransaction set auctionStatus='CLOSED' where auctionTransactionId IN (:ids)")
					.setParameter("ids", ids).executeUpdate();
			
		}
		em.getTransaction().commit();
		PersistenceManager.closeEntityManagerFactory();
	}
}
