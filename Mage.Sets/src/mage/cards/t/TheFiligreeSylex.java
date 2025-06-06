package mage.cards.t;

import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.CompositeCost;
import mage.abilities.costs.common.RemoveCounterCost;
import mage.abilities.costs.common.SacrificeSourceCost;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.effects.common.DestroyAllEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SuperType;
import mage.counters.CounterType;
import mage.filter.FilterPermanent;
import mage.filter.StaticFilters;
import mage.filter.common.FilterNonlandPermanent;
import mage.filter.predicate.mageobject.ManaValueEqualToCountersSourceCountPredicate;
import mage.target.TargetPermanent;
import mage.target.common.TargetAnyTarget;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class TheFiligreeSylex extends CardImpl {

    private static final FilterPermanent filter = new FilterNonlandPermanent(
            "each nonland permanent with mana value equal to the number of oil counters on {this}"
    );

    static {
        filter.add(new ManaValueEqualToCountersSourceCountPredicate(CounterType.OIL));
    }

    public TheFiligreeSylex(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{2}");

        this.supertype.add(SuperType.LEGENDARY);

        // {T}: Put an oil counter on The Filigree Sylex.
        this.addAbility(new SimpleActivatedAbility(
                new AddCountersSourceEffect(CounterType.OIL.createInstance()), new TapSourceCost()
        ));

        // {T}, Sacrifice The Filigree Sylex: Destroy each nonland permanent with mana value equal to the number of oil counters on The Filigree Sylex.
        Ability ability = new SimpleActivatedAbility(new DestroyAllEffect(filter), new TapSourceCost());
        ability.addCost(new SacrificeSourceCost());
        this.addAbility(ability);

        // {T}, Remove ten oil counters from among permanents you control and sacrifice The Filigree Sylex: It deals 10 damage to any target.
        ability = new SimpleActivatedAbility(new DamageTargetEffect(10, "it"), new TapSourceCost());
        ability.addCost(new CompositeCost(
                new RemoveCounterCost(new TargetPermanent(
                        0, Integer.MAX_VALUE,
                        StaticFilters.FILTER_CONTROLLED_PERMANENTS
                ), CounterType.OIL, 10), new SacrificeSourceCost(),
                "remove ten oil counters from among permanents you control and sacrifice {this}"
        ));
        ability.addTarget(new TargetAnyTarget());
        this.addAbility(ability);
    }

    private TheFiligreeSylex(final TheFiligreeSylex card) {
        super(card);
    }

    @Override
    public TheFiligreeSylex copy() {
        return new TheFiligreeSylex(this);
    }
}
