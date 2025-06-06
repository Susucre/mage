package mage.cards.r;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.condition.common.AttackedThisTurnSourceCondition;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.keyword.AffinityAbility;
import mage.abilities.keyword.HasteAbility;
import mage.abilities.keyword.TrampleAbility;
import mage.abilities.triggers.BeginningOfEndStepTriggeredAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.permanent.token.HumanSoldierToken;
import mage.players.Player;

import java.util.UUID;

/**
 * @author Susucr
 */
public final class RidersOfTheMark extends CardImpl {

    public RidersOfTheMark(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{6}{R}");

        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.KNIGHT);
        this.power = new MageInt(7);
        this.toughness = new MageInt(4);

        // Affinity for Humans
        this.addAbility(new AffinityAbility(AffinityType.HUMANS));

        // Trample
        this.addAbility(TrampleAbility.getInstance());

        // Haste
        this.addAbility(HasteAbility.getInstance());

        // At the beginning of your end step, if Riders of the Mark attacked this turn, return it to its owner's hand. If you do, create a number of 1/1 white Human Soldier creature tokens equal to its toughness.
        this.addAbility(new BeginningOfEndStepTriggeredAbility(new RidersOfTheMarkEffect())
                .withInterveningIf(AttackedThisTurnSourceCondition.instance));
    }

    private RidersOfTheMark(final RidersOfTheMark card) {
        super(card);
    }

    @Override
    public RidersOfTheMark copy() {
        return new RidersOfTheMark(this);
    }
}

class RidersOfTheMarkEffect extends OneShotEffect {

    RidersOfTheMarkEffect() {
        super(Outcome.Benefit);
        staticText = "return it to its owner's hand. If you do, create a number of "
                + "1/1 white Human Soldier creature tokens equal to its toughness.";
    }

    private RidersOfTheMarkEffect(final RidersOfTheMarkEffect effect) {
        super(effect);
    }

    @Override
    public RidersOfTheMarkEffect copy() {
        return new RidersOfTheMarkEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = source.getSourcePermanentIfItStillExists(game);
        Player player = game.getPlayer(source.getControllerId());
        if (permanent == null || player == null) {
            return false;
        }
        int toughness = permanent.getToughness().getValue();
        if (!player.moveCards(permanent, Zone.HAND, source, game)) {
            return false;
        }

        if (toughness > 0) {
            new CreateTokenEffect(new HumanSoldierToken(), toughness)
                    .apply(game, source);
        }
        return true;
    }

}
