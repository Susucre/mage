package mage.abilities.effects.common;

import mage.abilities.Ability;
import mage.abilities.Mode;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.constants.Outcome;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.Target;
import mage.util.CardUtil;

import java.util.UUID;

/**
 * @author BetaSteward_at_googlemail.com
 * @author North
 */
public class DamageTargetEffect extends OneShotEffect {

    protected DynamicValue amount;
    protected boolean preventable;
    protected String targetDescription;
    protected boolean useOnlyTargetPointer; // TODO: investigate why do we ignore targetPointer by default??
    protected String sourceName = "{this}";

    public DamageTargetEffect(int amount) {
        this(StaticValue.get(amount), true);
    }

    public DamageTargetEffect(int amount, String whoDealDamageName) {
        this(StaticValue.get(amount), true);
        this.sourceName = whoDealDamageName;
    }

    public DamageTargetEffect(int amount, boolean preventable) {
        this(StaticValue.get(amount), preventable);
    }

    public DamageTargetEffect(int amount, boolean preventable, String targetDescription) {
        this(StaticValue.get(amount), preventable, targetDescription);
    }

    public DamageTargetEffect(int amount, boolean preventable, String targetDescription, boolean useOnlyTargetPointer) {
        this(StaticValue.get(amount), preventable, targetDescription, useOnlyTargetPointer);
    }

    public DamageTargetEffect(int amount, boolean preventable, String targetDescription, String whoDealDamageName) {
        this(StaticValue.get(amount), preventable, targetDescription);
        this.sourceName = whoDealDamageName;
    }

    public DamageTargetEffect(DynamicValue amount) {
        this(amount, true);
    }

    public DamageTargetEffect(DynamicValue amount, String whoDealDamageName) {
        this(amount, true);
        this.sourceName = whoDealDamageName;
    }

    public DamageTargetEffect(DynamicValue amount, boolean preventable) {
        this(amount, preventable, "");
    }

    public DamageTargetEffect(DynamicValue amount, boolean preventable, String targetDescription) {
        this(amount, preventable, targetDescription, false);
    }

    public DamageTargetEffect(DynamicValue amount, boolean preventable, String targetDescription, boolean useOnlyTargetPointer) {
        super(Outcome.Damage);
        this.amount = amount;
        this.preventable = preventable;
        this.targetDescription = targetDescription;
        this.useOnlyTargetPointer = useOnlyTargetPointer;
    }

    public int getAmount() {
        if (amount instanceof StaticValue) {
            return amount.calculate(null, null, this);
        } else {
            return 0;
        }
    }

    public void setAmount(DynamicValue amount) {
        this.amount = amount;
    }

    protected DamageTargetEffect(final DamageTargetEffect effect) {
        super(effect);
        this.amount = effect.amount.copy();
        this.preventable = effect.preventable;
        this.targetDescription = effect.targetDescription;
        this.useOnlyTargetPointer = effect.useOnlyTargetPointer;
        this.sourceName = effect.sourceName;
    }

    public DamageTargetEffect withTargetDescription(String targetDescription) {
        this.targetDescription = targetDescription;
        return this;
    }

    // TODO: this should most likely be refactored to not be needed and always use target pointer.
    public Effect setUseOnlyTargetPointer(boolean useOnlyTargetPointer) {
        this.useOnlyTargetPointer = useOnlyTargetPointer;
        return this;
    }

    @Override
    public DamageTargetEffect copy() {
        return new DamageTargetEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        if (!useOnlyTargetPointer && source.getTargets().size() > 1) {
            for (Target target : source.getTargets()) {
                for (UUID targetId : target.getTargets()) {
                    Permanent permanent = game.getPermanent(targetId);
                    if (permanent != null) {
                        permanent.damage(amount.calculate(game, source, this), source.getSourceId(), source, game, false, preventable);
                    }
                    Player player = game.getPlayer(targetId);
                    if (player != null) {
                        player.damage(amount.calculate(game, source, this), source.getSourceId(), source, game, false, preventable);
                    }
                }
            }
            return true;
        }
        for (UUID targetId : this.getTargetPointer().getTargets(game, source)) {
            Permanent permanent = game.getPermanent(targetId);
            if (permanent != null) {
                permanent.damage(amount.calculate(game, source, this), source.getSourceId(), source, game, false, preventable);
            } else {
                Player player = game.getPlayer(targetId);
                if (player != null) {
                    player.damage(amount.calculate(game, source, this), source.getSourceId(), source, game, false, preventable);
                }
            }
        }
        return true;
    }

    @Override
    public String getText(Mode mode) {
        if (staticText != null && !staticText.isEmpty()) {
            return staticText;
        }
        StringBuilder sb = new StringBuilder();
        String message = amount.getMessage();
        sb.append(this.sourceName).append(" deals ");
        if (message.isEmpty() || !message.equals("1")) {
            sb.append(amount);
        }
        if (!sb.toString().endsWith(" ")) {
            sb.append(' ');
        }
        sb.append("damage to ");
        if (!targetDescription.isEmpty()) {
            sb.append(targetDescription);
        } else {
            if (!mode.getTargets().isEmpty()) {
                Target firstTarget = mode.getTargets().get(0);
                String targetName = firstTarget.getTargetName();
                if (targetName.contains("any")) {
                    sb.append(targetName);
                } else {
                    if (firstTarget.getMinNumberOfTargets() == 0) {
                        int maxTargets = firstTarget.getMaxNumberOfTargets();
                        switch (maxTargets) {
                            case Integer.MAX_VALUE:
                                sb.append("any number of ");
                                break;
                            case 1:
                                sb.append("up to one ");
                                break;
                            default:
                                sb.append("each of up to ");
                                sb.append(CardUtil.numberToText(maxTargets));
                                sb.append(' ');
                        }
                    }
                    if (!targetName.contains("target ")) {
                        sb.append("target ");
                    }
                    sb.append(targetName);
                }
            } else {
                sb.append("that target");
            }
        }
        if (!message.isEmpty()) {
            if (message.equals("1")) {
                sb.append(" equal to the number of ");
            } else {
                if (message.startsWith("the") || message.startsWith("that") || message.startsWith("twice")) {
                    sb.append(" equal to ");
                } else {
                    sb.append(" for each ");
                }
            }
            sb.append(message);
        }
        if (!preventable) {
            sb.append(". The damage can't be prevented");
        }
        return sb.toString();
    }
}
