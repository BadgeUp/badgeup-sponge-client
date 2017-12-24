package io.badgeup.sponge;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import io.badgeup.sponge.command.executor.BadgeUpInitCommandExecutor;
import io.badgeup.sponge.command.executor.CreateItemAwardCommandExecutor;
import io.badgeup.sponge.command.executor.DebugCommandExecutor;
import io.badgeup.sponge.command.executor.ListAchievementsCommandExecutor;
import io.badgeup.sponge.command.executor.ListAwardsCommandExecutor;
import io.badgeup.sponge.command.executor.RedeemAwardCommandExecutor;
import io.badgeup.sponge.eventlistener.BadgeUpEventListener;
import io.badgeup.sponge.eventlistener.GeneralEventListener;
import io.badgeup.sponge.eventlistener.MoveEventListener;
import io.badgeup.sponge.service.AchievementPersistenceService;
import io.badgeup.sponge.service.AwardPersistenceService;
import io.badgeup.sponge.service.FlatfileAchievementPersistenceService;
import io.badgeup.sponge.service.FlatfileAwardPersistenceService;
import io.badgeup.sponge.util.Constants;
import io.badgeup.sponge.util.ResourceCache;
import io.badgeup.sponge.util.Util;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Plugin(id = Constants.PLUGIN_ID)
public class BadgeUpSponge {

    private static Config config;
    @Inject @ConfigDir(sharedRoot = false) private Path configDir;
    @Inject @DefaultConfig(sharedRoot = false) private File configFile;
    @Inject @DefaultConfig(sharedRoot = false) private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    @Inject private Logger logger;
    private List<BadgeUpEventListener> eventListeners = Lists.newArrayList(new GeneralEventListener(this), new MoveEventListener(this));
    private ResourceCache resourceCache;
    private EventConnectionPool eventConnectionPool = new EventConnectionPool(this);

    @Listener(order = Order.EARLY)
    public void preInit(GamePreInitializationEvent event) {
        this.logger.info("Initializing " + getContainer().getName());
        setup();
    }

    @Listener
    public void reload(GameReloadEvent event) {
        this.logger.info("Reloading " + getContainer().getName());
        setup();
    }

    public void setup() {
        // Make sure all commands & event listeners are disabled so there won't
        // be any conflicts. Also closes all websockets
        disable();

        setupConfig();
        validateConfig();

        for (int i = 0; i < BadgeUpSponge.config.getBadgeUpConfig().getEventPoolConnections(); i++) {
            this.eventConnectionPool.openNewConnection();
        }

        this.eventListeners.forEach(listener -> Sponge.getEventManager().registerListeners(this, listener));

        Sponge.getServiceManager().setProvider(this, AchievementPersistenceService.class,
                new FlatfileAchievementPersistenceService(this.configDir));
        Sponge.getServiceManager().setProvider(this, AwardPersistenceService.class,
                new FlatfileAwardPersistenceService(this.configDir));

        this.resourceCache = new ResourceCache(this.logger);

        // /awards, /rewards
        Sponge.getCommandManager().register(this,
                CommandSpec.builder().description(Text.of("Displays pending awards to the player"))
                        .permission("badgeup.awards.list").executor(new ListAwardsCommandExecutor(this)).build(),
                "awards", "rewards");

        // /redeem <award id>
        Sponge.getCommandManager().register(this,
                CommandSpec.builder().description(Text.of("Redeem a pending award")).permission("badgeup.awards.redeem")
                        .arguments(GenericArguments.string(Text.of("id")))
                        .executor(new RedeemAwardCommandExecutor(this)).build(),
                "redeem");

        // /progress, /achievements
        Sponge.getCommandManager().register(this,
                CommandSpec.builder().description(Text.of("Displays achievement progress to the player"))
                        .permission("badgeup.progress").executor(new ListAchievementsCommandExecutor(this)).build(),
                "progress", "achievements");

        // /badgeup init
        Map<List<String>, CommandSpec> subCommands = new HashMap<>();
        subCommands.put(Arrays.asList("init"),
                CommandSpec.builder().description(Text.of("Initialize your BadgeUp account with demo achievements"))
                        .permission("badgeup.admin.init").executor(new BadgeUpInitCommandExecutor(this)).build());

        // badgeup debug <player name>
        subCommands.put(Arrays.asList("debug"),
                CommandSpec.builder().description(Text.of("Monitor player event activity")).arguments(GenericArguments.player(Text.of("player")))
                        .permission("badgeup.admin.debug").executor(new DebugCommandExecutor()).build());

        // /badgeup awards
        Map<List<String>, CommandSpec> awardsSubCommands = new HashMap<>();

        // /badgeup awards create
        Map<List<String>, CommandSpec> awardsCreateSubCommands = new HashMap<>();
        // /badgeup awards create inhand
        awardsCreateSubCommands.put(Arrays.asList("inhand"),
                CommandSpec.builder().description(Text.of("Create an item award with the item you are currently holding"))
                        .permission("badgeup.admin.award.create").executor(new CreateItemAwardCommandExecutor(this)).build());

        awardsSubCommands.put(Arrays.asList("create"), CommandSpec.builder().children(awardsCreateSubCommands).build());

        subCommands.put(Arrays.asList("awards"), CommandSpec.builder().children(awardsSubCommands).build());

        Sponge.getCommandManager().register(this, CommandSpec.builder().children(subCommands).build(), "badgeup");
    }

    // Run asynchronously
    public void presentAchievement(Player player, String achievementId) throws InterruptedException, ExecutionException {
        Optional<JSONObject> achievementOpt = this.resourceCache.getAchievementById(achievementId).get();
        if (!achievementOpt.isPresent()) {
            this.logger.warn("Failed to get achievement \"" + achievementId + "\" when granting to player \"" + player.getUniqueId() + "\"");
            return;
        }

        JSONObject achievement = achievementOpt.get();

        Text.Builder achTextBuilder = Text.builder(achievement.getString("name")).color(TextColors.GOLD);
        if (!achievement.isNull("description")) {
            achTextBuilder
                    .onHover(TextActions.showText(Text.of(TextColors.BLUE, achievement.getString("description"))));
        }

        player.sendTitle(
                Title.of(Text.of(TextColors.GREEN, "Achievement Get!"),
                        Text.of(TextColors.BLUE, "You completed the ", TextColors.GOLD, "\"", achTextBuilder.build(),
                                "\"", TextColors.BLUE, " achievement!")));

        if (config.doBroadcastAchievements()) {
            MessageChannel.TO_ALL.send(Text.of(TextColors.GOLD, player.getDisplayNameData().displayName().get(),
                    TextColors.GREEN, " has just completed ", achTextBuilder.build(), TextColors.GREEN, "!"));
        }

        if (config.doFireworks()) {
            Sponge.getScheduler().createTaskBuilder().intervalTicks(10).execute(new FireworkConsumer(player)).submit(this);
        }

        if (config.getSoundsConfig().isEnabled()) {
            Optional<SoundType> soundTypeOpt = Sponge.getRegistry().getType(SoundType.class, config.getSoundsConfig().getSound());

            if (soundTypeOpt.isPresent()) {
                Sponge.getScheduler().createTaskBuilder().intervalTicks(10).execute(new SoundEffectConsumer(player, soundTypeOpt.get())).submit(this);
            }
        }

        AwardPersistenceService aps = Sponge.getServiceManager().provide(AwardPersistenceService.class).get();
        Map<String, Integer> playerAwards;
        try {
            // This is already not on the main thread, so OK to wait
            playerAwards = aps.getAllForPlayer(player.getUniqueId()).get();
        } catch (InterruptedException | ExecutionException e) {
            playerAwards = Maps.newHashMap();
        }

        int totalCount = playerAwards.values().stream().mapToInt(Integer::intValue).sum();

        if (totalCount > 0) {
            player.sendMessage(Text.of(TextColors.GREEN, "You have ", TextColors.GOLD, totalCount,
                    TextColors.GREEN, " pending award(s). Type '/awards' to claim!"));
        }
    }

    private void setupConfig() {
        if (!this.configFile.exists()) {
            saveDefaultConfig();
        } else {
            loadConfig();
        }
    }

    private void validateConfig() {
        final String region = config.getBadgeUpConfig().getRegion();
        Preconditions.checkArgument(!region.isEmpty(), "Region must not be empty");

        final String apiKey = config.getBadgeUpConfig().getAPIKey();
        Preconditions.checkArgument(!apiKey.isEmpty(), "API key must not be empty");

        Optional<String> appIdOpt = Util.parseAppIdFromAPIKey(apiKey);
        Preconditions.checkArgument(appIdOpt.isPresent(), "API key is invalid");
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
            CommentedConfigurationNode configNode = this.configLoader.load();

            try {
                // Populate config with default values
                config = Config.MAPPER.bindToNew().populate(configNode);
                Config.MAPPER.bind(config).serialize(configNode);
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }

            // Remove config settings only intended for development
            configNode.getNode("badgeup").removeChild("base-api-url");
            configNode.getNode("badgeup").removeChild("event-pool-connections");

            this.configLoader.save(configNode);
            this.logger.info("Config file successfully generated.");
        } catch (IOException exception) {
            this.logger.warn("The default configuration could not be created!");
        }
    }

    private void disable() {
        this.eventListeners.forEach(Sponge.getEventManager()::unregisterListeners);
        Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getGame().getCommandManager()::removeMapping);
        Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
        this.eventConnectionPool.closeAllConnections("Disabling BadgeUp Sponge plugin");
    }

    public static Config getConfig() {
        return config;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public static PluginContainer getContainer() {
        return Sponge.getPluginManager().getPlugin(Constants.PLUGIN_ID).get();
    }

    public ResourceCache getResourceCache() {
        return this.resourceCache;
    }

    public EventConnectionPool getEventConnectionPool() {
        return this.eventConnectionPool;
    }

}
