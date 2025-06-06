package mage.watchers.common;

import mage.MageObjectReference;
import mage.constants.WatcherScope;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.watchers.Watcher;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author padfoot
 */
// copied almost entirely from BoostCountersAddedFirstTimeWatcher
public class CountersAddedFirstTimeWatcher extends Watcher {

    private final Map<MageObjectReference, UUID> map = new HashMap<>();

    public CountersAddedFirstTimeWatcher() {
        super(WatcherScope.GAME);
    }

    @Override
    public void watch(GameEvent event, Game game) {
        if (event.getType() != GameEvent.EventType.COUNTERS_ADDED) {
            return;
        }
        Permanent permanent = game.getPermanent(event.getTargetId());
        int offset = 0;
        if (permanent == null) {
            permanent = game.getPermanentEntering(event.getTargetId());
            offset++;
        }
        if (permanent != null) {
            map.putIfAbsent(new MageObjectReference(permanent, game, offset), event.getId());
        }
    }

    @Override
    public void reset() {
        super.reset();
        map.clear();
    }

    public static boolean checkEvent(GameEvent event, Permanent permanent, Game game, int offset) {
        return event
                .getId()
                .equals(game
                        .getState()
                        .getWatcher(CountersAddedFirstTimeWatcher.class)
                        .map
                        .getOrDefault(new MageObjectReference(permanent, game, offset), null));
    }
}
