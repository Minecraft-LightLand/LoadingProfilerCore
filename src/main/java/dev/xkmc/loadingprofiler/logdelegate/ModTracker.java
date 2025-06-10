package dev.xkmc.loadingprofiler.logdelegate;

import dev.xkmc.loadingprofiler.bootstrap.ILoadingStage;
import dev.xkmc.loadingprofiler.bootstrap.LPBootCore;
import dev.xkmc.loadingprofiler.init.LPCore;
import dev.xkmc.loadingprofiler.init.LPEarly;
import net.neoforged.bus.api.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class ModTracker {

	public static final Map<String, ModTracker> MAP = new ConcurrentHashMap<>();
	public static final AtomicLong GATHER_TIME = new AtomicLong(), RELOAD_TIME = new AtomicLong();
	private static AtomicLong GLOBAL = GATHER_TIME;

	public static void switchTracker(ILoadingStage stage) {
		GLOBAL = RELOAD_TIME;
	}

	public static synchronized void startConstruct(String id) {
		MAP.put(id, new ModTracker());
	}

	public static synchronized void finishConstruct(String id) {
		var mod = MAP.get(id);
		if (mod == null) {
			LPCore.LOGGER.error("Mod {} skipped construction timing", id);
			MAP.put(id, mod = new ModTracker());
		}
		mod.stop();
	}

	public static void startEvent(String id, Event event) {
		var mod = MAP.get(id);
		if (mod == null) {
			LPCore.LOGGER.error("Mod {} skipped construction entirely", id);
			MAP.put(id, mod = new ModTracker());
			mod.stop();
		}
		mod.start(event);
	}

	public static void finishEvent(String id, Event event) {
		var mod = MAP.get(id);
		if (mod == null) {
			return;
		}
		mod.stop(event);
	}

	public long startTime, constructTime;
	public final AtomicLong totalTime = new AtomicLong();
	public final ConcurrentMap<Event, Long> eventStartTime = new ConcurrentHashMap<>();
	public final ConcurrentMap<Event, Long> eventTotalTime = new ConcurrentHashMap<>();

	private ModTracker() {
		start();
	}

	private void start() {
		if (startTime > 0) LPBootCore.LOGGER.throwing(new IllegalStateException("Wrong mod state order"));
		startTime = System.nanoTime();
	}

	private void stop() {
		if (startTime == 0) LPBootCore.LOGGER.throwing(new IllegalStateException("Wrong mod state order"));
		long diff = System.nanoTime() - startTime;
		if (diff < 0) {
			LPBootCore.LOGGER.throwing(new IllegalStateException("Negative time for construct"));
		} else {
			constructTime += diff;
			GLOBAL.addAndGet(diff);
			totalTime.addAndGet(diff);
			startTime = 0;
		}
	}

	private void start(Event event) {
		eventStartTime.put(event, System.nanoTime());
	}

	private void stop(Event event) {
		long time = eventStartTime.remove(event);
		long diff = System.nanoTime() - time;
		if (diff < 0) {
			LPBootCore.LOGGER.throwing(new IllegalStateException("Negative time for event " + LPEarly.parseClassName(event.getClass())));
		} else {
			GLOBAL.addAndGet(diff);
			totalTime.addAndGet(diff);
			eventTotalTime.put(event, diff);
			eventStartTime.remove(event);
		}
	}

}
