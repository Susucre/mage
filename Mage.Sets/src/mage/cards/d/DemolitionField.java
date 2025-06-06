package mage.cards.d;

import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.SacrificeSourceCost;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.abilities.effects.common.search.SearchLibraryPutInPlayEffect;
import mage.abilities.effects.common.search.SearchLibraryPutInPlayTargetControllerEffect;
import mage.abilities.mana.ColorlessManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SuperType;
import mage.constants.TargetController;
import mage.filter.FilterPermanent;
import mage.filter.StaticFilters;
import mage.filter.common.FilterLandPermanent;
import mage.filter.predicate.Predicates;
import mage.target.TargetPermanent;
import mage.target.common.TargetCardInLibrary;

import java.util.UUID;

/**
 * @author weirddan455
 */
public final class DemolitionField extends CardImpl {

    private static final FilterPermanent filter = new FilterLandPermanent("nonbasic land an opponent controls");

    static {
        filter.add(TargetController.OPPONENT.getControllerPredicate());
        filter.add(Predicates.not(SuperType.BASIC.getPredicate()));
    }

    public DemolitionField(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.LAND}, "");

        // {T}: Add {C}.
        this.addAbility(new ColorlessManaAbility());

        // {2}, {T}, Sacrifice Demolition Field: Destroy target nonbasic land an opponent controls.
        // That land's controller may search their library for a basic land card, put it onto the battlefield, then shuffle.
        // You may search your library for a basic land card, put it onto the battlefield, then shuffle.
        Ability ability = new SimpleActivatedAbility(new DestroyTargetEffect(), new GenericManaCost(2));
        ability.addCost(new TapSourceCost());
        ability.addCost(new SacrificeSourceCost());
        ability.addEffect(new SearchLibraryPutInPlayTargetControllerEffect(
                new TargetCardInLibrary(StaticFilters.FILTER_CARD_BASIC_LAND_A),
                false, Outcome.PutLandInPlay, "that land's controller"
        ));
        ability.addEffect(new SearchLibraryPutInPlayEffect(
                new TargetCardInLibrary(StaticFilters.FILTER_CARD_BASIC_LAND_A),
                false, false, true
        ));
        ability.addTarget(new TargetPermanent(filter));
        this.addAbility(ability);
    }

    private DemolitionField(final DemolitionField card) {
        super(card);
    }

    @Override
    public DemolitionField copy() {
        return new DemolitionField(this);
    }
}
