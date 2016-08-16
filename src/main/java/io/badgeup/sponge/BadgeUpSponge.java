package io.badgeup.sponge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

import io.badgeup.sponge.event.BadgeUpEvent;
import io.badgeup.sponge.service.AchievementPersistenceService;
import io.badgeup.sponge.service.AwardPersistenceService;
import io.badgeup.sponge.service.FlatfileAchievementPersistenceService;
import io.badgeup.sponge.service.FlatfileAwardPersistenceService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

@Plugin(id = "badgeup-sponge-client", name = "BadgeUpSpongeClient", version = "1.0.0")
public class BadgeUpSponge {

	private static BlockingQueue<BadgeUpEvent> eventQueue;
	private static Config config;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private File configFile;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;

	@Inject
	private Logger logger;

	@Listener(order = Order.EARLY)
	public void preInit(GamePreInitializationEvent event) {
		eventQueue = new ArrayBlockingQueue<BadgeUpEvent>(10000);

		setupConfig();

		Sponge.getEventManager().registerListeners(this, new BadgeUpEventListener(this));

		for (int i = 1; i <= 8; i++) {
			Sponge.getScheduler().createTaskBuilder().async().execute(new PostEventsRunnable(this))
					.name("BadgeUp - Event Posting Thread #" + i).submit(this);
		}

		Sponge.getServiceManager().setProvider(this, AchievementPersistenceService.class,
				new FlatfileAchievementPersistenceService(this.configDir));
		Sponge.getServiceManager().setProvider(this, AwardPersistenceService.class,
				new FlatfileAwardPersistenceService(this.configDir));
	}

	public static void presentAchievement(Player player, JSONObject achievement) {
		Text.Builder achTextBuilder = Text.builder(achievement.getString("name")).color(TextColors.GOLD);
		if(achievement.has("description")) {
			achTextBuilder.onHover(TextActions.showText(Text.of(TextColors.BLUE, achievement.getString("description"))));
		}
		
		MessageChannel.TO_ALL.send(Text.of(TextColors.GOLD, player.getDisplayNameData().displayName().get(), TextColors.GREEN,
				" has just completed ", achTextBuilder.build(), TextColors.GREEN, "!"));
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

	public PluginContainer getContainer() {
		return Sponge.getPluginManager().getPlugin("badgeup-sponge-client").get();
	}

}
