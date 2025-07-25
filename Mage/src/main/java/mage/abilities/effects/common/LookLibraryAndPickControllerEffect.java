package mage.abilities.effects.common;

import mage.abilities.Ability;
import mage.abilities.Mode;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.constants.PutCards;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.players.Player;
import mage.target.TargetCard;
import mage.util.CardUtil;

/**
 * @author LevelX, awjackson
 */
public class LookLibraryAndPickControllerEffect extends LookLibraryControllerEffect {

    protected int numberToPick;
    protected PutCards putPickedCards;
    protected FilterCard filter;
    protected boolean revealPickedCards;
    protected boolean optional;
    protected boolean upTo;
    protected Effect otherwiseEffect = null;

    public LookLibraryAndPickControllerEffect(int numberOfCards, int numberToPick,
                                              PutCards putPickedCards, PutCards putLookedCards) {
        this(numberOfCards, numberToPick, putPickedCards, putLookedCards, false);
    }

    public LookLibraryAndPickControllerEffect(DynamicValue numberOfCards, int numberToPick,
                                              PutCards putPickedCards, PutCards putLookedCards) {
        this(numberOfCards, numberToPick, putPickedCards, putLookedCards, false);
    }

    public LookLibraryAndPickControllerEffect(int numberOfCards, int numberToPick,
                                              PutCards putPickedCards, PutCards putLookedCards, boolean upTo) {
        this(StaticValue.get(numberOfCards), numberToPick, putPickedCards, putLookedCards, upTo);
    }

    public LookLibraryAndPickControllerEffect(DynamicValue numberOfCards, int numberToPick,
                                              PutCards putPickedCards, PutCards putLookedCards, boolean upTo) {
        super(putPickedCards.getOutcome(), numberOfCards, putLookedCards);
        this.numberToPick = numberToPick;
        this.putPickedCards = putPickedCards;
        this.filter = (numberToPick > 1) ? StaticFilters.FILTER_CARD_CARDS : StaticFilters.FILTER_CARD_A;
        this.revealPickedCards = false;
        this.optional = false;
        this.upTo = upTo || numberToPick == Integer.MAX_VALUE;
    }

    public LookLibraryAndPickControllerEffect(int numberOfCards, int numberToPick, FilterCard filter,
                                              PutCards putPickedCards, PutCards putLookedCards) {
        this(numberOfCards, numberToPick, filter, putPickedCards, putLookedCards, true);
    }

    public LookLibraryAndPickControllerEffect(DynamicValue numberOfCards, int numberToPick, FilterCard filter,
                                              PutCards putPickedCards, PutCards putLookedCards) {
        this(numberOfCards, numberToPick, filter, putPickedCards, putLookedCards, true);
    }

    public LookLibraryAndPickControllerEffect(int numberOfCards, int numberToPick, FilterCard filter,
                                              PutCards putPickedCards, PutCards putLookedCards, boolean optional) {
        this(StaticValue.get(numberOfCards), numberToPick, filter, putPickedCards, putLookedCards, optional);
    }

    public LookLibraryAndPickControllerEffect(DynamicValue numberOfCards, int numberToPick, FilterCard filter,
                                              PutCards putPickedCards, PutCards putLookedCards, boolean optional) {
        super(putPickedCards.getOutcome(), numberOfCards, putLookedCards);
        this.numberToPick = numberToPick;
        this.putPickedCards = putPickedCards;
        this.filter = filter;
        this.revealPickedCards = !putPickedCards.getZone().isPublicZone();
        this.optional = optional;
        this.upTo = (numberToPick > 1);
    }

    protected LookLibraryAndPickControllerEffect(final LookLibraryAndPickControllerEffect effect) {
        super(effect);
        this.numberToPick = effect.numberToPick;
        this.putPickedCards = effect.putPickedCards;
        this.filter = effect.filter.copy();
        this.revealPickedCards = effect.revealPickedCards;
        this.optional = effect.optional;
        this.upTo = effect.upTo;
        this.otherwiseEffect = effect.otherwiseEffect != null ? effect.otherwiseEffect.copy() : null;
    }

    @Override
    public LookLibraryAndPickControllerEffect copy() {
        return new LookLibraryAndPickControllerEffect(this);
    }

    public LookLibraryAndPickControllerEffect withOtherwiseEffect(Effect otherwiseEffect) {
        this.otherwiseEffect = otherwiseEffect;
        return this;
    }

    @Override
    protected boolean actionWithLookedCards(Game game, Ability source, Player player, Cards cards) {
        int number = Math.min(numberToPick, cards.count(filter, source.getControllerId(), source, game));
        if (number < 1
                || optional && !player.chooseUse(putPickedCards.getOutcome(), getMayText(), source, game)) {
            return actionWithPickedCards(game, source, player, new CardsImpl(), cards);
        }
        TargetCard target = new TargetCard((upTo ? 0 : number), number, Zone.LIBRARY, filter);
        target.withChooseHint(getChooseHint());
        if (!player.chooseTarget(putPickedCards.getOutcome(), cards, target, source, game)) {
            return actionWithPickedCards(game, source, player, new CardsImpl(), cards);
        }
        Cards pickedCards = new CardsImpl(target.getTargets());
        if (revealPickedCards) {
            player.revealCards(source, pickedCards, game);
        }
        cards.removeAll(pickedCards);
        return actionWithPickedCards(game, source, player, pickedCards, cards);
    }

    /**
     * Can be overriden for additional effect
     */
    protected boolean actionWithPickedCards(Game game, Ability source, Player player, Cards pickedCards, Cards otherCards) {
        boolean result = putPickedCards.moveCards(player, pickedCards, source, game);
        result |= putLookedCards.moveCards(player, otherCards, source, game);
        if (!pickedCards.isEmpty() || otherwiseEffect == null) {
            return result;
        }
        game.processAction();
        if (otherwiseEffect instanceof OneShotEffect) {
            otherwiseEffect.apply(game, source);
        } else if (otherwiseEffect instanceof ContinuousEffect) {
            game.addEffect((ContinuousEffect) otherwiseEffect, source);
        }
        return result;
    }

    protected String getMayText() {
        boolean plural = numberToPick > 1;
        StringBuilder sb = new StringBuilder(revealPickedCards ? "Reveal " : "Put ");
        sb.append(plural ? filter.getMessage() : CardUtil.addArticle(filter.getMessage()));
        if (revealPickedCards) {
            sb.append(" and put ");
            sb.append(plural ? "them" : "it");
        }
        sb.append(" ");
        sb.append(putPickedCards.getMessage(false, plural));
        return sb.append("?").toString();
    }

    protected String getChooseHint() {
        return "to put " + putPickedCards.getMessage(false, numberToPick > 1);
    }

    @Override
    public String getText(Mode mode) {
        if (staticText != null && !staticText.isEmpty()) {
            return staticText;
        }
        StringBuilder sb = new StringBuilder(". ");
        if (optional) {
            sb.append(revealPickedCards ? "You may reveal " : "You may put ");
        } else {
            sb.append(revealPickedCards ? "Reveal " : "Put ");
        }
        boolean havePredicates = filter.hasPredicates();
        boolean plural = numberToPick > 1;
        if (havePredicates && !plural && !upTo) {
            sb.append(CardUtil.addArticle(filter.getMessage()));
        } else if (numberToPick == Integer.MAX_VALUE) {
            sb.append("any number of ");
            if (havePredicates) {
                sb.append(filter.getMessage());
            }
        } else {
            if (upTo) {
                sb.append("up to ");
            }
            sb.append(CardUtil.numberToText(numberToPick));
            sb.append(" ");
            sb.append(havePredicates ? filter.getMessage() : "of ");
        }
        if (havePredicates) {
            sb.append(" from among ");
        }
        sb.append("them ");
        if (revealPickedCards) {
            sb.append("and put ");
            sb.append(plural ? "them " : "it ");
        } else if (putPickedCards == PutCards.TOP_ANY && (numberOfCards instanceof StaticValue)) {
            sb.append("back ");
        }
        sb.append(putPickedCards.getMessage(false, plural));

        plural = optional
                || upTo
                || !(numberOfCards instanceof StaticValue)
                || numberOfCards.calculate(null, null, this) - numberToPick != 1;

        // if remaining text would be "put the other on top of your library", omit it
        if (!plural && putLookedCards == PutCards.TOP_ANY) {
            return setText(mode, sb.toString());
        }
        sb.append(havePredicates && (optional || upTo) ? ". Put" : " and");
        sb.append(" the ");
        sb.append(plural ? "rest " : "other ");
        if (putPickedCards == PutCards.GRAVEYARD && putLookedCards == PutCards.TOP_ANY) {
            sb.append("back ");
        }
        sb.append(putLookedCards.getMessage(false, plural));
        if (otherwiseEffect != null) {
            sb.append(". If you didn't put a card ");
            sb.append(putPickedCards.getMessage(false, false));
            sb.append(" this way, ");
            sb.append(otherwiseEffect.getText(mode));
        }

        // get text frame from super class and inject action text
        return setText(mode, sb.toString());
    }
}
