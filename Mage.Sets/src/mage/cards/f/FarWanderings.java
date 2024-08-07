package mage.cards.f;

import mage.abilities.condition.common.ThresholdCondition;
import mage.abilities.decorator.ConditionalOneShotEffect;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.search.SearchLibraryPutInPlayEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.AbilityWord;
import mage.constants.CardType;
import mage.filter.StaticFilters;
import mage.target.common.TargetCardInLibrary;

import java.util.UUID;

/**
 * @author fireshoes
 */

public final class FarWanderings extends CardImpl {

    public FarWanderings(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{2}{G}");

        // Search your library for a basic land card, put that card onto the battlefield tapped, then shuffle your library.
        // Threshold - If seven or more cards are in your graveyard, instead search your library for up to three basic land cards, put them onto the battlefield tapped, then shuffle your library.
        Effect effect = new ConditionalOneShotEffect(
                new SearchLibraryPutInPlayEffect(new TargetCardInLibrary(
                        3, StaticFilters.FILTER_CARD_BASIC_LAND
                ), true),
                new SearchLibraryPutInPlayEffect(new TargetCardInLibrary(
                        1, StaticFilters.FILTER_CARD_BASIC_LAND
                ), true),
                ThresholdCondition.instance, "Search your library for a basic land card, " +
                "put that card onto the battlefield tapped, then shuffle.<br>" + AbilityWord.THRESHOLD.formatWord() +
                "If seven or more cards are in your graveyard, instead search your library for up to " +
                "three basic land cards, put them onto the battlefield tapped, then shuffle."
        );
        this.getSpellAbility().addEffect(effect);
    }

    private FarWanderings(final FarWanderings card) {
        super(card);
    }

    @Override
    public FarWanderings copy() {
        return new FarWanderings(this);
    }
}
