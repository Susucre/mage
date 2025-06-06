package mage.cards.m;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.SacrificeOpponentsEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.filter.StaticFilters;
import mage.game.Controllable;
import mage.game.Game;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @author jeffwadsworth
 */
public final class Malfegor extends CardImpl {

    public Malfegor(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{2}{B}{B}{R}{R}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.DEMON);
        this.subtype.add(SubType.DRAGON);
        this.power = new MageInt(6);
        this.toughness = new MageInt(6);

        // Flying
        this.addAbility(FlyingAbility.getInstance());

        // When Malfegor enters the battlefield, discard your hand. Each opponent sacrifices a creature for each card discarded this way.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new MalfegorEffect(), false));
    }

    private Malfegor(final Malfegor card) {
        super(card);
    }

    @Override
    public Malfegor copy() {
        return new Malfegor(this);
    }
}

class MalfegorEffect extends OneShotEffect {

    MalfegorEffect() {
        super(Outcome.Neutral);
        staticText = "discard your hand. Each opponent sacrifices a creature for each card discarded this way";
    }

    private MalfegorEffect(final MalfegorEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return Optional
                .ofNullable(source)
                .map(Controllable::getControllerId)
                .map(game::getPlayer)
                .filter(player -> !player.getHand().isEmpty())
                .map(player -> player.discard(player.getHand(), false, source, game))
                .map(Set::size)
                .filter(amount -> amount > 0 && new SacrificeOpponentsEffect(
                        amount, StaticFilters.FILTER_CONTROLLED_CREATURE
                ).apply(game, source))
                .isPresent();
    }

    @Override
    public MalfegorEffect copy() {
        return new MalfegorEffect(this);
    }
}
