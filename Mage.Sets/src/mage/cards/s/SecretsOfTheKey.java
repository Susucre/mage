package mage.cards.s;

import mage.abilities.condition.common.CastFromGraveyardSourceCondition;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.decorator.ConditionalOneShotEffect;
import mage.abilities.effects.keyword.InvestigateEffect;
import mage.abilities.keyword.FlashbackAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class SecretsOfTheKey extends CardImpl {

    public SecretsOfTheKey(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{U}");

        // Investigate. If this spell was cast from a graveyard, investigate twice instead.
        this.getSpellAbility().addEffect(new ConditionalOneShotEffect(
                new InvestigateEffect(2), new InvestigateEffect(), CastFromGraveyardSourceCondition.instance,
                "Investigate. If this spell was cast from a graveyard, investigate twice instead."
        ));

        // Flashback {3}{U}
        this.addAbility(new FlashbackAbility(this, new ManaCostsImpl<>("{3}{U}")));
    }

    private SecretsOfTheKey(final SecretsOfTheKey card) {
        super(card);
    }

    @Override
    public SecretsOfTheKey copy() {
        return new SecretsOfTheKey(this);
    }
}
