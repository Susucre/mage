package mage.cards.v;

import mage.abilities.costs.common.DiscardXTargetCost;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.filter.FilterCard;
import mage.filter.StaticFilters;
import mage.target.TargetPermanent;
import mage.target.targetadjustment.XTargetsCountAdjuster;

import java.util.UUID;

/**
 * @author fireshoes
 */
public final class VengefulDreams extends CardImpl {

    public VengefulDreams(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{W}{W}");

        // As an additional cost to cast this spell, discard X cards.
        this.getSpellAbility().addCost(new DiscardXTargetCost(StaticFilters.FILTER_CARD_CARDS, true));

        // Exile X target attacking creatures.
        Effect effect = new ExileTargetEffect();
        effect.setText("Exile X target attacking creatures");
        this.getSpellAbility().addEffect(effect);
        this.getSpellAbility().addTarget(new TargetPermanent(StaticFilters.FILTER_ATTACKING_CREATURES));
        this.getSpellAbility().setTargetAdjuster(new XTargetsCountAdjuster());
    }

    private VengefulDreams(final VengefulDreams card) {
        super(card);
    }

    @Override
    public VengefulDreams copy() {
        return new VengefulDreams(this);
    }
}
