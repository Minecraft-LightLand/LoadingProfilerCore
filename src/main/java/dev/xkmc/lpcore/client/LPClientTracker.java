package dev.xkmc.lpcore.client;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.loadingprofiler.bootstrap.LPBootCore;
import dev.xkmc.lpcore.init.LPCore;
import dev.xkmc.lpcore.reporting.ReportWriter;
import dev.xkmc.lpcore.reporting.Summarizer;

import java.util.*;

public class LPClientTracker {

	public static final int TASK_THRES = 100, MODEL_THRES = 50;

	public static WrappedResourceManager MANAGER;
	private static ModelAnalyzer.Data models;
	private static long prevTime = 0;
	private static int bakedModelCount = 0;

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

	public static void setBakedCount(int size) {
		bakedModelCount = size;
	}

	private record Time(String name, long async, long sync) {

		public long sec() {
			return (async + sync) / 1000000000;
		}
	}

	public static void fillReport(long time, long noTask, List<WrappedResourceManager.ReloadReportEntry> ans) {
		var list = Summarizer.INS;
		list.dash();
		list.all("Reload Manager takes " + time / 1000 + " seconds, loading " + ans.size() + " tasks");
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
		list.all("Tasks are processed in parallel using multiple threads.");
		list.detail("<Task>: <main thread time> / <total time>");
		for (var e : minecraft) {
			if (e.sec() >= 10) list.summary("- %s: %d seconds".formatted(e.name, e.sec()));
			list.detail(format(e));
		}
		List<Time> aggregated = new ArrayList<>(mods.values());
		aggregated.sort(Comparator.comparingLong(e -> -(e.sync() + e.async())));
		for (var e : aggregated) {
			if (e.sec() >= 10) list.summary("- %s: %d seconds".formatted(e.name, e.sec()));
			list.detail(format(e));
		}
		list.detail("Misc " + misc + " Tasks: " + format(miscSync, miscAsync));
		if (self != null) list.detail("Model Analyzer: " + format(self.sync, self.async));
		list.detail("Main thread utilization: %.2f%%".formatted(syncTotal / 10000d / time));
		list.all("Off thread utilization: %.2f%%".formatted(asyncTotal / 10000d / time));
		list.detail("Idle time: " + noTask + " ms");
		list.dash();
		Summarizer.reportMod(false);
		list.dash();
		reportModel(list);
		ReportWriter.generate(Summarizer.build());
	}

	private static void reportModel(Summarizer list) {
		if (models == null) return;
		list.all("Model Load Time per Stage:");
		for (var e : ModelStateTracker.TIME_CHART) {
			long mil = e.getSecond() / 1000000;
			if (mil > 10000) {
				list.summary("- " + e.getFirst().text + ": " + mil / 1000 + " seconds");
			}
			list.detail("- " + e.getFirst().text + ": " + Summarizer.comma(mil) + " ms");
		}
		list.all("Found " + models.map().size() + " packs");
		list.all("Loaded " + Summarizer.comma(models.count()) + " model files with total size of " + Summarizer.comma(models.size() / 1000) + " KB");
		list.all("Baked " + bakedModelCount + " models");
		List<Pair<String, Long>> data = models.map().entrySet().stream()
				.map(e -> Pair.of(e.getKey(), e.getValue()))
				.sorted(Comparator.comparingLong(e -> -e.getSecond()))
				.toList();
		long miscSize = 0;
		int miscCount = 0;
		int thres = MODEL_THRES * 1000;
		for (var e : data) {
			long size = e.getSecond();
			if (size < thres) {
				miscSize += size;
				miscCount++;
			} else {
				list.line(size >= 1000000, "- " + e.getFirst() + ": " + Summarizer.comma(size / 1000) + " KB");
			}
		}
		list.detail("Misc " + miscCount + " packs: " + Summarizer.comma(miscSize / 1000) + " KB");
	}

	private static String format(Time time) {
		return "- " + time.name + ": " + format(time.sync, time.async);
	}

	private static String format(long sync, long async) {
		int ms = (int) (sync / 1000000);
		int ma = (int) (async / 1000000);
		int total = ms + ma;
		return Summarizer.comma(ms) + " ms / " + Summarizer.comma(total) + " ms";
	}

}
