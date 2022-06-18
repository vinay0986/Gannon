package com.vinni.webservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;

import com.vinni.entity.ConstantSettings;

public class EmailSend {
	public void emailSend(EntityManager em, String subject, String body, String reciptent) {
		try {
			List<ConstantSettings> settings = em.createQuery("from ConstantSettings where fActive='Y'").getResultList();
			Map<String, String> map = new HashMap<>(0);
			for (ConstantSettings set : settings) {
				map.put(set.getKey(), set.getValue());
			}
			String password = map.get("password");
			Properties props = new Properties();
			props.put("mail.smtp.user", map.get("user"));
			props.put("mail.smtp.host", map.get("host"));
			props.put("mail.smtp.port", map.get("port"));
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.setProperty("mail.smtp.ssl.enable", "false");
			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(map.get("user"), password);
				}
			});
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(map.get("user")));
			msg.setSubject(subject);
			msg.setContent(body.toString().replaceAll("(\n)", "<br />"), "text/html");
			msg.saveChanges();
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(reciptent));
			Transport.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}