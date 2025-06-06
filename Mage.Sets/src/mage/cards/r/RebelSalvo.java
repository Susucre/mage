package mage.cards.r;

import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.effects.common.continuous.LoseAbilityTargetEffect;
import mage.abilities.keyword.AffinityAbility;
import mage.abilities.keyword.IndestructibleAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.AffinityType;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.target.common.TargetCreatureOrPlaneswalker;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class RebelSalvo extends CardImpl {

    public RebelSalvo(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{2}{R}");

        // Affinity for Equipment
        this.addAbility(new AffinityAbility(AffinityType.EQUIPMENT));

        // Rebel Salvo deals 5 damage to target creature or planeswalker. That permanent loses indestructible until end of turn.
        this.getSpellAbility().addEffect(new DamageTargetEffect(5));
        this.getSpellAbility().addTarget(new TargetCreatureOrPlaneswalker());
        this.getSpellAbility().addEffect(new LoseAbilityTargetEffect(IndestructibleAbility.getInstance(), Duration.EndOfTurn)
                .setText("that permanent loses indestructible until end of turn"));
    }

    private RebelSalvo(final RebelSalvo card) {
        super(card);
    }

    @Override
    public RebelSalvo copy() {
        return new RebelSalvo(this);
    }
}
