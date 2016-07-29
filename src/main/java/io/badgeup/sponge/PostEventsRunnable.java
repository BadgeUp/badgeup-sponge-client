package io.badgeup.sponge;

import java.net.URI;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.google.common.base.Preconditions;

import io.badgeup.sponge.event.BadgeUpEvent;

public class PostEventsRunnable implements Runnable {

	private final String BASE_URL = "http://localhost:3000/v1/apps/";

	private BadgeUpSponge plugin;

	public PostEventsRunnable(BadgeUpSponge plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		final String apiKey = BadgeUpSponge.getConfig().getAPIKey();
		Preconditions.checkArgument(!apiKey.isEmpty(), "API key must not be empty");
		// Base64 decode the API key
		final byte[] decodedKey = Base64.getDecoder().decode(apiKey);
		JSONObject keyObj = null;
		try {
			keyObj = new JSONObject(new String(decodedKey));
		} catch (Exception e) {
			plugin.getLogger().error("Please specify a valid API key.");
		}
		final String appId = keyObj.getString("applicationId");
		Preconditions.checkArgument(!appId.isEmpty(), "Application ID must not be empty");
		Preconditions.checkArgument(appId.matches("^[a-zA-Z0-9]*$"),
				"Application ID must contain only letters and numbers");
		final BlockingQueue<BadgeUpEvent> eventQueue = BadgeUpSponge.getEventQueue();

		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(URI.create("http://localhost:3000/v1/apps/" + appId + "/events"));
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);
		invocationBuilder.header("Authorization", "bearer "
				+ "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50SWQiOiJ2aW9vaml4Iiwicm9sZSI6ImFjY291bnRfb3duZXIiLCJpc3MiOiJodHRwczovL2JhZGdldXAuYXV0aDAuY29tLyIsInN1YiI6ImF1dGgwfDU3ODE4MmZiODkzYzU4NmIzZTk1YzAyNCIsImF1ZCI6IlkyTjFlVEpXYjFEZHlWdVo4MXBQY2s4REhUN0V2dG11IiwiZXhwIjoxNDY5ODUzMTkxLCJpYXQiOjE0Njk4MTcxOTF9.yLXn3mZzcUMH5bwECr-y5WRJUgV7axGx0LjCjG4Rhb8");

		plugin.getLogger().info("Started BadgeUp event consumer");

		try {
			while (true) {
				final BadgeUpEvent event = eventQueue.take();

				try {
					Response response = invocationBuilder.post(Entity.entity(event.build().toString(), MediaType.APPLICATION_JSON_TYPE));
					System.out.println("BadgeUp response status code: " + response.getStatus());
				} catch (Exception e) {
					System.err.println(e.getMessage());
					// TODO possibly put the event back in the queue if it was a
					// timeout
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
