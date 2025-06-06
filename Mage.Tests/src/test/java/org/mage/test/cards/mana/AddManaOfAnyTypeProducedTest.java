package org.mage.test.cards.mana;

import mage.constants.ManaType;
import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.counters.CounterType;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 * @author LevelX2
 */
public class AddManaOfAnyTypeProducedTest extends CardTestPlayerBase {

    /**
     * Vorinclex, Voice of Hunger is not mana doubling River of Tears.
     */
    @Test
    public void testRiverOfTears() {
        // Trample
        // Whenever you tap a land for mana, add one mana of any type that land produced.
        // Whenever an opponent taps a land for mana, that land doesn't untap during its controller's next untap step.
        addCard(Zone.BATTLEFIELD, playerA, "Vorinclex, Voice of Hunger", 1);
        // {T}: Add {U}. If you played a land this turn, add {B} instead.
        addCard(Zone.BATTLEFIELD, playerA, "River of Tears", 1);
        addCard(Zone.HAND, playerA, "Vedalken Mastermind", 1);

        // because available mana calculation does not work correctly with Vorinclex, Voice of Hunger we have to tap the land manually
        activateManaAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "{T}: Add {U}");
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Vedalken Mastermind");

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Vedalken Mastermind", 1);
    }

    /**
     * Vorinclex, Voice of Hunger is not mana doubling River of Tears.
     */
    @Test
    public void testVorinclexVoiceofHungerRiverOfTearsManaMultiplier() {
        // Mana pools don't empty as steps and phases end.
        addCard(Zone.BATTLEFIELD, playerA, "Upwelling", 1);
        addCard(Zone.HAND, playerA, "River of Tears", 1);
        // Trample
        // Whenever you tap a land for mana, add one mana of any type that land produced.
        // Whenever an opponent taps a land for mana, that land doesn't untap during its controller's next untap step.
        addCard(Zone.BATTLEFIELD, playerA, "Vorinclex, Voice of Hunger", 1);
        // {T}: Add {U}. If you played a land this turn, add {B} instead.
        playLand(1, PhaseStep.PRECOMBAT_MAIN, playerA, "River of Tears");

        activateManaAbility(3, PhaseStep.PRECOMBAT_MAIN, playerA, "{T}: Add {U}");

        setStrictChooseMode(true);
        setStopAt(3, PhaseStep.BEGIN_COMBAT);
        execute();

        assertManaPool(playerA, ManaType.BLUE, 2);
    }

    /**
     * Vorinclex glitches with Gemstone Cavern
     */
    @Test
    public void testGemstoneCavern() {
        // Trample
        // Whenever you tap a land for mana, add one mana of any type that land produced.
        // Whenever an opponent taps a land for mana, that land doesn't untap during its controller's next untap step.
        addCard(Zone.BATTLEFIELD, playerB, "Vorinclex, Voice of Hunger", 1); // {6}{G}{G}

        // If Gemstone Caverns is in your opening hand and you're not playing first, you may begin the game with Gemstone Caverns on the battlefield with a luck counter on it. If you do, exile a card from your hand.
        // {T}: Add {C}. If Gemstone Caverns has a luck counter on it, instead add one mana of any color.
        addCard(Zone.HAND, playerB, "Gemstone Caverns", 1);
        addCard(Zone.HAND, playerB, "Swamp", 1);

        addCard(Zone.HAND, playerB, "Silvercoat Lion", 2);

        // pay and put Gemstone to battlefield on starting
        setChoice(playerB, true);
        setChoice(playerB, "Swamp");

        activateManaAbility(2, PhaseStep.PRECOMBAT_MAIN, playerB, "{T}: Add");
        setChoice(playerB, "White");
        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerB, "Silvercoat Lion");

        setStrictChooseMode(true);
        setStopAt(2, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerB, "Gemstone Caverns", 1);
        assertCounterCount("Gemstone Caverns", CounterType.LUCK, 1);
        assertPermanentCount(playerB, "Silvercoat Lion", 1);
        assertExileCount("Swamp", 1);
        assertTapped("Gemstone Caverns", true);

    }

    @Test
    public void testCastWithGemstoneCavern() {
        // Trample
        // Whenever you tap a land for mana, add one mana of any type that land produced.
        // Whenever an opponent taps a land for mana, that land doesn't untap during its controller's next untap step.
        addCard(Zone.HAND, playerB, "Vorinclex, Voice of Hunger", 1); // {6}{G}{G}
        addCard(Zone.BATTLEFIELD, playerB, "Forest", 7);

        // If Gemstone Caverns is in your opening hand and you're not playing first, you may begin the game with Gemstone Caverns on the battlefield with a luck counter on it. If you do, exile a card from your hand.
        // {T}: Add {C}. If Gemstone Caverns has a luck counter on it, instead add one mana of any color.
        addCard(Zone.HAND, playerB, "Gemstone Caverns", 1);
        addCard(Zone.HAND, playerB, "Swamp", 1);

        addCard(Zone.HAND, playerB, "Silvercoat Lion", 2);

        // pay and put Gemstone to battlefield on starting
        setChoice(playerB, true);
        setChoice(playerB, "Swamp");

        activateManaAbility(2, PhaseStep.PRECOMBAT_MAIN, playerB, "{T}: Add {C}");
        setChoice(playerB, "White");
        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerB, "Vorinclex, Voice of Hunger");

        setStrictChooseMode(true);
        setStopAt(2, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerB, "Gemstone Caverns", 1);
        assertCounterCount("Gemstone Caverns", CounterType.LUCK, 1);
        assertPermanentCount(playerB, "Vorinclex, Voice of Hunger", 1);
        assertTapped("Gemstone Caverns", true);
    }

    private static final String kinnan = "Kinnan, Bonder Prodigy";
    private static final String signet = "Gruul Signet";
    private static final String addRG = "{1}, {T}: Add {R}{G}";

    @Test
    public void testChooseColorRed() {
        addCard(Zone.BATTLEFIELD, playerA, kinnan); // Whenever you tap a nonland permanent for mana, add one mana of any type that permanent produced.
        addCard(Zone.BATTLEFIELD, playerA, "Wastes");
        addCard(Zone.BATTLEFIELD, playerA, signet);
        String raid = "Massive Raid"; // Massive Raid deals damage to any target equal to the number of creatures you control.
        addCard(Zone.HAND, playerA, raid); // 1RR

        activateManaAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, addRG);
        setChoice(playerA, "Red");
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, raid, playerB);

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertTapped(signet, true);
        assertTapped("Wastes", true);
        assertDamageReceived(playerA, kinnan, 0);
        assertLife(playerA, 20);
        assertLife(playerB, 19);

    }

    @Test
    public void testChooseColorGreen() {
        addCard(Zone.BATTLEFIELD, playerA, kinnan); // Whenever you tap a nonland permanent for mana, add one mana of any type that permanent produced.
        addCard(Zone.BATTLEFIELD, playerA, "Wastes");
        addCard(Zone.BATTLEFIELD, playerA, signet);
        String hailstorm = "Hail Storm"; // Hail Storm deals 2 damage to each attacking creature and 1 damage to you and each creature you control.
        addCard(Zone.HAND, playerA, hailstorm); // 1GG

        activateManaAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, addRG);
        setChoice(playerA, "Green");
        waitStackResolved(1, PhaseStep.PRECOMBAT_MAIN);
        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, hailstorm);

        setStrictChooseMode(true);
        setStopAt(1, PhaseStep.POSTCOMBAT_MAIN);
        execute();

        assertTapped(signet, true);
        assertTapped("Wastes", true);
        assertDamageReceived(playerA, kinnan, 1);
        assertLife(playerA, 19);
        assertLife(playerB, 20);

    }

}
