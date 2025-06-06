package mage.cards.u;

import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author wetterlicht
 */
public final class Unforge extends CardImpl {

    public Unforge(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{2}{R}");

        // Destroy target Equipment. If that Equipment was attached to a creature, Unforge deals 2 damage to that creature.
        getSpellAbility().addTarget(new TargetPermanent(StaticFilters.FILTER_PERMANENT_EQUIPMENT));
        getSpellAbility().addEffect(new DestroyTargetEffect());
        getSpellAbility().addEffect(new UnforgeEffect());
    }

    private Unforge(final Unforge card) {
        super(card);
    }

    @Override
    public Unforge copy() {
        return new Unforge(this);
    }

}

class UnforgeEffect extends OneShotEffect {

    UnforgeEffect() {
        super(Outcome.Damage);
        staticText = "If that Equipment was attached to a creature, {this} deals 2 damage to that creature.";
    }

    private UnforgeEffect(final UnforgeEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent equipment = getTargetPointer().getFirstTargetPermanentOrLKI(game, source);
        if (equipment != null) {
            Permanent creature = game.getPermanent(equipment.getAttachedTo());
            if (creature != null) {
                creature.damage(2, source.getSourceId(), source, game, false, true);
                return true;
            }
        }
        return false;
    }

    @Override
    public UnforgeEffect copy() {
        return new UnforgeEffect(this);
    }


}
