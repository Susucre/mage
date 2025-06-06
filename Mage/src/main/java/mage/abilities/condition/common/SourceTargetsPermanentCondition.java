package mage.abilities.condition.common;

import mage.abilities.Ability;
import mage.abilities.condition.Condition;
import mage.filter.FilterPermanent;
import mage.game.Game;
import mage.game.stack.StackObject;
import mage.util.CardUtil;

import java.util.Objects;

/**
 * @author TheElk801
 */
public class SourceTargetsPermanentCondition implements Condition {

    private final FilterPermanent filter;

    public SourceTargetsPermanentCondition(FilterPermanent filter) {
        this.filter = filter;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        StackObject stackObject = game.getStack().getStackObject(source.getSourceId());
        if (stackObject == null) {
            return false;
        }
        return CardUtil.getAllSelectedTargets(stackObject.getStackAbility(), game)
                .stream()
                .map(game::getPermanentOrLKIBattlefield)
                .filter(Objects::nonNull)
                .anyMatch(p -> filter.match(p, source.getControllerId(), source, game));
    }

    @Override
    public String toString() {
        return "it targets " + CardUtil.addArticle(filter.getMessage());
    }

}
