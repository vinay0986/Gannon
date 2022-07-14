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
	public final static String AUTH_KEY_FCM = "AAAAvIyPMQE:APA91bFUsDPCRxHqLnETNjCCqDyw1g73mT7wu0Uj4qsWQ7bky5FboxznIoEn3-ISgvXpbGTvhUoZjH_VonsCtBWKiNFdPxs22ayQkai3p31TvYkL9o4rK3f_7hS2hs6s3HItwuI5kGRb";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>(0);
		list.add(
				"APA91bEy-HEEa4Ou2wKZ49hzJlvI6hqF2f69gipmEOslOGVezujFN8u1ZwtxTdwKaF4WSXylYT_aRcQ3Yhwqbz55IyqvWjm2SxAQlb5VIaMIOJKN-YqR7blQU6f1nD-Ie_verSQ0hDiF");

		try {
			FirebseCloudMessagingClass.sendPushNotification(
					"flFmXbzOQtOSpFs-MiCgl5:APA91bF4rsWNA8oCFvI3dyjsVH6UP0FQNc32FvX68ItWuaEJtemk_-L71Ep5qBQeDQenD-ZpBzihSHhEFK1HkR0F171KoDa43CV-uccN4Xt6YDaGF6Lb11PhCTyROmWlmMZji-4MpEOC");
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
		// info.put("image", "http://localhost:8080/img/1656250147366.jpg");
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