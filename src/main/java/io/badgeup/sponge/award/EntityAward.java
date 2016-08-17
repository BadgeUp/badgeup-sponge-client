package io.badgeup.sponge.award;

import java.util.Optional;

import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;

import com.flowpowered.math.vector.Vector3d;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.Util;

public class EntityAward extends Award {

	private String entityTypeID;
	private JSONObject rawPosition;

	public EntityAward(BadgeUpSponge plugin, JSONObject award) {
		super(plugin, award);
		this.entityTypeID = Util.safeGetString(data, "entityType").orElse("");
		this.rawPosition = Util.safeGetJSONObject(data, "position")
				.orElse(new JSONObject().put("x", "~").put("y", "~").put("z", "~"));
	}

	@Override
	public boolean awardPlayer(Player player) {
		final Optional<EntityType> optType = Sponge.getRegistry().getType(EntityType.class, entityTypeID);
		if (!optType.isPresent()) {
			return false;
		}

		Optional<Vector3d> positionOpt = resolvePosition(this.rawPosition, player.getLocation().getPosition());
		if (!positionOpt.isPresent()) {
			plugin.getLogger().error("Malformed entity position. Aborting.");
			return false;
		}

		Vector3d position = positionOpt.get();
		Entity entity = player.getWorld().createEntity(optType.get(), position);

		Optional<String> colorIdOpt = Util.safeGetString(data, "color");
		if (colorIdOpt.isPresent()) {
			Optional<DyeColor> colorOpt = Sponge.getRegistry().getType(DyeColor.class, colorIdOpt.get());
			if (colorOpt.isPresent()) {
				entity.offer(Keys.DYE_COLOR, colorOpt.get());
			}
		}

		// TODO configurable world?
		return player.getWorld().spawnEntity(entity,
				Cause.source(EntitySpawnCause.builder().entity(entity).type(SpawnTypes.PLUGIN).build()).build());
	}

	private Optional<Vector3d> resolvePosition(JSONObject raw, Vector3d playerPosition) {
		JSONObject relativePosition = new JSONObject().put("x", playerPosition.getX()).put("y", playerPosition.getY())
				.put("z", playerPosition.getZ());

		JSONObject finalPosition = new JSONObject();

		String[] keys = { "x", "y", "z" };
		for (String key : keys) {
			if (!raw.has(key)) {
				return Optional.empty();
			}
			boolean isRelative = false;
			Optional<Double> coordOpt;
			if (raw.get(key) instanceof String) {
				String coordinateString = raw.getString(key);
				int tildeIndex = coordinateString.indexOf('~');
				if (tildeIndex > -1) {
					isRelative = true;
					coordinateString = coordinateString.substring(0, tildeIndex)
							+ coordinateString.substring(tildeIndex + 1);
				}
				// If the string was only ever just "~"
				if (coordinateString.isEmpty()) {
					coordOpt = Optional.of(0d);
				} else {
					coordOpt = Util.safeParseDouble(coordinateString);
				}
			} else {
				coordOpt = Util.safeGetDouble(raw, key);
			}

			if (!coordOpt.isPresent()) {
				return Optional.empty();
			}

			if (isRelative) {
				finalPosition.put(key, relativePosition.getDouble(key) + coordOpt.get());
			} else {
				finalPosition.put(key, coordOpt.get());
			}
		}

		return Optional.of(
				new Vector3d(finalPosition.getDouble("x"), finalPosition.getDouble("y"), finalPosition.getDouble("z")));

	}

}
