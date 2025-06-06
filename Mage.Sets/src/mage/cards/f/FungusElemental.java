package mage.cards.f;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.condition.common.SourceEnteredThisTurnCondition;
import mage.abilities.costs.common.SacrificeTargetCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.decorator.ConditionalActivatedAbility;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.constants.SubType;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.filter.common.FilterControlledPermanent;

/**
 *
 * @author TheElk801
 */
public final class FungusElemental extends CardImpl {

    private static final FilterControlledPermanent filter = new FilterControlledPermanent("Forest");

    static {
        filter.add(SubType.FOREST.getPredicate());
    }

    public FungusElemental(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{G}");

        this.subtype.add(SubType.FUNGUS);
        this.subtype.add(SubType.ELEMENTAL);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // {G}, Sacrifice a Forest: Put a +2/+2 counter on Fungus Elemental. Activate this ability only if Fungus Elemental entered the battlefield this turn.
        Ability ability = new ConditionalActivatedAbility(
                Zone.BATTLEFIELD,
                new AddCountersSourceEffect(CounterType.P2P2.createInstance()),
                new ManaCostsImpl<>("{G}"),
                SourceEnteredThisTurnCondition.DID
        );
        ability.addCost(new SacrificeTargetCost(filter));
        this.addAbility(ability);
    }

    private FungusElemental(final FungusElemental card) {
        super(card);
    }

    @Override
    public FungusElemental copy() {
        return new FungusElemental(this);
    }
}
