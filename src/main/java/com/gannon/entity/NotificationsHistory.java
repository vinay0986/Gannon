package com.gannon.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "notifications_history")
public class NotificationsHistory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notifications_history_id", unique = true, nullable = false)
	private Integer notificationsHistoryId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notifications_id")
	private Notifications notifications;

	@Column(name = "read_by")
	private int readby;

	@Column(name = "read_date")
	private Date readDate;

	public Integer getNotificationsHistoryId() {
		return notificationsHistoryId;
	}

	public void setNotificationsHistoryId(Integer notificationsHistoryId) {
		this.notificationsHistoryId = notificationsHistoryId;
	}

	public Notifications getNotifications() {
		return notifications;
	}

	public void setNotifications(Notifications notifications) {
		this.notifications = notifications;
	}

	public int getReadby() {
		return readby;
	}

	public void setReadby(int readby) {
		this.readby = readby;
	}

	public Date getReadDate() {
		return readDate;
	}

	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}

}
