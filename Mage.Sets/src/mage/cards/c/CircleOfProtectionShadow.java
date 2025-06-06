
package mage.cards.c;

import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.PreventNextDamageFromChosenSourceEffect;
import mage.abilities.keyword.ShadowAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.mageobject.AbilityPredicate;

import java.util.UUID;

/**
 * @author LoneFox
 */
public final class CircleOfProtectionShadow extends CardImpl {

    private static final FilterPermanent filter = new FilterCreaturePermanent("a creature of your choice with shadow");

    static {
        filter.add(new AbilityPredicate(ShadowAbility.class));
    }

    public CircleOfProtectionShadow(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{1}{W}");

        // {1}: The next time a creature of your choice with shadow would deal damage to you this turn, prevent that damage.
        Effect effect = new PreventNextDamageFromChosenSourceEffect(Duration.EndOfTurn, true, filter);
        effect.setText("The next time a creature of your choice with shadow would deal damage to you this turn, prevent that damage");
        this.addAbility(new SimpleActivatedAbility(effect, new ManaCostsImpl<>("{1}")));
    }

    private CircleOfProtectionShadow(final CircleOfProtectionShadow card) {
        super(card);
    }

    @Override
    public CircleOfProtectionShadow copy() {
        return new CircleOfProtectionShadow(this);
    }
}
