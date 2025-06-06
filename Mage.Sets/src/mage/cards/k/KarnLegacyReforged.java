package mage.cards.k;

import mage.MageInt;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.dynamicvalue.common.GreatestAmongPermanentsValue;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.SetBasePowerToughnessSourceEffect;
import mage.abilities.mana.builder.ConditionalManaBuilder;
import mage.abilities.triggers.BeginningOfUpkeepTriggeredAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.permanent.token.PowerstoneToken;
import mage.players.Player;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class KarnLegacyReforged extends CardImpl {

    public KarnLegacyReforged(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT, CardType.CREATURE}, "{5}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.GOLEM);
        this.power = new MageInt(0);
        this.toughness = new MageInt(0);

        // Karn, Legacy Reforged's power and toughness are each equal to the greatest mana value among artifacts you control.
        this.addAbility(new SimpleStaticAbility(
                Zone.ALL, new SetBasePowerToughnessSourceEffect(GreatestAmongPermanentsValue.MANAVALUE_CONTROLLED_ARTIFACTS)
                .setText("{this}'s power and toughness are each equal to the greatest mana value among artifacts you control")
        ));

        // At the beginning of your upkeep, add {C} for each artifact you control. This mana can't be spent to cast nonartifact spells. Until end of turn, you don't lose this mana as steps and phases end.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(
                new KarnLegacyReforgedEffect()
        ));
    }

    private KarnLegacyReforged(final KarnLegacyReforged card) {
        super(card);
    }

    @Override
    public KarnLegacyReforged copy() {
        return new KarnLegacyReforged(this);
    }
}

class KarnLegacyReforgedEffect extends OneShotEffect {

    KarnLegacyReforgedEffect() {
        super(Outcome.Benefit);
        staticText = "add {C} for each artifact you control. This mana can't be spent " +
                "to cast nonartifact spells. Until end of turn, you don't lose this mana as steps and phases end";
    }

    private KarnLegacyReforgedEffect(final KarnLegacyReforgedEffect effect) {
        super(effect);
    }

    @Override
    public KarnLegacyReforgedEffect copy() {
        return new KarnLegacyReforgedEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        int artifacts = game.getBattlefield().count(
                StaticFilters.FILTER_CONTROLLED_PERMANENT_ARTIFACT,
                source.getControllerId(), source, game
        );
        if (player == null || artifacts < 1) {
            return false;
        }
        ConditionalManaBuilder builder = PowerstoneToken.makeBuilder();
        builder.setMana(Mana.ColorlessMana(artifacts), source, game);
        player.getManaPool().addMana(builder.build(), game, source, true);
        return true;
    }
}
