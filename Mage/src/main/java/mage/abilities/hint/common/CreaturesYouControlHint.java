package mage.abilities.hint.common;

import mage.abilities.Ability;
import mage.abilities.dynamicvalue.common.CreaturesYouControlCount;
import mage.abilities.hint.Hint;
import mage.abilities.hint.ValueHint;
import mage.game.Game;

/**
 * @author JayDi85
 */
public enum CreaturesYouControlHint implements Hint {

    instance;
    private static final Hint hint = new ValueHint("Creatures you control", CreaturesYouControlCount.PLURAL);

    @Override
    public String getText(Game game, Ability ability) {
        return hint.getText(game, ability);
    }

    @Override
    public Hint copy() {
        return instance;
    }
}
