package com.gannon.webSessions;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;

import com.gannon.entity.Users;
import com.gannon.webservices.PersistenceManager;

@SessionScoped
@ManagedBean
public class LoginAction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private String passWord;
	private Users user;

	@PostConstruct
	public void pageEnter() {
		userName = null;
		passWord = null;
		user=null;
	}

	@Transactional
	public String login() {
		System.out.println("-----------------user name & password-----"+userName+"--"+passWord);
		try {
			PersistenceManager manager = new PersistenceManager();
			EntityManagerFactory emf = manager.getEntityManagerFactory();
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			user = (Users) em.createQuery("from Users where email=:em and passWord=:pwd and fActive='Y'")
					.setParameter("em", userName).setParameter("pwd", passWord).getSingleResult();
			em.getTransaction().commit();
			FacesContext.getCurrentInstance().addMessage("messages",
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Login Successfully", ""));
			return "/pages/UserManagment.xhtml?faces-redirect=true";
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Invalid Credentials", ""));
		}
		return null;

	}
	
	public String logout() {
		PersistenceManager manager = new PersistenceManager();
		manager.closeEntityManagerFactory();
		return "/login1.xhtml?faces-redirect=true";

	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

}
