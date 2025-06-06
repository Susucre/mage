package mage.abilities.effects.common;

import mage.abilities.Ability;
import mage.abilities.Mode;
import mage.abilities.effects.PreventionEffectImpl;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.filter.FilterSource;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.target.TargetSource;

/**
 * @author emerald000
 */
public class PreventNextDamageFromChosenSourceToTargetEffect extends PreventionEffectImpl {

    protected final TargetSource targetSource;

    public PreventNextDamageFromChosenSourceToTargetEffect(Duration duration) {
        this(duration, new FilterSource());
    }

    public PreventNextDamageFromChosenSourceToTargetEffect(Duration duration, FilterSource filter) {
        this(duration, filter, false);
    }

    public PreventNextDamageFromChosenSourceToTargetEffect(Duration duration, FilterSource filter, boolean onlyCombat) {
        super(duration, Integer.MAX_VALUE, onlyCombat);
        this.targetSource = new TargetSource(filter);
    }

    protected PreventNextDamageFromChosenSourceToTargetEffect(final PreventNextDamageFromChosenSourceToTargetEffect effect) {
        super(effect);
        this.targetSource = effect.targetSource.copy();
    }

    @Override
    public PreventNextDamageFromChosenSourceToTargetEffect copy() {
        return new PreventNextDamageFromChosenSourceToTargetEffect(this);
    }

    @Override
    public void init(Ability source, Game game) {
        super.init(source, game);
        this.targetSource.choose(Outcome.PreventDamage, source.getControllerId(), source.getSourceId(), source, game);
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        preventDamageAction(event, source, game);
        this.used = true;
        return false;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (!this.used && super.applies(event, source, game)) {
            if (event.getTargetId().equals(getTargetPointer().getFirst(game, source)) && event.getSourceId().equals(targetSource.getFirstTarget())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getText(Mode mode) {
        if (staticText != null && !staticText.isEmpty()) {
            return staticText;
        }
        StringBuilder sb = new StringBuilder("The next time a ").append(targetSource.getFilter().getMessage());
        sb.append(" of your choice would deal damage to ");
        sb.append(getTargetPointer().describeTargets(mode.getTargets(), "it"));
        if (duration == Duration.EndOfTurn) {
            sb.append(" this turn");
        }
        sb.append(", prevent that damage");
        return sb.toString();
    }
}
