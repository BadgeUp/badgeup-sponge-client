package io.badgeup.sponge;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mashape.unirest.http.Unirest;
import io.badgeup.sponge.command.executor.BadgeUpInitCommandExecutor;
import io.badgeup.sponge.command.executor.ListAwardsCommandExecutor;
import io.badgeup.sponge.command.executor.RedeemAwardCommandExecutor;
import io.badgeup.sponge.eventlistener.BadgeUpSpongeEventListener;
import io.badgeup.sponge.service.AchievementPersistenceService;
import io.badgeup.sponge.service.AwardPersistenceService;
import io.badgeup.sponge.service.FlatfileAchievementPersistenceService;
import io.badgeup.sponge.service.FlatfileAwardPersistenceService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

@Plugin(id = "badgeup-sponge-client")
public class BadgeUpSponge {

    private static Config config;

    @Inject @ConfigDir(sharedRoot = false) private Path configDir;

    @Inject @DefaultConfig(sharedRoot = false) private File configFile;

    @Inject @DefaultConfig(sharedRoot = false) private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Inject private Logger logger;

    @Listener(order = Order.EARLY)
    public void preInit(GamePreInitializationEvent event) {
        setupConfig();
        validateConfig();

        try {
            setupRestClient();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            this.logger.error("Could not initialize the REST client with TLS enabled. Shutting down.");
            e.printStackTrace();
            Sponge.getServer().shutdown();
        }

        Sponge.getEventManager().registerListeners(this, new BadgeUpSpongeEventListener(this));

        Sponge.getServiceManager().setProvider(this, AchievementPersistenceService.class,
                new FlatfileAchievementPersistenceService(this.configDir));
        Sponge.getServiceManager().setProvider(this, AwardPersistenceService.class,
                new FlatfileAwardPersistenceService(this.configDir));

        Sponge.getCommandManager().register(this,
                CommandSpec.builder().description(Text.of("Displays pending awards to the player"))
                        .permission("badgeup.awards.list").executor(new ListAwardsCommandExecutor()).build(),
                "awards", "rewards");

        Sponge.getCommandManager().register(this,
                CommandSpec.builder().description(Text.of("Redeem a pending award")).permission("badgeup.awards.redeem")
                        .arguments(GenericArguments.string(Text.of("id")))
                        .executor(new RedeemAwardCommandExecutor(this)).build(),
                "redeem");

        Map<List<String>, CommandSpec> subCommands = new HashMap<>();
        subCommands.put(Arrays.asList("init"),
                CommandSpec.builder().description(Text.of("Initialize your BadgeUp account with demo achievements"))
                        .permission("badgeup.admin.init").executor(new BadgeUpInitCommandExecutor(this)).build());

        Sponge.getCommandManager().register(this, CommandSpec.builder().children(subCommands).build(), "badgeup");
    }

    private void setupRestClient() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslcontext = SSLContext.getInstance("TLSv1");
        System.setProperty("https.protocols", "TLSv1");
        TrustManager[] trustAllCerts = {new InsecureTrustManager()};
        sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());

        String apiKey = config.getBadgeUpConfig().getAPIKey();
        final String authHeader = "Basic " + new String(Base64.getEncoder().encode((apiKey + ":").getBytes()));

        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLHostnameVerifier(new InsecureHostnameVerifier())
                .setSSLContext(sslcontext)
                .setDefaultHeaders(Lists.newArrayList(
                        new BasicHeader("User-Agent", "BadgeUp_SpongeClient v1.0.0"),
                        new BasicHeader("Authorization", authHeader)))
                .build();

        Unirest.setHttpClient(httpclient);
    }

    // Run asynchronously
    public static void presentAchievement(Player player, JSONObject achievement) {
        Text.Builder achTextBuilder = Text.builder(achievement.getString("name")).color(TextColors.GOLD);
        if (!achievement.isNull("description")) {
            achTextBuilder
                    .onHover(TextActions.showText(Text.of(TextColors.BLUE, achievement.getString("description"))));
        }

        if (config.doBroadcastAchievements()) {
            MessageChannel.TO_ALL.send(Text.of(TextColors.GOLD, player.getDisplayNameData().displayName().get(),
                    TextColors.GREEN, " has just completed ", achTextBuilder.build(), TextColors.GREEN, "!"));
        } else {
            player.sendMessage(Text.of(TextColors.GREEN, "You have just completed ", achTextBuilder.build(),
                    TextColors.GREEN, "!"));
        }

        AwardPersistenceService aps = Sponge.getServiceManager().provide(AwardPersistenceService.class).get();
        List<JSONObject> playerAwards;
        try {
            // This is already not on the main thread, so OK to wait
            playerAwards = aps.getPendingAwardsForPlayer(player.getUniqueId()).get();
        } catch (InterruptedException | ExecutionException e) {
            playerAwards = new ArrayList<>();
        }

        if (playerAwards.size() > 0) {
            player.sendMessage(Text.of(TextColors.GREEN, "You have ", TextColors.GOLD, playerAwards.size(),
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

            // Remove the base URL config setting only intended for development
            configNode.getNode("badgeup").removeChild("base-api-url");

            this.configLoader.save(configNode);
            this.logger.info("Config file successfully generated.");
        } catch (IOException exception) {
            this.logger.warn("The default configuration could not be created!");
        }
    }

    public static Config getConfig() {
        return config;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public PluginContainer getContainer() {
        return Sponge.getPluginManager().getPlugin("badgeup-sponge-client").get();
    }

}
