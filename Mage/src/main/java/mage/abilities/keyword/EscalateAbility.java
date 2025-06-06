
package mage.abilities.keyword;

import mage.abilities.Ability;
import mage.abilities.Mode;
import mage.abilities.SpellAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.Cost;
import mage.abilities.costs.mana.ManaCost;
import mage.abilities.effects.common.cost.CostModificationEffectImpl;
import mage.constants.CostModificationType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.util.CardUtil;

/**
 * @author emerald000
 */
public class EscalateAbility extends SimpleStaticAbility {

    public EscalateAbility(Cost cost) {
        super(Zone.ALL, new EscalateEffect(cost));
        this.setRuleAtTheTop(true);
    }

    protected EscalateAbility(final EscalateAbility ability) {
        super(ability);
    }

    @Override
    public EscalateAbility copy() {
        return new EscalateAbility(this);
    }
}

class EscalateEffect extends CostModificationEffectImpl {

    private final Cost cost;

    EscalateEffect(Cost cost) {
        super(Duration.WhileOnBattlefield, Outcome.Detriment, CostModificationType.INCREASE_COST);
        this.cost = cost;
    }

    private EscalateEffect(final EscalateEffect effect) {
        super(effect);
        this.cost = effect.cost.copy();
    }

    @Override
    public boolean apply(Game game, Ability source, Ability abilityToModify) {
        int numCosts = abilityToModify.getModes().getSelectedModes().size() - 1;
        for (int i = 0; i < numCosts; i++) {
            abilityToModify.addCost(cost.copy());
        }
        return true;
    }

    @Override
    public boolean applies(Ability abilityToModify, Ability source, Game game) {
        if (abilityToModify.getSourceId().equals(source.getSourceId()) && (abilityToModify instanceof SpellAbility)) {
            return abilityToModify.getModes().getSelectedModes().size() > 1;
        }
        return false;
    }

    @Override
    public EscalateEffect copy() {
        return new EscalateEffect(this);
    }

    @Override
    public String getText(Mode mode) {
        StringBuilder sb = new StringBuilder("Escalate");
        if (cost instanceof ManaCost) {
            sb.append(' ');
            sb.append(cost.getText());
        } else {
            sb.append("&mdash;");
            sb.append(CardUtil.getTextWithFirstCharUpperCase(cost.getText()));
            sb.append('.');
        }
        sb.append(" <i>(Pay this cost for each mode chosen beyond the first.)</i>");
        return sb.toString();
    }
}
