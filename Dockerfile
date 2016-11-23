# BadgeUp Sponge Client Dockerfile

# Sponge requires Java 8
FROM java:8

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

# Expose the port the Sponge server is running on
EXPOSE 25567

# Start the server
CMD java -jar spongevanilla-${SPONGE_VERSION}.jar
