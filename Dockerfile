# BadgeUp Sponge Client Dockerfile

# Sponge requires Java 8
FROM openjdk:8u111-jre

MAINTAINER Robert Herhold <robert@badgeup.io>

WORKDIR /data

# Download SpongeVanilla
ENV SPONGE_VERSION 1.10.2-5.1.0-BETA-357
RUN wget -q https://repo.spongepowered.org/maven/org/spongepowered/spongevanilla/${SPONGE_VERSION}/spongevanilla-${SPONGE_VERSION}.jar

# Download BadgeUp Sponge Client
ENV BUP_CLIENT_VERSION v1.0.2
RUN mkdir mods && wget -q -O mods/badgeup-sponge-client-${BUP_CLIENT_VERSION}.jar https://github.com/BadgeUp/sponge-client/releases/download/${BUP_CLIENT_VERSION}/badgeup-sponge-client-${BUP_CLIENT_VERSION}.jar

# Accept Minecraft EULA
RUN echo eula=true > eula.txt

# Add API Key to config
RUN mkdir -p config/badgeup && echo " \
badgeup {\n \
    api-key=\"REPLACE_ME\"\n \
    region=useast1\n \
    base-api-url=\"https://api-stage.useast1.badgeup.io/v1/apps/\"\n \
}\n \
broadcast-achievements=true \
" > config/badgeup/badgeup.conf

# Expose the port the Sponge server is running on
EXPOSE 25565

# Start the server
CMD java -Xmx1024M -Xms1024M -jar spongevanilla-${SPONGE_VERSION}.jar
