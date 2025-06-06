package mage.cards.k;

import mage.MageInt;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.common.TurnedFaceUpSourceTriggeredAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.asthought.PlayFromNotOwnHandZoneTargetEffect;
import mage.abilities.keyword.MorphAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.game.Game;
import mage.game.stack.StackObject;
import mage.target.TargetSpell;

import java.util.UUID;

/**
 * @author emerald000
 */
public final class KheruSpellsnatcher extends CardImpl {

    public KheruSpellsnatcher(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{U}");
        this.subtype.add(SubType.SNAKE);
        this.subtype.add(SubType.WIZARD);

        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // Morph {4}{U}{U}
        this.addAbility(new MorphAbility(this, new ManaCostsImpl<>("{4}{U}{U}")));

        // When Kheru Spellthief is turned face up, counter target spell. If that spell is countered this way, exile it instead of putting it into its owner's graveyard. You may cast that card without paying its mana cost as long as it remains exiled.
        Ability ability = new TurnedFaceUpSourceTriggeredAbility(new KheruSpellsnatcherEffect());
        ability.addTarget(new TargetSpell());
        this.addAbility(ability);
    }

    private KheruSpellsnatcher(final KheruSpellsnatcher card) {
        super(card);
    }

    @Override
    public KheruSpellsnatcher copy() {
        return new KheruSpellsnatcher(this);
    }
}

class KheruSpellsnatcherEffect extends OneShotEffect {

    KheruSpellsnatcherEffect() {
        super(Outcome.Benefit);
        this.staticText = "counter target spell. If that spell is countered this way, "
                + "exile it instead of putting it into its owner's graveyard. "
                + "You may cast that card without paying its mana cost for as long as it remains exiled";
    }

    private KheruSpellsnatcherEffect(final KheruSpellsnatcherEffect effect) {
        super(effect);
    }

    @Override
    public KheruSpellsnatcherEffect copy() {
        return new KheruSpellsnatcherEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        MageObject sourceObject = source.getSourceObject(game);
        StackObject stackObject = game.getStack().getStackObject(getTargetPointer().getFirst(game, source));
        if (stackObject != null && sourceObject != null
                && game.getStack().counter(getTargetPointer().getFirst(game, source), source, game, PutCards.EXILED)) {
            if (!stackObject.isCopy()) {
                MageObject card = game.getObject(stackObject.getSourceId());
                if (card instanceof Card) {
                    return PlayFromNotOwnHandZoneTargetEffect.exileAndPlayFromExile(game, source, (Card) card,
                            TargetController.YOU, Duration.Custom, true, false, true);
                }
            }
        }
        return false;
    }
}
