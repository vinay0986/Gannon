// 
// Decompiled by Procyon v0.5.36
// 

package com.gannon.entity;

import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import java.util.Date;
import javax.persistence.Table;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class Users implements Serializable {
	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private Integer userId;
	private String passWord;
	private String fActive;
	private Date registeredDate;
	private Date activatedDate;
	private Date deActivatedDate;
	private int activatedUser;
	private int deActivatedUser;
	private boolean fAdmin;
	private String studentId;
	private String token;

	@Column(name = "token_id")
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Users() {
		this.fAdmin = false;
	}

	@Column(name = "Student_id")
	public String getStudentId() {
		return this.studentId;
	}

	public void setStudentId(final String studentId) {
		this.studentId = studentId;
	}

	@Column(name = "f_admin")
	public boolean isfAdmin() {
		return this.fAdmin;
	}

	public void setfAdmin(final boolean fAdmin) {
		this.fAdmin = fAdmin;
	}

	@Column(name = "registered_date")
	public Date getRegisteredDate() {
		return this.registeredDate;
	}

	public void setRegisteredDate(final Date registeredDate) {
		this.registeredDate = registeredDate;
	}

	@Column(name = "Activated_date")
	public Date getActivatedDate() {
		return this.activatedDate;
	}

	public void setActivatedDate(final Date activatedDate) {
		this.activatedDate = activatedDate;
	}

	@Column(name = "Deactivated_date")
	public Date getDeActivatedDate() {
		return this.deActivatedDate;
	}

	public void setDeActivatedDate(final Date deActivatedDate) {
		this.deActivatedDate = deActivatedDate;
	}

	@Column(name = "Activated_user")
	public int getActivatedUser() {
		return this.activatedUser;
	}

	public void setActivatedUser(final int activatedUser) {
		this.activatedUser = activatedUser;
	}

	@Column(name = "Deactivated_user")
	public int getDeActivatedUser() {
		return this.deActivatedUser;
	}

	public void setDeActivatedUser(final int deActivatedUser) {
		this.deActivatedUser = deActivatedUser;
	}

	@Column(name = "f_active")
	public String getfActive() {
		return this.fActive;
	}

	public void setfActive(final String fActive) {
		this.fActive = fActive;
	}

	@Column(name = "password")
	public String getPassWord() {
		return this.passWord;
	}

	public void setPassWord(final String passWord) {
		this.passWord = passWord;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", unique = true, nullable = false)
	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(final Integer userId) {
		this.userId = userId;
	}

	@Column(name = "First_name")
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "Last_name")
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "Email")
	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	@Column(name = "Phone_number")
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
