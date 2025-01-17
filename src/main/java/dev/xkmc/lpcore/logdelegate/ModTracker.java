package dev.xkmc.lpcore.logdelegate;

import dev.xkmc.loadingprofiler.bootstrap.ILoadingStage;
import dev.xkmc.loadingprofiler.bootstrap.LPBootCore;
import dev.xkmc.lpcore.init.LPEarly;
import net.minecraftforge.eventbus.api.Event;

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

	public static void startCreate(String clsName) {
	}

	public static void finishCreate(String clsname) {
	}

	public static void startConstruct(String id) {
		MAP.put(id, new ModTracker());
	}

	public static void finishConstruct(String id) {
		MAP.get(id).stop();
	}

	public static void startSubscribe(String id) {
		MAP.get(id).start();
	}

	public static void finishSubscribe(String id) {
		MAP.get(id).stop();
	}

	public static void startEvent(String id, Event event) {
		MAP.get(id).start(event);
	}

	public static void finishEvent(String id, Event event) {
		MAP.get(id).stop(event);
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
