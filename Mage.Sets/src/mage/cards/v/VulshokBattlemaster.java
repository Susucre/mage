package mage.cards.v;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.HasteAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.UUID;

/**
 * @author Derpthemeus
 */
public final class VulshokBattlemaster extends CardImpl {

    public VulshokBattlemaster(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{4}{R}");
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.WARRIOR);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Haste
        this.addAbility(HasteAbility.getInstance());

        // When Vulshok Battlemaster enters the battlefield, attach all Equipment on the battlefield to it.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new VulshokBattlemasterEffect()));
    }

    private VulshokBattlemaster(final VulshokBattlemaster card) {
        super(card);
    }

    @Override
    public VulshokBattlemaster copy() {
        return new VulshokBattlemaster(this);
    }

}

class VulshokBattlemasterEffect extends OneShotEffect {

    VulshokBattlemasterEffect() {
        super(Outcome.Benefit);
        this.staticText = "attach all Equipment on the battlefield to it";
    }

    private VulshokBattlemasterEffect(final VulshokBattlemasterEffect effect) {
        super(effect);
    }

    @Override
    public VulshokBattlemasterEffect copy() {
        return new VulshokBattlemasterEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = source.getSourcePermanentIfItStillExists(game);
        if (permanent == null) {
            return false;
        }
        for (Permanent equipment : game.getBattlefield().getActivePermanents(
                StaticFilters.FILTER_PERMANENT_EQUIPMENT, source.getControllerId(), source, game
        )) {
            permanent.addAttachment(equipment.getId(), source, game);
        }
        return true;
    }
}
