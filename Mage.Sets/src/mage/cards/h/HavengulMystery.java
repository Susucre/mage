package mage.cards.h;

import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.TransformIntoSourceTriggeredAbility;
import mage.abilities.costs.common.PayLifeCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.TransformSourceEffect;
import mage.abilities.mana.BlackManaAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SuperType;
import mage.constants.Zone;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.ZoneChangeEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetCardInYourGraveyard;
import mage.util.CardUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author TheElk801
 */
public final class HavengulMystery extends CardImpl {

    public HavengulMystery(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.LAND}, "");
        this.supertype.add(SuperType.LEGENDARY);
        this.nightCard = true;

        // When this land transforms into Havengul Mystery, return target creature card from your graveyard to the battlefield.
        Ability ability = new TransformIntoSourceTriggeredAbility(new HavengulMysteryEffect())
                .setTriggerPhrase("When this land transforms into {this}, ");
        ability.addTarget(new TargetCardInYourGraveyard(StaticFilters.FILTER_CARD_CREATURE_YOUR_GRAVEYARD));
        this.addAbility(ability);

        // When the creature put onto the battlefield with Havengul Mystery leaves the battlefield, transform Havengul Mystery.
        this.addAbility(new HavengulMysteryLeavesAbility());

        // {T}, Pay 1 life: Add {B}.
        Ability ability2 = new BlackManaAbility();
        ability2.addCost(new PayLifeCost(1));
        this.addAbility(ability2);
    }

    private HavengulMystery(final HavengulMystery card) {
        super(card);
    }

    @Override
    public HavengulMystery copy() {
        return new HavengulMystery(this);
    }

    static String makeKey(Ability source, Game game) {
        return "HavengulMystery_" + source.getSourceId() + '_' + CardUtil.getActualSourceObjectZoneChangeCounter(game, source);
    }
}

class HavengulMysteryEffect extends OneShotEffect {

    HavengulMysteryEffect() {
        super(Outcome.Benefit);
        staticText = "return target creature card from your graveyard to the battlefield";
    }

    private HavengulMysteryEffect(final HavengulMysteryEffect effect) {
        super(effect);
    }

    @Override
    public HavengulMysteryEffect copy() {
        return new HavengulMysteryEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        Card card = game.getCard(getTargetPointer().getFirst(game, source));
        if (player == null || card == null) {
            return false;
        }
        player.moveCards(card, Zone.BATTLEFIELD, source, game);
        Permanent permanent = CardUtil.getPermanentFromCardPutToBattlefield(card, game);
        if (permanent == null) {
            return false;
        }
        String key = HavengulMystery.makeKey(source, game);
        Set<MageObjectReference> morSet;
        if (game.getState().getValue(key) != null) {
            morSet = (Set<MageObjectReference>) game.getState().getValue(key);
        } else {
            morSet = new HashSet<>();
            game.getState().setValue(key, morSet);
        }
        morSet.add(new MageObjectReference(permanent, game));
        return true;
    }
}

class HavengulMysteryLeavesAbility extends TriggeredAbilityImpl {

    HavengulMysteryLeavesAbility() {
        super(Zone.BATTLEFIELD, new TransformSourceEffect());
        setLeavesTheBattlefieldTrigger(true);
    }

    private HavengulMysteryLeavesAbility(final HavengulMysteryLeavesAbility ability) {
        super(ability);
    }

    @Override
    public HavengulMysteryLeavesAbility copy() {
        return new HavengulMysteryLeavesAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.ZONE_CHANGE;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        ZoneChangeEvent zEvent = (ZoneChangeEvent) event;
        if (zEvent.getFromZone() != Zone.BATTLEFIELD) {
            return false;
        }

        String key = HavengulMystery.makeKey(this, game);
        Set<MageObjectReference> morSet = (Set<MageObjectReference>) game.getState().getValue(key);
        return morSet != null
                && !morSet.isEmpty()
                && morSet.stream().anyMatch(mor -> mor.refersTo(zEvent.getTarget(), game));
    }

    @Override
    public String getRule() {
        return "When the creature put onto the battlefield with {this} leaves the battlefield, transform {this}.";
    }
}
