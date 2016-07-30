package io.badgeup.sponge;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.listener.block.ChangeBlockEventListener;
import io.badgeup.sponge.listener.block.CollideBlockEventListener;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

@Plugin(id = "badgeup-sponge-client", name = "BadgeUpSpongeClient", version = "1.0.0")
public class BadgeUpSponge {

	private static BlockingQueue<BadgeUpEvent> eventQueue;
	private static Config config;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private File configFile;
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	@Inject
	private Logger logger;

	@Listener
	public void preInit(GamePreInitializationEvent event) {
		eventQueue = new ArrayBlockingQueue<BadgeUpEvent>(10000);

		setupConfig();

		// Register event listeners

		Sponge.getEventManager().registerListeners(this, new ChangeBlockEventListener(this));
		// Generates way too many events
		// Sponge.getEventManager().registerListeners(this, new CollideBlockEventListener(this));

		// game.getEventManager().registerListeners(plugin, new
		// PlayerEventListener(this));
		// game.getEventManager().registerListeners(plugin, new
		// DropItemStackEventListener(this));

		for (int i = 1; i <= 8; i++) {
			Sponge.getScheduler().createTaskBuilder().async().execute(new PostEventsRunnable(this))
					.name("BadgeUp - Event Posting Thread #" + i).submit(this);
		}
	}

	private void setupConfig() {
		if (!this.configFile.exists()) {
			saveDefaultConfig();
		} else {
			loadConfig();
		}
	}

	/**
	 * Reads in config values supplied from the ConfigManager. Falls back on the
	 * default configuration values in Config.java
	 */
	private void loadConfig() {
		ConfigurationNode rawConfig = null;
		try {
			rawConfig = this.configLoader.load();
			config = Config.MAPPER.bindToNew().populate(rawConfig);
		} catch (IOException e) {
			this.logger.warn("The configuration could not be loaded! Using the default configuration");
		} catch (ObjectMappingException e) {
			this.logger.warn("There was an error loading the configuration." + e.getStackTrace());
		}
	}

	/**
	 * Saves a config file with default values if it does not already exist
	 *
	 */
	private void saveDefaultConfig() {
		try {
			this.logger.info("Generating config file...");
			this.configFile.getParentFile().mkdirs();
			this.configFile.createNewFile();
			CommentedConfigurationNode rawConfig = this.configLoader.load();

			try {
				// Populate config with default values
				config = Config.MAPPER.bindToNew().populate(rawConfig);
				Config.MAPPER.bind(config).serialize(rawConfig);
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}

			this.configLoader.save(rawConfig);
			this.logger.info("Config file successfully generated.");
		} catch (IOException exception) {
			this.logger.warn("The default configuration could not be created!");
		}
	}

	public static BlockingQueue<BadgeUpEvent> getEventQueue() {
		return eventQueue;
	}

	public static Config getConfig() {
		return config;
	}

	public Logger getLogger() {
		return logger;
	}

}
