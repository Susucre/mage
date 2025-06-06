package mage.cards.j;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldControlledTriggeredAbility;
import mage.abilities.effects.common.DontUntapInControllersNextUntapStepTargetEffect;
import mage.abilities.effects.common.TapTargetEffect;
import mage.abilities.keyword.AffinityAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.AffinityType;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.filter.StaticFilters;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class JunkWinder extends CardImpl {

    public JunkWinder(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{5}{U}{U}");

        this.subtype.add(SubType.SERPENT);
        this.power = new MageInt(5);
        this.toughness = new MageInt(6);

        // Affinity for tokens
        this.addAbility(new AffinityAbility(AffinityType.TOKENS));

        // Whenever a token you control enters, tap target nonland permanent an opponent controls. It doesn't untap during its controller's next untap step.
        Ability ability = new EntersBattlefieldControlledTriggeredAbility(
                new TapTargetEffect(), StaticFilters.FILTER_PERMANENT_TOKEN
        );
        ability.addEffect(new DontUntapInControllersNextUntapStepTargetEffect("It"));
        ability.addTarget(new TargetPermanent(StaticFilters.FILTER_OPPONENTS_PERMANENT_NON_LAND));
        this.addAbility(ability);
    }

    private JunkWinder(final JunkWinder card) {
        super(card);
    }

    @Override
    public JunkWinder copy() {
        return new JunkWinder(this);
    }
}
