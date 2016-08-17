package io.badgeup.sponge.award;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;

import javax.validation.constraints.DecimalMin;

import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.Util;

public class MonetaryAward extends Award {

	@DecimalMin(value = "0", inclusive = false)
	private final BigDecimal amount;

	private final String currencyId;

	public MonetaryAward(BadgeUpSponge plugin, JSONObject award) {
		super(plugin, award);
		this.amount = Util.safeGetBigDecimal(data, "amount").orElse(BigDecimal.ZERO);
		this.currencyId = Util.safeGetString(data, "currency").orElse("");
	}

	@Override
	public boolean awardPlayer(Player player) {
		final Optional<EconomyService> econSvcOpt = Sponge.getServiceManager().provide(EconomyService.class);
		if (!econSvcOpt.isPresent()) {
			plugin.getLogger().error("No EconomyService present. Cannot give monetary award.");
			return false;
		}
		final EconomyService economy = econSvcOpt.get();
		final Optional<UniqueAccount> accountOpt = economy.getOrCreateAccount(player.getUniqueId());
		if (!accountOpt.isPresent()) {
			plugin.getLogger().error("Unable to retrieve economy account for player " + player.getUniqueId().toString());
			return false;
		}
		final UniqueAccount playerAccount = accountOpt.get();

		Optional<Currency> currencyOpt = Optional.empty();
		if (currencyId.isEmpty()) {
			currencyOpt = Optional.of(economy.getDefaultCurrency());
		} else {
			final Iterator<Currency> iter = economy.getCurrencies().iterator();
			while (iter.hasNext()) {
				Currency currency = iter.next();
				if (currency.getId().equalsIgnoreCase(currencyId)) {
					currencyOpt = Optional.of(currency);
				}
			}
		}
		if (!currencyOpt.isPresent()) {
			plugin.getLogger().error("Could not find currency " + currencyId
					+ ". Cannot award monetary award to player " + player.getUniqueId().toString());
		}
		// TODO add more stuff to the cause chain
		TransactionResult result = playerAccount.deposit(currencyOpt.get(), amount, Cause.source(plugin.getContainer()).build());
		return result.getResult().equals(ResultType.SUCCESS);
	}

}
