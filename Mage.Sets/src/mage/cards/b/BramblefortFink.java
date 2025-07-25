package mage.cards.b;

import mage.MageInt;
import mage.abilities.common.ActivateIfConditionActivatedAbility;
import mage.abilities.condition.Condition;
import mage.abilities.condition.common.PermanentsOnTheBattlefieldCondition;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.common.continuous.SetBasePowerToughnessSourceEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;
import mage.filter.common.FilterControlledPlaneswalkerPermanent;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class BramblefortFink extends CardImpl {

    private static final Condition condition = new PermanentsOnTheBattlefieldCondition(
            new FilterControlledPlaneswalkerPermanent(SubType.OKO, "you control an Oko planeswalker")
    );

    public BramblefortFink(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{G}");

        this.subtype.add(SubType.OUPHE);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // {8}: Bramblefort Fink has base power and toughness 10/10 until end of turn. Activate this ability only if you control an Oko planeswalker.
        this.addAbility(new ActivateIfConditionActivatedAbility(
                new SetBasePowerToughnessSourceEffect(
                        10, 10, Duration.EndOfTurn
                ), new GenericManaCost(8), condition
        ));
    }

    private BramblefortFink(final BramblefortFink card) {
        super(card);
    }

    @Override
    public BramblefortFink copy() {
        return new BramblefortFink(this);
    }
}
