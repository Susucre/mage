package mage.cards.h;

import mage.abilities.LoyaltyAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.dynamicvalue.common.GreatestAmongPermanentsValue;
import mage.abilities.effects.common.GainLifeEffect;
import mage.abilities.effects.common.ruleModifying.CombatDamageByToughnessControlledEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.SuperType;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class HuatliTheSunsHeart extends CardImpl {

    public HuatliTheSunsHeart(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.PLANESWALKER}, "{2}{G/W}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.HUATLI);
        this.setStartingLoyalty(7);

        // Each creature you control assigns combat damage equal to its toughness rather than its power.
        this.addAbility(new SimpleStaticAbility(new CombatDamageByToughnessControlledEffect()));

        // -3: You gain life equal to the greatest toughness among creatures you control.
        this.addAbility(new LoyaltyAbility(new GainLifeEffect(
                GreatestAmongPermanentsValue.TOUGHNESS_CONTROLLED_CREATURES,
                "You gain life equal to the greatest toughness among creatures you control"
        ), -3).addHint(GreatestAmongPermanentsValue.TOUGHNESS_CONTROLLED_CREATURES.getHint()));
    }

    private HuatliTheSunsHeart(final HuatliTheSunsHeart card) {
        super(card);
    }

    @Override
    public HuatliTheSunsHeart copy() {
        return new HuatliTheSunsHeart(this);
    }
}
