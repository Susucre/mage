package mage.cards.g;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.AttacksTriggeredAbility;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.game.Controllable;
import mage.game.Game;
import mage.game.permanent.token.GoblinToken;

import java.util.Optional;
import java.util.UUID;

/**
 * @author TheElk801
 */
public final class GoblinTraprunner extends CardImpl {

    public GoblinTraprunner(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{R}");

        this.subtype.add(SubType.GOBLIN);
        this.power = new MageInt(4);
        this.toughness = new MageInt(2);

        // Whenever Goblin Traprunner attacks, flip three coins. For each flip you win, create a 1/1 red Goblin creature token that's tapped and attacking.
        this.addAbility(new AttacksTriggeredAbility(new CreateTokenEffect(
                new GoblinToken(), GoblinTraprunnerValue.instance, true, true
        ).setText("flip three coins. For each flip you win, " +
                "create a 1/1 red Goblin creature token that's tapped and attacking"), false));
    }

    private GoblinTraprunner(final GoblinTraprunner card) {
        super(card);
    }

    @Override
    public GoblinTraprunner copy() {
        return new GoblinTraprunner(this);
    }
}

enum GoblinTraprunnerValue implements DynamicValue {
    instance;

    @Override
    public int calculate(Game game, Ability sourceAbility, Effect effect) {
        return Optional
                .ofNullable(sourceAbility)
                .map(Controllable::getControllerId)
                .map(game::getPlayer)
                .map(player -> player
                        .flipCoins(sourceAbility, game, 3, true)
                        .stream()
                        .mapToInt(x -> x ? 1 : 0)
                        .sum()
                ).orElse(0);
    }

    @Override
    public GoblinTraprunnerValue copy() {
        return instance;
    }

    @Override
    public String getMessage() {
        return "";
    }
}
