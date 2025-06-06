
package mage.cards.s;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.DiesAttachedTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.AttachEffect;
import mage.abilities.effects.common.continuous.BoostEnchantedEffect;
import mage.abilities.keyword.EnchantAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.Target;
import mage.target.TargetPermanent;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author spjspj
 */
public final class ScreamsFromWithin extends CardImpl {

    public ScreamsFromWithin(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{1}{B}{B}");
        this.subtype.add(SubType.AURA);

        // Enchant creature
        TargetPermanent auraTarget = new TargetCreaturePermanent();
        this.getSpellAbility().addTarget(auraTarget);
        this.getSpellAbility().addEffect(new AttachEffect(Outcome.Detriment));
        Ability ability = new EnchantAbility(auraTarget);
        this.addAbility(ability);

        // Enchanted creature gets -1/-1.
        this.addAbility(new SimpleStaticAbility(new BoostEnchantedEffect(-1, -1, Duration.WhileOnBattlefield)));

        // When enchanted creature dies, return Screams from Within from your graveyard to the battlefield.
        this.addAbility(new DiesAttachedTriggeredAbility(new ScreamsFromWithinEffect(), "enchanted creature"));
    }

    private ScreamsFromWithin(final ScreamsFromWithin card) {
        super(card);
    }

    @Override
    public ScreamsFromWithin copy() {
        return new ScreamsFromWithin(this);
    }
}

class ScreamsFromWithinEffect extends OneShotEffect {

    ScreamsFromWithinEffect() {
        super(Outcome.PutCardInPlay);
        staticText = "return this card from your graveyard to the battlefield";
    }

    private ScreamsFromWithinEffect(final ScreamsFromWithinEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card aura = game.getCard(source.getSourceId());
        Player player = game.getPlayer(source.getControllerId());
        if (aura != null && game.getState().getZone(source.getSourceId()) == Zone.GRAVEYARD && player != null && player.getGraveyard().contains(source.getSourceId())) {
            for (Permanent creaturePermanent : game.getBattlefield().getActivePermanents(StaticFilters.FILTER_PERMANENT_CREATURE, source.getControllerId(), source, game)) {
                for (Target target : aura.getSpellAbility().getTargets()) {
                    if (target.canTarget(creaturePermanent.getId(), game)) {
                        return player.moveCards(aura, Zone.BATTLEFIELD, source, game);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ScreamsFromWithinEffect copy() {
        return new ScreamsFromWithinEffect(this);
    }
}
