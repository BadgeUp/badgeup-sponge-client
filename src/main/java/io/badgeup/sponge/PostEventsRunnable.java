package io.badgeup.sponge;

import java.net.URI;
import java.util.concurrent.BlockingQueue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;

import io.badgeup.sponge.event.BadgeUpEvent;

public class PostEventsRunnable implements Runnable {

	@Override
	public void run() {
		System.out.println("Posting events...");
		final BlockingQueue<BadgeUpEvent> eventQueue = BadgeUpSponge.getEventQueue();

		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		WebTarget target = client.target(URI.create("http://localhost:3000/v1/apps/zzazynm/events"));
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);
		invocationBuilder.header("Authorization",
				"bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwicm9sZSI6ImFjY291bnQtaG9sZGVyIiwiYWNjb3VudElkIjoibm9kczUwdyJ9.ZtKwpkwwcLbIKWgciki-9VHALCUaT7OEgVFmEDSe61U");

		System.out.println("BadgeUp event consumer started");

		try {
			while (true) {
				final BadgeUpEvent event = eventQueue.take();

				try {
					Response response = invocationBuilder.post(Entity.entity(event, MediaType.APPLICATION_JSON_TYPE));
					System.out.println("BadgeUp response status code: " + response.getStatus());
				} catch (Exception e) {
					System.err.println(e.getMessage());
					//TODO possibly put the event back in the queue if it was a timeout
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
