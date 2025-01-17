package dev.xkmc.lpcore.logdelegate;

import dev.xkmc.loadingprofiler.bootstrap.ILoadingStage;
import dev.xkmc.loadingprofiler.bootstrap.LPBootCore;
import dev.xkmc.lpcore.init.LPEarly;
import net.minecraftforge.eventbus.api.Event;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class ModTracker {

	private static final Map<String, ModTracker> MAP = new ConcurrentHashMap<>();
	private static final AtomicLong GATHER_TIME = new AtomicLong(), RELOAD_TIME = new AtomicLong();
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

	public static void startEvent(String id, Event event) {
		MAP.get(id).start(event);
	}

	public static void finishEvent(String id, Event event) {
		MAP.get(id).stop(event);
	}

	private long startTime, constructTime;
	private final AtomicLong totalTime = new AtomicLong();
	private final ConcurrentMap<Event, Long> eventStartTime = new ConcurrentHashMap<>();
	private final ConcurrentMap<Event, Long> eventTotalTime = new ConcurrentHashMap<>();

	private ModTracker() {
		start();
	}

	private void start() {
		if (startTime > 0) LPBootCore.LOGGER.throwing(new IllegalStateException("Wrong mod state order"));
		startTime = System.nanoTime();
	}

	private void stop() {
		if (startTime == 0) LPBootCore.LOGGER.throwing(new IllegalStateException("Wrong mod state order"));
		constructTime = System.nanoTime() - startTime;
		if (constructTime < 0) {
			LPBootCore.LOGGER.throwing(new IllegalStateException("Negative time for construct"));
		} else {
			GLOBAL.addAndGet(constructTime);
			totalTime.addAndGet(constructTime);
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

	public static void getReport(List<String> list) {
		int modDisplayThres = 500;
		int modDetailThres = 1000;
		int detailDisplayThres = 100;

		for (var mod : MAP.entrySet()) {
			for (var event : mod.getValue().eventStartTime.keySet()) {
				LPBootCore.LOGGER.error("Unfinished event recording for {} - {}", mod.getKey(), LPEarly.parseClassName(event.getClass()));
			}
		}
		list.add("Mods use %d seconds to initialize and %d seconds to load".formatted(GATHER_TIME.get() / 1000000000L, RELOAD_TIME.get() / 1000000000));
		list.add("Mod loading time:");
		var mods = MAP.entrySet().stream().sorted(Comparator.comparingLong(e -> -e.getValue().totalTime.get())).toList();
		long misc = 0;
		int miscCount = 0;
		for (var ent : mods) {
			long totalTime = ent.getValue().totalTime.get();
			long mil = totalTime / 1000000;
			if (mil < modDisplayThres) {
				misc += totalTime;
				miscCount++;
			} else {
				list.add("- " + ent.getKey() + ": " + LPEarly.comma(mil) + " ms");
				if (mil >= modDetailThres) {
					var mod = ent.getValue();
					long construct = mod.constructTime / 1000000;
					if (construct >= detailDisplayThres) {
						list.add("  | - Construct: " + LPEarly.comma(construct) + " ms");
					}
					var events = mod.eventTotalTime.entrySet().stream().sorted(Comparator.comparingLong(e -> -e.getValue())).toList();
					long other = 0;
					for (var eve : events) {
						long time = eve.getValue() / 1000000;
						if (time < detailDisplayThres) {
							other += eve.getValue();
						} else {
							String name = LPEarly.parseClassName(eve.getKey().getClass());
							list.add("  | - " + name + ": " + LPEarly.comma(time) + " ms");
						}
					}
					list.add("  | - Others: " + LPEarly.comma(other / 1000000) + " ms");
				}
			}
		}
		list.add("Misc " + miscCount + " mods: " + LPEarly.comma(misc / 1000000) + " ms");
	}

}
