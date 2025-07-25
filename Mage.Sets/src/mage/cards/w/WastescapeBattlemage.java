package mage.cards.w;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.condition.Condition;
import mage.abilities.condition.common.KickedCostCondition;
import mage.abilities.effects.common.CastSourceTriggeredAbility;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.abilities.effects.common.ReturnToHandTargetEffect;
import mage.abilities.keyword.KickerAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.TargetController;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterArtifactOrEnchantmentPermanent;
import mage.target.TargetPermanent;
import mage.target.common.TargetOpponentsCreaturePermanent;

import java.util.UUID;

/**
 * @author notgreat
 */
public final class WastescapeBattlemage extends CardImpl {

    private static final FilterPermanent filter
            = new FilterArtifactOrEnchantmentPermanent("artifact or enchantment an opponent controls");

    static {
        filter.add(TargetController.OPPONENT.getControllerPredicate());
    }

    private static final Condition condition = new KickedCostCondition("{G}");
    private static final Condition condition2 = new KickedCostCondition("{1}{U}");

    public WastescapeBattlemage(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{C}");

        this.subtype.add(SubType.ELDRAZI);
        this.subtype.add(SubType.WIZARD);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Kicker {G} and/or kicker {1}{U}
        KickerAbility kickerAbility = new KickerAbility("{G}");
        kickerAbility.addKickerCost("{1}{U}");
        this.addAbility(kickerAbility);

        // When you cast this spell, if it was kicked with its {G} kicker, exile target artifact or enchantment an opponent controls.
        Ability ability = new CastSourceTriggeredAbility(new ExileTargetEffect()).withInterveningIf(condition);
        ability.addTarget(new TargetPermanent(filter));
        this.addAbility(ability);

        // When you cast this spell, if it was kicked with its {1}{U} kicker, return target creature an opponent controls to its owner's hand.
        ability = new CastSourceTriggeredAbility(new ReturnToHandTargetEffect()).withInterveningIf(condition2);
        ability.addTarget(new TargetOpponentsCreaturePermanent());
        this.addAbility(ability);
    }

    private WastescapeBattlemage(final WastescapeBattlemage card) {
        super(card);
    }

    @Override
    public WastescapeBattlemage copy() {
        return new WastescapeBattlemage(this);
    }
}
