
package mage.cards.c;

import mage.MageInt;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.common.DealsDamageToACreatureTriggeredAbility;
import mage.abilities.common.LimitedTimesPerTurnActivatedAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.abilities.effects.common.LookLibraryControllerEffect;
import mage.abilities.effects.common.continuous.GainAbilitySourceEffect;
import mage.cards.*;
import mage.constants.*;
import mage.game.Game;
import mage.players.Player;

import java.util.UUID;

/**
 *
 * @author LevelX2
 */
public final class CruelDeceiver extends CardImpl {

    public CruelDeceiver(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{B}");
        this.subtype.add(SubType.SPIRIT);

        this.power = new MageInt(2);
        this.toughness = new MageInt(1);

        // {1}: Look at the top card of your library.
        this.addAbility(new SimpleActivatedAbility(new LookLibraryControllerEffect(), new GenericManaCost(1)));

        // {2}: Reveal the top card of your library. If it's a land card, Cruel Deceiver gains "Whenever Cruel Deceiver deals damage to a creature, destroy that creature" until end of turn. Activate this ability only once each turn.
        this.addAbility(new LimitedTimesPerTurnActivatedAbility(Zone.BATTLEFIELD, new CruelDeceiverEffect(), new ManaCostsImpl<>("{2}")));
    }

    private CruelDeceiver(final CruelDeceiver card) {
        super(card);
    }

    @Override
    public CruelDeceiver copy() {
        return new CruelDeceiver(this);
    }
}

class CruelDeceiverEffect extends OneShotEffect {

    CruelDeceiverEffect() {
        super(Outcome.AddAbility);
        this.staticText = "Reveal the top card of your library. If it's a land card, {this} gains \"Whenever {this} deals damage to a creature, destroy that creature\" until end of turn";
    }

    private CruelDeceiverEffect(final CruelDeceiverEffect effect) {
        super(effect);
    }

    @Override
    public CruelDeceiverEffect copy() {
        return new CruelDeceiverEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        MageObject sourceObject = source.getSourceObject(game);
        if (controller != null && sourceObject != null) {
            Cards cards = new CardsImpl();
            Card card = controller.getLibrary().getFromTop(game);
            if (card != null) {
                cards.add(card);
                controller.revealCards(sourceObject.getIdName(), cards, game);
                if (card.isLand(game)) {
                    game.addEffect(new GainAbilitySourceEffect(new DealsDamageToACreatureTriggeredAbility(new DestroyTargetEffect(true), false, false, true), Duration.EndOfTurn), source);
                }
            }
            return true;
        }
        return false;
    }
}
