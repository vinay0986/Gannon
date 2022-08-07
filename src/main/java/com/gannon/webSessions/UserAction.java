package com.gannon.webSessions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;

import com.gannon.entity.Users;
import com.gannon.webservices.EmailSend;
import com.gannon.webservices.PersistenceManager;

@SessionScoped
@ManagedBean
public class UserAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private List<Users> newlyRegisteredList;
	private List<Users> exisitingUsersList;
	private String searchString1;
	private String searchString2;
	private Users adminUser;

	public String getSearchString1() {
		return searchString1;
	}

	public void setSearchString1(String searchString1) {
		this.searchString1 = searchString1;
	}

	public String getSearchString2() {
		return searchString2;
	}

	public void setSearchString2(String searchString2) {
		this.searchString2 = searchString2;
	}

	@Transactional
	public void pageEnter() {
		userName = null;
		newlyRegisteredList = new ArrayList<Users>(0);
		exisitingUsersList = new ArrayList<Users>(0);
		searchString1 = null;
		searchString2 = null;
		PersistenceManager manager = new PersistenceManager();
		final EntityManagerFactory emf = manager.getEntityManagerFactory();
		final EntityManager em = emf.createEntityManager();
		newlyRegisteredList = em
				.createQuery("from Users where fActive is null and fAdmin=0 order by registeredDate desc")
				.getResultList();

		exisitingUsersList = em.createQuery("from Users where fActive is not null and fAdmin=0 order by userId desc")
				.getResultList();
		try {
			adminUser = (Users) em.createQuery("from Users where  fAdmin=1").getSingleResult();
		} catch (Exception e) {

		}

		manager.closeEntityManagerFactory();

	}

	@Transactional
	public void search1() {
		if (searchString1 == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide the search value", ""));
		} else {
			newlyRegisteredList = new ArrayList<Users>(0);
			PersistenceManager manager = new PersistenceManager();
			final EntityManagerFactory emf = manager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			newlyRegisteredList = em.createQuery(
					"from Users where fActive is  null and fAdmin=0 and (firstName like :sea or lastName like :sea or email like :sea or studentId like :sea) "
							+ " order by registeredDate desc")
					.setParameter("sea", "%" + searchString1 + "%").getResultList();
			manager.closeEntityManagerFactory();
			if (newlyRegisteredList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"No Search results found with the given search criteria", ""));
			}

		}
	}

	public void approveOrDeny(Users reg, String approved) {
		PersistenceManager manager = new PersistenceManager();
		final EntityManagerFactory emf = manager.getEntityManagerFactory();
		final EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		reg.setfActive(approved);
		if (approved.equalsIgnoreCase("Y")) {
			reg.setActivatedDate(new Date());
			reg.setActivatedUser(adminUser.getUserId());
		} else {
			reg.setDeActivatedDate(new Date());
			reg.setDeActivatedUser(adminUser.getUserId());
		}
		em.merge(reg);
		em.getTransaction().commit();
		manager.closeEntityManagerFactory();
		for (Users u : exisitingUsersList) {
			if (reg.getUserId().compareTo(u.getUserId()) == 0) {
				u.setfActive(reg.getfActive());
			}
		}
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully updated", ""));
	}

	public void deny1(Users reg) {
		if (reg.getDenyReason() == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Deny reason is required for this activity", ""));
		}

		PersistenceManager manager = new PersistenceManager();
		final EntityManagerFactory emf = manager.getEntityManagerFactory();
		final EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		reg.setfActive("N");
		reg.setDeActivatedDate(new Date());
		reg.setDeActivatedUser(adminUser.getUserId());
		em.merge(reg);
		em.getTransaction().commit();
		manager.closeEntityManagerFactory();

		newlyRegisteredList.remove(reg);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully updated", ""));
	}

	public void approve1(Users reg) {
		PersistenceManager manager = new PersistenceManager();
		final EntityManagerFactory emf = manager.getEntityManagerFactory();
		final EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		reg.setfActive("Y");
		reg.setActivatedDate(new Date());
		reg.setActivatedUser(adminUser.getUserId());
		em.merge(reg);
		em.getTransaction().commit();

		try {
			final StringBuilder sb = new StringBuilder();
			sb.append("Dear  " + reg.getFirstName() + "\n\n");
			sb.append("Welcome to Online Auction Shop for Campus Students!!! \n\n");
			sb.append("Please use below credentails to login \n\n");
			sb.append("User Name:" + reg.getEmail() + "\n\n");
			sb.append("Password:" + reg.getPassWord() + "\n\n");
			sb.append("Regards:\n Online Auction Shop for campus studes team");
			sb.append("\n\n\tNOTE:This is a system-generated e-mail, Please don't reply to this message");
			new EmailSend().emailSend(em, "User Account Details", sb.toString(), reg.getEmail());
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			manager.closeEntityManagerFactory();
		}
		newlyRegisteredList.remove(reg);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Successfully approved and details sent to registered mailId", ""));
	}

	public void search2() {
		if (searchString2 == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide the search value", ""));
		} else {
			exisitingUsersList = new ArrayList<Users>(0);
			PersistenceManager manager = new PersistenceManager();
			final EntityManagerFactory emf = manager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			exisitingUsersList = em.createQuery(
					"from Users where fActive is not null and fAdmin=0 and (firstName like :sea or lastName like :sea or email like :sea or studentId like :sea)"
							+ " order by userId desc")
					.setParameter("sea", "%" + searchString2 + "%").getResultList();
			em.getTransaction().commit();
			manager.closeEntityManagerFactory();
			if (exisitingUsersList.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"No Search results found with the given search criteria", ""));
			}

		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<Users> getNewlyRegisteredList() {
		return newlyRegisteredList;
	}

	public void setNewlyRegisteredList(List<Users> newlyRegisteredList) {
		this.newlyRegisteredList = newlyRegisteredList;
	}

	public List<Users> getExisitingUsersList() {
		return exisitingUsersList;
	}

	public void setExisitingUsersList(List<Users> exisitingUsersList) {
		this.exisitingUsersList = exisitingUsersList;
	}

}
