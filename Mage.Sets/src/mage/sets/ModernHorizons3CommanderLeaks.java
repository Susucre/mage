package mage.sets;

import mage.cards.ExpansionSet;
import mage.constants.Rarity;
import mage.constants.SetType;

/**
 * Those cards are not yet confirmed to be true.
 * They seem legit enough, that implementing seemed worthwhile.
 * Set to Custom for only opt-in use them (like in cube list).
 * <p>
 * TODO: Move cards to M3C and remove this Set once confirmed real cards, or remove if confirmed fake.
 *
 * @author Susucr
 */
public final class ModernHorizons3CommanderLeaks extends ExpansionSet {

    private static final ModernHorizons3CommanderLeaks instance = new ModernHorizons3CommanderLeaks();

    public static ModernHorizons3CommanderLeaks getInstance() {
        return instance;
    }

    private ModernHorizons3CommanderLeaks() {
        super("Modern Horizons 3 Commander Leaks", "M3CL", ExpansionSet.buildDate(2024, 6, 7), SetType.CUSTOM_SET);
        this.hasBasicLands = false;
        this.hasBoosters = false;

        cards.add(new SetCardInfo("Pyrogoyf", 111, Rarity.RARE, mage.cards.p.Pyrogoyf.class));
    }
}
