package io.badgeup.sponge.award;

import io.badgeup.sponge.BadgeUpSponge;
import io.badgeup.sponge.util.Util;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;

public class MonetaryAward extends Award {

    public MonetaryAward(BadgeUpSponge plugin, JSONObject award) {
        super(plugin, award);
    }

    @Override
    public boolean awardPlayer(Player player) {
        Optional<BigDecimal> amountOpt = Util.safeGetBigDecimal(this.data, "amount");
        if (!amountOpt.isPresent()) {
            this.plugin.getLogger().error("No amount specified. Aborting.");
            return false;
        }

        final Optional<EconomyService> econSvcOpt = Sponge.getServiceManager().provide(EconomyService.class);
        if (!econSvcOpt.isPresent()) {
            this.plugin.getLogger().error("No EconomyService present. Cannot give monetary award.");
            return false;
        }
        final EconomyService economy = econSvcOpt.get();
        final Optional<UniqueAccount> accountOpt = economy.getOrCreateAccount(player.getUniqueId());
        if (!accountOpt.isPresent()) {
            this.plugin.getLogger()
                    .error("Unable to retrieve economy account for player " + player.getUniqueId().toString());
            return false;
        }
        final UniqueAccount playerAccount = accountOpt.get();

        Optional<String> currencyIDOpt = Util.safeGetString(this.data, "currency");
        Optional<Currency> currencyOpt = Optional.empty();
        if (currencyIDOpt.isPresent()) {
            final Iterator<Currency> iter = economy.getCurrencies().iterator();
            while (iter.hasNext()) {
                Currency currency = iter.next();
                if (currency.getId().equalsIgnoreCase(currencyIDOpt.get())) {
                    currencyOpt = Optional.of(currency);
                }
            }
        } else {
            currencyOpt = Optional.of(economy.getDefaultCurrency());
        }

        if (!currencyOpt.isPresent()) {
            this.plugin.getLogger().error("Could not find currency " + currencyIDOpt.get()
                    + ". Cannot award monetary award to player " + player.getUniqueId().toString());
            return false;
        }
        // TODO add more stuff to the cause chain
        TransactionResult result = playerAccount.deposit(currencyOpt.get(), amountOpt.get(),
                Cause.source(BadgeUpSponge.getContainer()).build());
        return result.getResult().equals(ResultType.SUCCESS);
    }

}
