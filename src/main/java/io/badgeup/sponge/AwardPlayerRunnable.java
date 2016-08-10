package io.badgeup.sponge;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import io.badgeup.sponge.award.Award;
import io.badgeup.sponge.award.MonetaryAward;

public class AwardPlayerRunnable implements Runnable {
	
	private BadgeUpSponge plugin;
	private JSONObject eventBody;
	private Player subject;
	private Validator validator;

	public AwardPlayerRunnable(BadgeUpSponge plugin, JSONObject eventBody) {
		this.plugin = plugin;
		this.eventBody = eventBody;
	    this.validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Override
	public void run() {
		final UUID playerId = UUID.fromString(eventBody.getString("subject"));
		final Optional<Player> subjectOpt = Sponge.getServer().getPlayer(playerId);
		if(!subjectOpt.isPresent()) {
			plugin.getLogger().info("Unable to find player with ID " + playerId.toString() + " to give an award. The award will be given when the player logs in.");
		}
		this.subject = subjectOpt.get();
		eventBody.getJSONObject("achievement").getJSONArray("awards").forEach(this::processAward);
	}
	
	private void processAward(Object obj) {
		final JSONObject awardJSON = (JSONObject) obj;
		final String awardType = awardJSON.getJSONObject("data").getString("type");
		Optional<Award> awardOpt = Optional.empty();
		switch(awardType.toLowerCase()) {
			case "monetary":
				awardOpt = Optional.of(new MonetaryAward(plugin, awardJSON));
				break;
		}
		
		if(!awardOpt.isPresent()) {
			plugin.getLogger().warn("Could not parse award for type " + awardType);
		}
		Award award = awardOpt.get();
		
		Set<ConstraintViolation<Award>> violations = validator.validate(award);
		if(!violations.isEmpty()) {
			plugin.getLogger().error("Invalid data for award type " + awardType + ":");
			for(ConstraintViolation<Award> violation : violations) {
				plugin.getLogger().error(violation.getMessage());
			}
			return;
		}
		
		award.awardPlayer(subject);
	}

}
