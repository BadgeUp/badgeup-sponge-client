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
import io.badgeup.sponge.award.ItemAward;
import io.badgeup.sponge.award.MonetaryAward;
import io.badgeup.sponge.service.AchievementPersistenceService;

public class ProcessAchievementRunnable implements Runnable {
	
	private BadgeUpSponge plugin;
	private UUID subjectId;
	private JSONObject achievement;
	private Player subject;
	private Validator validator;

	public ProcessAchievementRunnable(BadgeUpSponge plugin, UUID subjectId, JSONObject achievement) {
		this.plugin = plugin;
		this.subjectId = subjectId;
		this.achievement = achievement;
	    this.validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Override
	public void run() {
		final Optional<Player> subjectOpt = Sponge.getServer().getPlayer(subjectId);
		if(!subjectOpt.isPresent()) {
			AchievementPersistenceService aps = Sponge.getServiceManager().provide(AchievementPersistenceService.class).get();
			aps.addUnpresentedAchievement(subjectId, achievement);
			return;
		}
		this.subject = subjectOpt.get();
		
		
		
		achievement.getJSONArray("awards").forEach(this::processAward);
	}
	
	private void processAward(Object obj) {
		final JSONObject awardJSON = (JSONObject) obj;
		final String awardType = awardJSON.getJSONObject("data").getString("type");
		Optional<Award> awardOpt = Optional.empty();
		switch(awardType.toLowerCase()) {
			case "monetary":
				awardOpt = Optional.of(new MonetaryAward(plugin, awardJSON));
				break;
			case "item":
				awardOpt = Optional.of(new ItemAward(plugin, awardJSON));
				break;
			default:
				awardOpt = Optional.empty();
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
