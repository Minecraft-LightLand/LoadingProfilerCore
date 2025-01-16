package dev.xkmc.lpcore.client;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.loadingprofiler.bootstrap.LPBootCore;
import dev.xkmc.lpcore.init.LPCore;
import dev.xkmc.lpcore.init.LPEarly;
import dev.xkmc.lpcore.reporting.ReportWriter;

import java.util.*;

public class LPClientTracker {

	public static final int TASK_THRES = 100, MODEL_THRES = 50;

	public static WrappedResourceManager MANAGER;
	private static ModelAnalyzer.Data models;
	private static long prevTime = 0;

	public static void tickReload(long millis) {
		if (millis > prevTime + 50) {
			long diff = millis - prevTime;
			if (diff > 200 && prevTime > 0) {
				LPCore.LOGGER.warn("Loading Overlay interval of {} ms", diff);
			}
			prevTime = millis;
			var meter = LPBootCore.meter();
			if (MANAGER != null && meter != null) {
				String info = MANAGER.getRunning();
				meter.label(info);
			}
		}
		if (MANAGER != null && MANAGER.instance != null && MANAGER.instance.isDone()) {
			MANAGER.onStop();
			MANAGER = null;
		}

	}

	public static void set(WrappedResourceManager manager) {
		MANAGER = manager;
	}

	public static void putModelData(ModelAnalyzer.Data data) {
		models = data;
	}

	private record Time(String name, long async, long sync) {

	}

	public static void fillReport(long time, long noTask, List<WrappedResourceManager.ReloadReportEntry> ans) {
		var list = LPEarly.LIST;
		list.add("--------------------");
		list.add("Reload Manager takes " + time / 1000 + " seconds, loading " + ans.size() + " tasks");
		List<Time> minecraft = new ArrayList<>();
		Map<String, Time> mods = new LinkedHashMap<>();
		long syncTotal = 0, asyncTotal = 0;
		long miscSync = 0, miscAsync = 0;
		int misc = 0;
		Time self = null;
		int thres = TASK_THRES * 1000000;
		for (var e : ans) {
			if (e.module().equals("lpcore")) {
				self = new Time(e.name(), e.async(), e.sync());
			} else if (e.sync() < thres && e.async() < thres) {
				miscSync += e.sync();
				miscAsync += e.async();
				misc++;
			} else if (e.module().equals("minecraft") || e.module().equals("forge")) {
				minecraft.add(new Time(e.name(), e.async(), e.sync()));
			} else {
				mods.compute(e.module(), (k, v) -> v == null ? new Time(k, e.async(), e.sync()) :
						new Time(k, v.async + e.async(), v.sync + e.sync()));
			}
			syncTotal += e.sync();
			asyncTotal += e.async();
		}
		minecraft.sort(Comparator.comparingLong(e -> -(e.sync() + e.async())));
		list.add("<Task>: <main thread time> / <total time>");
		for (var e : minecraft) {
			list.add(format(e));
		}
		List<Time> aggregated = new ArrayList<>(mods.values());
		aggregated.sort(Comparator.comparingLong(e -> -(e.sync() + e.async())));
		for (var e : aggregated) {
			list.add(format(e));
		}
		list.add("Misc " + misc + " Tasks: " + format(miscSync, miscAsync));
		if (self != null) list.add("Model Analyzer: " + format(self.sync, self.async));
		list.add("Main thread utilization: %.2f%%".formatted(syncTotal / 10000d / time));
		list.add("Off thread utilization: %.2f%%".formatted(asyncTotal / 10000d / time));
		list.add("Idle time: " + noTask + " ms");
		list.add("--------------------");
		reportModel(list);
		ReportWriter.generate(list);
	}

	private static void reportModel(List<String> list) {
		if (models == null) return;
		list.add("Model Load Time per Stage:");
		for (var e : ModelStateTracker.TIME_CHART) {
			list.add("- " + e.getFirst().text + ": " + LPEarly.comma(e.getSecond() / 1000000) + " ms");
		}
		list.add("Found " + models.map().size() + " packs");
		list.add("Loaded " + LPEarly.comma(models.count()) + " models with total size of " + LPEarly.comma(models.size() / 1000) + " KB");
		List<Pair<String, Long>> data = models.map().entrySet().stream()
				.map(e -> Pair.of(e.getKey(), e.getValue()))
				.sorted(Comparator.comparingLong(e -> -e.getSecond()))
				.toList();
		long miscSize = 0;
		int miscCount = 0;
		int thres = MODEL_THRES * 1000;
		for (var e : data) {
			if (e.getSecond() < thres) {
				miscSize += e.getSecond();
				miscCount++;
			} else {
				list.add("- " + e.getFirst() + ": " + LPEarly.comma(e.getSecond() / 1000) + " KB");
			}
		}
		list.add("Misc " + miscCount + " packs: " + LPEarly.comma(miscSize / 1000) + " KB");
	}

	private static String format(Time time) {
		return "- " + time.name + ": " + format(time.sync, time.async);
	}

	private static String format(long sync, long async) {
		int ms = (int) (sync / 1000000);
		int ma = (int) (async / 1000000);
		int total = ms + ma;
		return LPEarly.comma(ms) + " ms / " + LPEarly.comma(total) + " ms";
	}

}
