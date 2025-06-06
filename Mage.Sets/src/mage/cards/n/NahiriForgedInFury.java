package mage.cards.n;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.AttacksCreatureYouControlTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.asthought.PlayFromNotOwnHandZoneTargetEffect;
import mage.abilities.keyword.AffinityAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.game.Game;
import mage.players.Player;

import java.util.UUID;

/**
 * @author correl
 */
public final class NahiriForgedInFury extends CardImpl {

    private static final FilterControlledCreaturePermanent equippedFilter = new FilterControlledCreaturePermanent(
            "an equipped creature you control");

    public NahiriForgedInFury(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{4}{R}{W}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.KOR);
        this.subtype.add(SubType.ARTIFICER);
        this.power = new MageInt(5);
        this.toughness = new MageInt(4);

        // Affinity for Equipment
        this.addAbility(new AffinityAbility(AffinityType.EQUIPMENT));

        // Whenever an equipped creature you control attacks, exile the top card
        // of your library. You may play that card this turn. You may cast
        // Equipment spells this way without paying their mana costs.
        this.addAbility(new AttacksCreatureYouControlTriggeredAbility(
                new NahiriForgedInFuryEffect(), false, equippedFilter));
    }

    private NahiriForgedInFury(final NahiriForgedInFury card) {
        super(card);
    }

    @Override
    public NahiriForgedInFury copy() {
        return new NahiriForgedInFury(this);
    }
}

class NahiriForgedInFuryEffect extends OneShotEffect {
    NahiriForgedInFuryEffect() {
        super(Outcome.PlayForFree);
        this.staticText = "exile the top card of your library. You may play that card this turn. "
                + "You may cast Equipment spells this way without paying their mana costs.";
    }

    private NahiriForgedInFuryEffect(final NahiriForgedInFuryEffect effect) {
        super(effect);
    }

    @Override
    public NahiriForgedInFuryEffect copy() {
        return new NahiriForgedInFuryEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }
        Card card = controller.getLibrary().getFromTop(game);
        if (card == null) {
            return false;
        }
        boolean withoutMana = card.hasSubtype(SubType.EQUIPMENT, game);
        return PlayFromNotOwnHandZoneTargetEffect.exileAndPlayFromExile(game, source, card, TargetController.YOU,
                Duration.EndOfTurn, withoutMana, false, false);
    }
}
