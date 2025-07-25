package mage.cards.d;

import mage.MageInt;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.common.DiesCreatureTriggeredAbility;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.condition.Condition;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.keyword.DoubleStrikeAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.permanent.token.GuenhwyvarToken;
import mage.util.CardUtil;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class DrizztDoUrden extends CardImpl {

    public DrizztDoUrden(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{G}{W}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.ELF);
        this.subtype.add(SubType.RANGER);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // Double strike
        this.addAbility(DoubleStrikeAbility.getInstance());

        // Whenever Drizzt Do'Urden enters the battlefield, create Guenhwyvar, a legendary 4/1 green Cat creature token with trample.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new CreateTokenEffect(new GuenhwyvarToken())));

        // Whenever a creature dies, if it had power greater than Drizzt's power, put a number of +1/+1 counters on Drizzt equal to the difference.
        this.addAbility(new DiesCreatureTriggeredAbility(new DrizztDoUrdenEffect(), false).withInterveningIf(DrizztDoUrdenCondition.instance));
    }

    private DrizztDoUrden(final DrizztDoUrden card) {
        super(card);
    }

    @Override
    public DrizztDoUrden copy() {
        return new DrizztDoUrden(this);
    }
}

enum DrizztDoUrdenCondition implements Condition {
    instance;

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent sourcePermanent = source.getSourcePermanentOrLKI(game);
        return sourcePermanent != null
                && CardUtil
                .getEffectValueFromAbility(source, "creatureDied", Permanent.class)
                .map(MageObject::getPower)
                .map(MageInt::getValue)
                .filter(x -> sourcePermanent.getPower().getValue() < x)
                .isPresent();
    }

    @Override
    public String toString() {
        return "it had power greater than {this}'s power";
    }
}

class DrizztDoUrdenEffect extends OneShotEffect {

    DrizztDoUrdenEffect() {
        super(Outcome.Benefit);
        staticText = "put a number of +1/+1 counters on {this} equal to the difference";
    }

    private DrizztDoUrdenEffect(final DrizztDoUrdenEffect effect) {
        super(effect);
    }

    @Override
    public DrizztDoUrdenEffect copy() {
        return new DrizztDoUrdenEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent sourcePermanent = source.getSourcePermanentOrLKI(game);
        Permanent creatureDied = (Permanent) this.getValue("creatureDied");
        if (creatureDied == null || sourcePermanent == null) {
            return false;
        }
        return sourcePermanent.addCounters(CounterType.P1P1.createInstance(
                Math.max(creatureDied.getPower().getValue() - sourcePermanent.getPower().getValue(), 0)
        ), source.getControllerId(), source, game);
    }
}
