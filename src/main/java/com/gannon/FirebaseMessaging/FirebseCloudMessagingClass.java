package com.gannon.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class FirebseCloudMessagingClass {
	public final static String AUTH_KEY_FCM = "AAAAUIUiDa4:APA91bHKDA23d0IwTMEjZGOKTFHx0id3AEJoOpKe6rwAdNisieAd_sDuC46i1Y6eORvmUFtO6Yd7zQk8W6n_NfvwFMLsqxDDttuHMarEawNZFMeFwsSldzoNbcBvLQQO1edBN6_0p7Kc";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>(0);
		list.add(
				"d7uIHpbwT6KZVf9Rfwul4Q:APA91bEy-HEEa4Ou2wKZ49hzJlvI6hqF2f69gipmEOslOGVezujFN8u1ZwtxTdwKaF4WSXylYT_aRcQ3Yhwqbz55IyqvWjm2SxAQlb5VIaMIOJKN-YqR7blQU6f1nD-Ie_verSQ0hDiF");

		try {
			FirebseCloudMessagingClass.sendPushNotification(
					"d7uIHpbwT6KZVf9Rfwul4Q:APA91bEy-HEEa4Ou2wKZ49hzJlvI6hqF2f69gipmEOslOGVezujFN8u1ZwtxTdwKaF4WSXylYT_aRcQ3Yhwqbz55IyqvWjm2SxAQlb5VIaMIOJKN-YqR7blQU6f1nD-Ie_verSQ0hDiF");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String sendPushNotificationToMultiple(List<String> deviceToken, String title, String body, String imageUrl)
			throws IOException {
		String result = "";
		URL url = new URL(API_URL_FCM);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
		conn.setRequestProperty("Content-Type", "application/json");

		JSONObject json = new JSONObject();

		// single device
		// json.put("to", deviceToken.trim());
		// multilpe
		json.put("registration_ids", deviceToken);
		JSONObject info = new JSONObject();
		info.put("title", title); // Notification title
		info.put("body", body); // Notification
		if (imageUrl != null)
			info.put("image", imageUrl);
		// body
		json.put("notification", info);
		try {
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(json.toString());
			wr.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			result = "Success";
		} catch (Exception e) {
			e.printStackTrace();
			result = "Failure";
		}
		System.out.println("GCM Notification is sent successfully");

		return result;

	}

	public static String sendPushNotification(String deviceToken) throws IOException {
		String result = "";
		URL url = new URL(API_URL_FCM);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
		conn.setRequestProperty("Content-Type", "application/json");

		JSONObject json = new JSONObject();

		// single device
		// json.put("to", deviceToken.trim());
		// multilpe
		List<String> toIds = new ArrayList<>(0);
		toIds.add(deviceToken);
		json.put("registration_ids", toIds);
		JSONObject info = new JSONObject();
		info.put("title", "New Registration"); // Notification title
		info.put("body", "Email:Vinay0986@gmail.com \n Name:Vinay\n Student Id:Stu1234"); // Notification
		info.put("image", "http://localhost:8080/img/1656250147366.jpg");
		// body
		json.put("notification", info);
		try {
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(json.toString());
			wr.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			result = "Success";
		} catch (Exception e) {
			e.printStackTrace();
			result = "Failure";
		}
		System.out.println("GCM Notification is sent successfully");

		return result;

	}

}