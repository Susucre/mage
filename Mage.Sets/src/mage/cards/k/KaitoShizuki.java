package mage.cards.k;

import mage.abilities.Ability;
import mage.abilities.LoyaltyAbility;
import mage.abilities.triggers.BeginningOfEndStepTriggeredAbility;
import mage.abilities.condition.Condition;
import mage.abilities.condition.InvertCondition;
import mage.abilities.condition.common.RaidCondition;
import mage.abilities.condition.common.SourceEnteredThisTurnCondition;
import mage.abilities.decorator.ConditionalOneShotEffect;
import mage.abilities.effects.common.CreateTokenEffect;
import mage.abilities.effects.common.DrawCardSourceControllerEffect;
import mage.abilities.effects.common.GetEmblemEffect;
import mage.abilities.effects.common.PhaseOutSourceEffect;
import mage.abilities.effects.common.discard.DiscardControllerEffect;
import mage.abilities.hint.ConditionHint;
import mage.abilities.hint.Hint;
import mage.abilities.hint.common.RaidHint;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.game.command.emblems.KaitoShizukiEmblem;
import mage.game.permanent.token.NinjaToken;
import mage.watchers.common.PlayerAttackedWatcher;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class KaitoShizuki extends CardImpl {

    private static final Hint hint = new ConditionHint(
            SourceEnteredThisTurnCondition.DID, "This permanent entered the battlefield this turn"
    );
    private static final Condition condition = new InvertCondition(RaidCondition.instance);

    public KaitoShizuki(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.PLANESWALKER}, "{1}{U}{B}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.KAITO);
        this.setStartingLoyalty(3);

        // At the beginning of your end step, if Kaito Shizuki entered the battlefield this turn, he phases out.
        this.addAbility(new BeginningOfEndStepTriggeredAbility(
                TargetController.YOU, new PhaseOutSourceEffect().setText("he phases out"),
                false, SourceEnteredThisTurnCondition.DID
        ).addHint(hint));

        // +1: Draw a card. Then discard a card unless you attacked this turn.
        Ability ability = new LoyaltyAbility(new DrawCardSourceControllerEffect(1), 1);
        ability.addEffect(new ConditionalOneShotEffect(
                new DiscardControllerEffect(1), condition,
                "Then discard a card unless you attacked this turn"
        ));
        this.addAbility(ability.addHint(RaidHint.instance), new PlayerAttackedWatcher());

        // −2: Create a 1/1 blue Ninja creature token with "This creature can't be blocked."
        this.addAbility(new LoyaltyAbility(new CreateTokenEffect(new NinjaToken()), -2));

        // −7: You get an emblem with "Whenever a creature you control deals combat damage to a player, search your library for a blue or black creature card, put it onto the battlefield, then shuffle."
        this.addAbility(new LoyaltyAbility(new GetEmblemEffect(new KaitoShizukiEmblem()), -7));
    }

    private KaitoShizuki(final KaitoShizuki card) {
        super(card);
    }

    @Override
    public KaitoShizuki copy() {
        return new KaitoShizuki(this);
    }
}
