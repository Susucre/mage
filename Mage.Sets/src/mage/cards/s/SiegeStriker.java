package mage.cards.s;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.AttacksTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.BoostSourceEffect;
import mage.abilities.keyword.DoubleStrikeAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author mikalinn777
 */
public final class SiegeStriker extends CardImpl {

    public SiegeStriker(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{W}");

        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.SOLDIER);
        this.power = new MageInt(1);
        this.toughness = new MageInt(1);

        // Double Strike
        this.addAbility(DoubleStrikeAbility.getInstance());

        // Whenever Siege Striker attacks, you may tap any number of untapped creatures you control. Siege Striker gets +1/+1 until end of turn for each creature tapped this way.
        this.addAbility(new AttacksTriggeredAbility(new SiegeStrikerEffect(), true));
    }

    private SiegeStriker(final SiegeStriker card) {
        super(card);
    }

    @Override
    public SiegeStriker copy() {
        return new SiegeStriker(this);
    }
}

class SiegeStrikerEffect extends OneShotEffect {

    public SiegeStrikerEffect() {
        super(Outcome.GainLife);
        staticText = "you may tap any number of untapped creatures you control. "
                + "{this} gets +1/+1 until end of turn for each creature tapped this way";
    }

    private SiegeStrikerEffect(final SiegeStrikerEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        int tappedAmount = 0;
        TargetPermanent target = new TargetPermanent(0, Integer.MAX_VALUE, StaticFilters.FILTER_CONTROLLED_UNTAPPED_CREATURES, true);
        target.choose(Outcome.Tap, source.getControllerId(), source.getSourceId(), source, game);
        for (UUID creatureId : target.getTargets()) {
            Permanent creature = game.getPermanent(creatureId);
            if (creature != null) {
                creature.tap(source, game);
                tappedAmount++;
            }
        }
        if (tappedAmount > 0) {
            game.addEffect(new BoostSourceEffect(tappedAmount, tappedAmount, Duration.EndOfTurn), source);
            return true;
        }
        return false;
    }

    @Override
    public SiegeStrikerEffect copy() {
        return new SiegeStrikerEffect(this);
    }

}
