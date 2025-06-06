
package mage.cards.o;

import mage.ObjectColor;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.common.SpellCastControllerTriggeredAbility;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.effects.common.cost.SpellsCostReductionControllerEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SuperType;
import mage.filter.FilterCard;
import mage.filter.StaticFilters;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.ColorPredicate;
import mage.game.permanent.token.WarriorVigilantToken;

import java.util.UUID;

/**
 * @author fireshoes
 */
public final class OketrasMonument extends CardImpl {

    private static final FilterCard filter = new FilterCard("White creature spells");

    static {
        filter.add(Predicates.and(new ColorPredicate(ObjectColor.WHITE), CardType.CREATURE.getPredicate()));
    }

    public OketrasMonument(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{3}");

        this.supertype.add(SuperType.LEGENDARY);

        // White creature spells you cast cost {1} less to cast.
        this.addAbility(new SimpleStaticAbility(new SpellsCostReductionControllerEffect(filter, 1)));

        // Whenever you cast a creature spell, create a 1/1 white Warrior creature token with vigilance.
        this.addAbility(new SpellCastControllerTriggeredAbility(
                new CreateTokenEffect(new WarriorVigilantToken()),
                StaticFilters.FILTER_SPELL_A_CREATURE, false
        ));
    }

    private OketrasMonument(final OketrasMonument card) {
        super(card);
    }

    @Override
    public OketrasMonument copy() {
        return new OketrasMonument(this);
    }
}
