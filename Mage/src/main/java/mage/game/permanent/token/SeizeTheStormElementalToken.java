package mage.game.permanent.token;

import mage.MageInt;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.abilities.effects.common.continuous.SetBasePowerToughnessSourceEffect;
import mage.abilities.hint.Hint;
import mage.abilities.hint.StaticHint;
import mage.abilities.keyword.TrampleAbility;
import mage.constants.CardType;
import mage.constants.SubType;

/**
 * @author TheElk801
 */
public final class SeizeTheStormElementalToken extends TokenImpl {

    public SeizeTheStormElementalToken() {
        this(StaticValue.get(0), new StaticHint(""));
    }

    public SeizeTheStormElementalToken(DynamicValue xValue, Hint hint) {
        super("Elemental Token", "red Elemental creature token with trample and " +
                "\"This token's power and toughness are each equal to the number of instant " +
                "and sorcery cards in your graveyard plus the number of cards with flashback you own in exile.\"");
        cardType.add(CardType.CREATURE);
        color.setRed(true);
        subtype.add(SubType.ELEMENTAL);
        power = new MageInt(0);
        toughness = new MageInt(0);
        this.addAbility(TrampleAbility.getInstance());
        this.addAbility(new SimpleStaticAbility(new SetBasePowerToughnessSourceEffect(
                xValue
        ).setText("this token's power and toughness are each equal to the number of " +
                "instant and sorcery cards in your graveyard, plus the number of cards with flashback you own in exile")
        ).addHint(hint));
    }

    private SeizeTheStormElementalToken(final SeizeTheStormElementalToken token) {
        super(token);
    }

    @Override
    public SeizeTheStormElementalToken copy() {
        return new SeizeTheStormElementalToken(this);
    }
}
