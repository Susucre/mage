
package mage.cards.r;

import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.PreventNextDamageFromChosenSourceEffect;
import mage.abilities.keyword.CyclingAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.filter.FilterSource;

import java.util.UUID;

/**
 * @author fireshoes
 */
public final class RuneOfProtectionArtifacts extends CardImpl {

    private static final FilterSource filter = new FilterSource("artifact source");

    static {
        filter.add(CardType.ARTIFACT.getPredicate());
    }

    public RuneOfProtectionArtifacts(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{1}{W}");


        // {W}: The next time an artifact source of your choice would deal damage to you this turn, prevent that damage.
        Effect effect = new PreventNextDamageFromChosenSourceEffect(Duration.EndOfTurn, true, filter);
        this.addAbility(new SimpleActivatedAbility(effect, new ManaCostsImpl<>("{W}")));
        // Cycling {2} ({2}, Discard this card: Draw a card.)
        this.addAbility(new CyclingAbility(new ManaCostsImpl<>("{2}")));
    }

    private RuneOfProtectionArtifacts(final RuneOfProtectionArtifacts card) {
        super(card);
    }

    @Override
    public RuneOfProtectionArtifacts copy() {
        return new RuneOfProtectionArtifacts(this);
    }
}
