package mage.game.stack;

import mage.MageIdentifier;
import mage.MageInt;
import mage.MageObject;
import mage.ObjectColor;
import mage.abilities.*;
import mage.abilities.costs.Cost;
import mage.abilities.costs.CostAdjuster;
import mage.abilities.costs.Costs;
import mage.abilities.costs.CostsImpl;
import mage.abilities.costs.mana.ManaCost;
import mage.abilities.costs.mana.ManaCosts;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.Effect;
import mage.abilities.effects.Effects;
import mage.abilities.hint.Hint;
import mage.abilities.icon.CardIcon;
import mage.cards.Card;
import mage.cards.FrameStyle;
import mage.constants.*;
import mage.filter.predicate.mageobject.MageObjectReferencePredicate;
import mage.game.Game;
import mage.game.events.CopiedStackObjectEvent;
import mage.game.events.GameEvent;
import mage.game.events.ZoneChangeEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.Target;
import mage.target.Targets;
import mage.target.targetadjustment.TargetAdjuster;
import mage.util.GameLog;
import mage.util.SubTypes;
import mage.util.functions.StackObjectCopyApplier;
import mage.watchers.Watcher;

import java.util.*;

/**
 * @author BetaSteward_at_googlemail.com
 */
public class StackAbility extends StackObjectImpl implements Ability {

    private static final List<CardType> emptyCardType = Collections.unmodifiableList(new ArrayList<>());
    private static final List<String> emptyString = Collections.unmodifiableList(new ArrayList<>());
    private static final ManaCosts<ManaCost> emptyCost = new ManaCostsImpl<>();
    private static final Costs<Cost> emptyCosts = new CostsImpl<>();
    private static final Abilities<Ability> emptyAbilites = new AbilitiesImpl<>();

    private final Ability ability;
    private UUID controllerId;
    private boolean copy;
    private MageObject copyFrom; // copied card INFO (used to call original adjusters)
    private String name;
    private TargetAdjuster targetAdjuster = null;
    private CostAdjuster costAdjuster = null;

    public StackAbility(Ability ability, UUID controllerId) {
        super();
        this.ability = ability;
        this.controllerId = controllerId;
        this.name = "stack ability (" + ability.getRule() + ')';
    }

    protected StackAbility(final StackAbility stackAbility) {
        super();
        this.ability = stackAbility.ability.copy();
        this.controllerId = stackAbility.controllerId;
        this.copy = stackAbility.copy;
        this.copyFrom = (stackAbility.copyFrom != null ? stackAbility.copyFrom.copy() : null);
        this.name = stackAbility.name;
        this.targetAdjuster = stackAbility.targetAdjuster;
        this.targetChanged = stackAbility.targetChanged;
        this.costAdjuster = stackAbility.costAdjuster;
    }

    @Override
    public boolean isActivated() {
        return ability.isActivated();
    }

    @Override
    public boolean resolve(Game game) {
        if (ability.getTargets().stillLegal(ability, game) || !canFizzle()) {
            boolean result = ability.resolve(game);
            game.getStack().remove(this, game);
            return result;
        }
        if (!game.isSimulation()) {
            game.informPlayers("Ability has been fizzled: " + getRule());
        }
        counter(null, /*this*/ game);
        game.getStack().remove(this, game);
        return false;
    }

    @Override
    public void reset(Game game) {
    }

    @Override
    public void counter(Ability source, Game game) {
        // zone ignored
        this.counter(source, game, PutCards.GRAVEYARD);
    }

    @Override
    public void counter(Ability source, Game game, PutCards putCard) {
        //20100716 - 603.8
        if (ability instanceof StateTriggeredAbility) {
            ((StateTriggeredAbility) ability).counter(game);
        }
    }

    @Override
    public void setCopy(boolean isCopy, MageObject copyFrom) {
        this.copy = isCopy;
        this.copyFrom = (copyFrom != null ? copyFrom.copy() : null);
    }

    @Override
    public boolean isCopy() {
        return this.copy;
    }

    @Override
    public MageObject getCopyFrom() {
        return this.copyFrom;
    }

    @Override
    public String getExpansionSetCode() {
        return "";
    }

    @Override
    public void setExpansionSetCode(String expansionSetCode) {
        throw new IllegalStateException("Wrong code usage: you can't change set code for the stack ability");
    }

    @Override
    public boolean getUsesVariousArt() {
        return false;
    }

    @Override
    public void setUsesVariousArt(boolean usesVariousArt) {
        throw new IllegalStateException("Wrong code usage: you can't change usesVariousArt for the stack ability");
    }

    @Override
    public String getCardNumber() {
        return "";
    }

    @Override
    public void setCardNumber(String cardNumber) {
        throw new IllegalStateException("Wrong code usage: you can't change card number for the stack ability");
    }

    @Override
    public String getImageFileName() {
        return "";
    }

    @Override
    public void setImageFileName(String imageFile) {
        throw new IllegalStateException("Wrong code usage: you can't change image file name for the stack ability");
    }

    @Override
    public Integer getImageNumber() {
        return 0;
    }

    @Override
    public void setImageNumber(Integer imageNumber) {
        throw new IllegalStateException("Wrong code usage: you can't change image number for the stack ability");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIdName() {
        return getName() + " [" + getId().toString().substring(0, 3) + ']';
    }

    @Override
    public String getLogName() {
        return GameLog.getColoredObjectIdName(this);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<CardType> getCardType(Game game) {
        return emptyCardType;
    }

    @Override
    public SubTypes getSubtype() {
        return new SubTypes();
    }

    @Override
    public SubTypes getSubtype(Game game) {
        return new SubTypes();
    }

    @Override
    public boolean hasSubtype(SubType subtype, Game game) {
        return false;
    }

    @Override
    public List<SuperType> getSuperType(Game game) {
        return Collections.emptyList();
    }

    @Override
    public Abilities<Ability> getAbilities() {
        return new AbilitiesImpl<>(ability);
    }

    @Override
    public Abilities<Ability> getInitAbilities() {
        return new AbilitiesImpl<>();
    }

    @Override
    public boolean hasAbility(Ability ability, Game game) {
        return false;
    }

    @Override
    public ObjectColor getColor() {
        return ObjectColor.COLORLESS;
    }

    @Override
    public ObjectColor getColor(Game game) {
        return ObjectColor.COLORLESS;
    }

    @Override
    public ObjectColor getFrameColor(Game game) {
        return ability.getSourceObject(game).getFrameColor(game);
    }

    @Override
    public FrameStyle getFrameStyle() {
        // Abilities all use the same frame
        return FrameStyle.M15_NORMAL;
    }

    @Override
    public ManaCosts<ManaCost> getManaCost() {
        return emptyCost;
    }

    @Override
    public void setManaCost(ManaCosts<ManaCost> costs) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public List<String> getManaCostSymbols() {
        return super.getManaCostSymbols();
    }

    @Override
    public MageInt getPower() {
        return MageInt.EmptyMageInt;
    }

    @Override
    public MageInt getToughness() {
        return MageInt.EmptyMageInt;
    }

    @Override
    public int getStartingLoyalty() {
        return 0;
    }

    @Override
    public void setStartingLoyalty(int startingLoyalty) {
    }

    @Override
    public int getStartingDefense() {
        return 0;
    }

    @Override
    public void setStartingDefense(int startingDefense) {
    }

    @Override
    public Zone getZone() {
        return this.ability.getZone();
    }

    @Override
    public UUID getId() {
        return this.ability.getId();
    }

    @Override
    public UUID getSourceId() {
        return this.ability.getSourceId();
    }

    @Override
    public UUID getControllerId() {
        return this.controllerId;
    }

    @Override
    public UUID getControllerOrOwnerId() {
        return getControllerId();
    }

    @Override
    public Costs<Cost> getCosts() {
        return emptyCosts;
    }

    @Override
    public int getManaValue() {
        // Activated abilities have an "activation cost" but they don't have a characteristic related to that while on the stack.
        // There are certain effects that interact with the cost to activate an ability (e.g., Training Grounds, Power Artifact)
        // but nothing that looks for that quality of an ability once it's on the stack.
        return 0;
    }

    @Override
    public Effects getEffects() {
        return ability.getEffects();
    }

    @Override
    public Effects getAllEffects() {
        return ability.getAllEffects();
    }

    @Override
    public Effects getEffects(Game game, EffectType effectType) {
        return ability.getEffects(game, effectType);
    }

    @Override
    public String getRule() {
        return ability.getRule();
    }

    @Override
    public String getRule(boolean all) {
        return ability.getRule(all);
    }

    @Override
    public String getRule(String source) {
        return ability.getRule(source);
    }

    @Override
    public void setControllerId(UUID controllerId) {
        this.controllerId = controllerId;
    }

    @Override
    public void setSourceId(UUID sourceID) {
    }

    @Override
    public void addCost(Cost cost) {
    }

    @Override
    public void addEffect(Effect effect) {
    }

    @Override
    public boolean activate(Game game, Set<MageIdentifier> allowedIdentifiers, boolean noMana) {
        return ability.activate(game, allowedIdentifiers, noMana);
    }

    @Override
    public Targets getTargets() {
        return ability.getTargets();
    }

    @Override
    public Targets getAllSelectedTargets() {
        return ability.getAllSelectedTargets();
    }

    @Override
    public void addTarget(Target target) {
    }

    @Override
    public UUID getFirstTarget() {
        return ability.getFirstTarget();
    }

    @Override
    public ManaCosts<ManaCost> getManaCosts() {
        return ability.getManaCosts();
    }

    @Override
    public ManaCosts<ManaCost> getManaCostsToPay() {
        return ability.getManaCostsToPay();
    }

    @Override
    public void addManaCostsToPay(ManaCost manaCost) {
        // Do nothing
    }

    @Override
    public void setVariableCostsMinMax(int min, int max) {
        ability.setVariableCostsMinMax(min, max);
    }

    @Override
    public void setVariableCostsValue(int xValue) {
        ability.setVariableCostsValue(xValue);
    }

    @Override
    public Map<String, Object> getCostsTagMap() {
        return ability.getCostsTagMap();
    }

    @Override
    public void setCostsTag(String tag, Object value) {
        ability.setCostsTag(tag, value);
    }

    @Override
    public AbilityType getAbilityType() {
        return ability.getAbilityType();
    }

    @Override
    public boolean isActivatedAbility() {
        return ability.isActivatedAbility();
    }

    @Override
    public boolean isTriggeredAbility() {
        return ability.isTriggeredAbility();
    }

    @Override
    public boolean isNonManaActivatedAbility() {
        return ability.isNonManaActivatedAbility();
    }

    @Override
    public boolean isManaActivatedAbility() {
        return ability.isManaActivatedAbility();
    }

    @Override
    public boolean isManaAbility() {
        return ability.isManaAbility();
    }

    @Override
    public boolean isUsesStack() {
        return true;
    }

    @Override
    public StackAbility copy() {
        return new StackAbility(this);
    }

    @Override
    public boolean checkIfClause(Game game) {
        return true;
    }

    @Override
    public void newId() {
        if (!(this instanceof MageSingleton)) {
            this.ability.newId();
        }
    }

    @Override
    public void newOriginalId() {
    }

    @Override
    public Ability getStackAbility() {
        return ability;
    }

    @Override
    public boolean isModal() {
        return ability.isModal();
    }

    @Override
    public void addMode(Mode mode) {
    }

    @Override
    public Modes getModes() {
        return ability.getModes();
    }

    @Override
    public boolean canChooseTarget(Game game, UUID playerId) {
        return ability.canChooseTarget(game, playerId);
    }

    @Override
    public boolean isInUseableZone(Game game, MageObject sourceObject, GameEvent event) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean hasSourceObjectAbility(Game game, MageObject sourceObject, GameEvent event) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean getRuleAtTheTop() {
        return this.ability.getRuleAtTheTop();
    }

    @Override
    public Ability setRuleAtTheTop(boolean ruleAtTheTop) {
        this.ability.setRuleAtTheTop(ruleAtTheTop);
        return this;
    }

    @Override
    public boolean getRuleVisible() {
        return this.ability.getRuleVisible();
    }

    @Override
    public Ability setRuleVisible(boolean ruleVisible) {
        this.ability.setRuleVisible(ruleVisible);
        return this;
    }

    @Override
    public boolean getAdditionalCostsRuleVisible() {
        return this.ability.getAdditionalCostsRuleVisible();
    }

    @Override
    public void setAdditionalCostsRuleVisible(boolean ruleAdditionalCostsVisible) {
        this.ability.setAdditionalCostsRuleVisible(ruleAdditionalCostsVisible);
    }

    @Override
    public UUID getOriginalId() {
        return this.ability.getOriginalId();
    }

    @Override
    public Ability setAbilityWord(AbilityWord abilityWord) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Ability withFlavorWord(String flavorWord) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String addRulePrefix(String rule) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Ability withFirstModeFlavorWord(String flavorWord) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Ability withFirstModeCost(Cost cost) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean activateAlternateOrAdditionalCosts(MageObject sourceObject, Set<MageIdentifier> allowedIdentifiers, boolean noMana, Player controller, Game game) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getGameLogMessage(Game game) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean getWorksFaceDown() {
        return this.ability.getWorksFaceDown();
    }

    @Override
    public void setWorksFaceDown(boolean worksFaceDown) {
        this.ability.setWorksFaceDown(worksFaceDown);
    }

    @Override
    public boolean getWorksPhasedOut() {
        return this.ability.getWorksPhasedOut();
    }

    @Override
    public void setWorksPhasedOut(boolean worksPhasedOut) {
        this.ability.setWorksPhasedOut(worksPhasedOut);
    }

    @Override
    public List<Watcher> getWatchers() {
        return this.ability.getWatchers();
    }

    @Override
    public void addWatcher(Watcher watcher) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public List<Ability> getSubAbilities() {
        return this.ability.getSubAbilities();
    }

    @Override
    public void addSubAbility(Ability ability) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public MageObject getSourceObject(Game game) {
        return this.ability.getSourceObject(game);
    }

    @Override
    public MageObject getSourceObjectIfItStillExists(Game game) {
        return this.ability.getSourceObjectIfItStillExists(game);
    }

    @Override
    public Card getSourceCardIfItStillExists(Game game) {
        return this.ability.getSourceCardIfItStillExists(game);
    }

    @Override
    public Permanent getSourcePermanentIfItStillExists(Game game) {
        return this.ability.getSourcePermanentIfItStillExists(game);
    }

    @Override
    public void setSourceObjectZoneChangeCounter(int zoneChangeCounter) {
        ability.setSourceObjectZoneChangeCounter(zoneChangeCounter);
    }

    @Override
    public void initSourceObjectZoneChangeCounter(Game game, boolean force) {
        ability.initSourceObjectZoneChangeCounter(game, force);
    }
    @Override
    public int getSourceObjectZoneChangeCounter() {
        return ability.getSourceObjectZoneChangeCounter();
    }

    @Override
    public Permanent getSourcePermanentOrLKI(Game game) {
        return ability.getSourcePermanentOrLKI(game);
    }

    @Override
    public void setSourcePermanentTransformCount(Game game) {
        ability.setSourcePermanentTransformCount(game);
    }

    @Override
    public boolean checkTransformCount(Permanent permanent, Game game) {
        return ability.checkTransformCount(permanent, game);
    }

    @Override
    public int getZoneChangeCounter(Game game) {
        return game.getState().getZoneChangeCounter(getSourceId());
    }

    @Override
    public void updateZoneChangeCounter(Game game, ZoneChangeEvent event) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setZoneChangeCounter(int value, Game game) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getTargetDescription(Targets targets, Game game) {
        return getAbilities().get(0).getTargetDescription(targets, game);
    }

    @Override
    public boolean canFizzle() {
        return ability.canFizzle();
    }

    @Override
    public void setCanFizzle(boolean canFizzle) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean canBeCopied() {
        return ability.canBeCopied();
    }

    @Override
    public Ability withCanBeCopied(boolean canBeCopied) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void createSingleCopy(UUID newControllerId, StackObjectCopyApplier applier, MageObjectReferencePredicate newTargetFilterPredicate, Game game, Ability source, boolean chooseNewTargets) {
        Ability newAbility = this.ability.copy();
        newAbility.newId();
        newAbility.setControllerId(newControllerId);

        StackAbility newStackAbility = new StackAbility(newAbility, newControllerId);
        game.getStack().push(newStackAbility);

        // new targets
        if (newTargetFilterPredicate != null) {
            newStackAbility.chooseNewTargets(game, newControllerId, true, false, newTargetFilterPredicate);
        } else if (chooseNewTargets || applier != null) { // if applier is non-null but predicate is null then it's extra
            newStackAbility.chooseNewTargets(game, newControllerId);
        }

        game.fireEvent(new CopiedStackObjectEvent(this, newStackAbility, newControllerId));
    }

    @Override
    public boolean isAllCreatureTypes(Game game) {
        return false;
    }

    @Override
    public void setIsAllCreatureTypes(boolean value) {
    }

    @Override
    public void setIsAllCreatureTypes(Game game, boolean value) {
    }

    @Override
    public boolean isAllNonbasicLandTypes(Game game) {
        return false;
    }

    @Override
    public void setIsAllNonbasicLandTypes(boolean value) {
    }

    @Override
    public void setIsAllNonbasicLandTypes(Game game, boolean value) {
    }

    @Override
    public StackAbility setTargetAdjuster(TargetAdjuster targetAdjuster) {
        this.targetAdjuster = targetAdjuster;
        return this;
    }

    @Override
    public TargetAdjuster getTargetAdjuster() {
        return targetAdjuster;
    }

    @Override
    public void adjustTargets(Game game) {
        if (targetAdjuster != null) {
            targetAdjuster.adjustTargets(this, game);
        }
    }

    @Override
    public StackAbility setCostAdjuster(CostAdjuster costAdjuster) {
        this.costAdjuster = costAdjuster;
        return this;
    }

    @Override
    public CostAdjuster getCostAdjuster() {
        return costAdjuster;
    }

    @Override
    public void adjustX(Game game) {
        if (costAdjuster != null) {
            costAdjuster.prepareX(this, game);
        }
    }

    @Override
    public void adjustCostsPrepare(Game game) {
        if (costAdjuster != null) {
            costAdjuster.prepareCost(this, game);
        }
    }

    @Override
    public void adjustCostsModify(Game game, CostModificationType costModificationType) {
        if (costAdjuster != null) {
            costAdjuster.modifyCost(this, game, costModificationType);
        }
    }

    @Override
    public List<Hint> getHints() {
        return this.ability.getHints();
    }

    @Override
    public Ability addHint(Hint hint) {
        throw new IllegalArgumentException("Stack ability is not supports hint adding");
    }

    @Override
    public void setModeTag(String tag) {
        throw new IllegalArgumentException("Stack ability does not supports setting modeTag");
    }

    @Override
    public List<CardIcon> getIcons() {
        return this.ability.getIcons();
    }

    @Override
    public List<CardIcon> getIcons(Game game) {
        return this.ability.getIcons(game);
    }

    @Override
    public Ability addIcon(CardIcon cardIcon) {
        throw new IllegalArgumentException("Stack ability is not supports icon adding");
    }

    @Override
    public Ability addCustomOutcome(Outcome customOutcome) {
        throw new IllegalArgumentException("Stack ability is not supports custom outcome adding");
    }

    @Override
    public Outcome getCustomOutcome() {
        return this.ability.getCustomOutcome();
    }

    @Override
    public boolean isSameInstance(Ability ability) {
        // same instance (by mtg rules) = same object, ID or class+text (you can't check class only cause it can be different by params/text)
        if (ability == null) {
            return false;
        }

        return (this == ability)
                || (this.getId().equals(ability.getId()))
                || (this.getOriginalId().equals(ability.getOriginalId()))
                || (this.getClass() == ability.getClass() && this.getRule().equals(ability.getRule()));
    }

    @Override
    public MageIdentifier getIdentifier() {
        return ability.getIdentifier();
    }

    @Override
    public AbilityImpl setIdentifier(MageIdentifier identifier) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String toString() {
        return this.name;
    }
}
