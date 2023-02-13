package com.twitter.notification.twitter.notification;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import twitter4j.TwitterException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class TwitterNotificationApplication{

	static List<String> lastLike = new ArrayList<>();

	public static void main(String[] args) throws TwitterException, IOException {
		SpringApplication.run(TwitterNotificationApplication.class, args);
	}


	private static void sendTweetsToTelegramBot(List<String> tweets) {
		for (String tweet: tweets){
			MiBot miBot = new MiBot();
			miBot.enviarMensaje(tweet);
		}

	}

	private static List<String> likeTweets() {
		List<String> listLikeTweets = new ArrayList<>();
		try {
			String likeTweetsEndpoint = "https://api.twitter.com/2/users/&id/liked_tweets?tweet.fields=author_id&max_results=15";
			likeTweetsEndpoint = likeTweetsEndpoint.replace("&id", "1456705609215119366");
			// Connect to the API endpoint
			URL url = new URL(likeTweetsEndpoint);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			// Authenticate with your API key
			connection.setRequestProperty("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAAMu1lgEAAAAANQ7X7hGp3hq%2FPunotUrmiqXOyhQ%3DfOMY1jTYLSojoMnjeyIj6fftK0CatzdJ1kyye5hJip5oredGWg");

			// Read the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			Gson gson = new Gson();
			JsonElement jsonElement = gson.fromJson(response.toString(), JsonElement.class);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if(jsonObject.getAsJsonArray("data")!=null){
				Integer count= 0;
				JsonArray jsonArray = jsonObject.getAsJsonArray("data");
				for(JsonElement element: jsonArray){
					String idTweet = element.getAsJsonObject().get("id").getAsString();;
					if(lastLike.contains(idTweet)){
						break;
					}
					String text = element.getAsJsonObject().get("text").getAsString();
					String userName = getUserById(element.getAsJsonObject().get("author_id").getAsString());
					String tweetCompose = "Like Tweet -> Text: "+ text + " || userName: "+userName;
					System.out.println(tweetCompose);
					listLikeTweets.add(tweetCompose);
					if(count==0){
						lastLike.add(idTweet);
					}
					count++;
				}
			}else{
				System.out.println("No Tweets found");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listLikeTweets;
	}

	public static String getUserById(String id){
		String username = "";
		try {
			String userNameEndpoint = "https://api.twitter.com/2/users/&id";
			userNameEndpoint = userNameEndpoint.replace("&id", id);
			// Connect to the API endpoint
			URL url = new URL(userNameEndpoint);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			// Authenticate with your API key
			connection.setRequestProperty("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAAMu1lgEAAAAANQ7X7hGp3hq%2FPunotUrmiqXOyhQ%3DfOMY1jTYLSojoMnjeyIj6fftK0CatzdJ1kyye5hJip5oredGWg");

			// Read the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			Gson gson = new Gson();
			JsonElement jsonElement = gson.fromJson(response.toString(), JsonElement.class);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if(jsonObject.getAsJsonObject("data")!=null){
				JsonObject jsonObjectData = jsonObject.getAsJsonObject("data");
				username = jsonObjectData.get("username").getAsString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return username;
	}

	public static List<String> tweetsAndRetweets(Integer minutos){
		List<String> listTweetsAndRetweets = new ArrayList<>();
		try {
			Date actualDate = new Date();
			actualDate = restarMinutosDate(actualDate, minutos, 1);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String dateConsultString = df.format(actualDate);
			// Your Twitter API endpoint
			String tweetsAndRetweettsEndpoint = "https://api.twitter.com/2/users/&id/tweets?start_time=&date&tweet.fields=created_at";

			// Replace <screen_name> with the username you want to retrieve tweets from
			// Replace <count> with the number of tweets you want to retrieve
			tweetsAndRetweettsEndpoint = tweetsAndRetweettsEndpoint.replace("&id", "1456705609215119366");
			tweetsAndRetweettsEndpoint = tweetsAndRetweettsEndpoint.replace("&date", dateConsultString);
			System.out.println("Peticion -> "+tweetsAndRetweettsEndpoint);
//			endpoint = endpoint.replace("<count>", "2");

			// Connect to the API endpoint
			URL url = new URL(tweetsAndRetweettsEndpoint);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			// Authenticate with your API key
			connection.setRequestProperty("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAAMu1lgEAAAAANQ7X7hGp3hq%2FPunotUrmiqXOyhQ%3DfOMY1jTYLSojoMnjeyIj6fftK0CatzdJ1kyye5hJip5oredGWg");

			// Read the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			Gson gson = new Gson();
			JsonElement jsonElement = gson.fromJson(response.toString(), JsonElement.class);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if(jsonObject.getAsJsonArray("data")!=null){
				JsonArray jsonArray = jsonObject.getAsJsonArray("data");
				for(JsonElement element: jsonArray){
					String idTweet = element.getAsJsonObject().get("id").getAsString();;
					String text = element.getAsJsonObject().get("text").getAsString();
					String date = element.getAsJsonObject().get("created_at").getAsString();
					String tweetCompose = "Tweet or Retweet -> Text: "+ text + " || date: "+date;
					System.out.println(tweetCompose);
					listTweetsAndRetweets.add(tweetCompose);
				}
			}else{
				System.out.println("No Tweets found "+new Date());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listTweetsAndRetweets;
	}

	private static Date restarMinutosDate(Date actualDate, Integer minutos, Integer horas) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(actualDate);

		// Suma 1 hora a la fecha
		calendar.add(Calendar.MINUTE, -minutos);
		calendar.add(Calendar.HOUR, -horas);

		// Obtiene la fecha modificada
		Date newDate = calendar.getTime();
		return newDate;
	}


}


