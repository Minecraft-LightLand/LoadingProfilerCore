package dev.xkmc.loadingprofiler.reporting;

import dev.xkmc.loadingprofiler.bootstrap.LPBootCore;
import dev.xkmc.loadingprofiler.bootstrap.LoadingStageGroup;
import dev.xkmc.loadingprofiler.init.LPEarly;
import dev.xkmc.loadingprofiler.logdelegate.ModTracker;

import java.text.DecimalFormat;
import java.util.*;

public class Summarizer {

	public static final Summarizer INS = new Summarizer();

	private static final DecimalFormat formatter = new DecimalFormat("#,###");

	public static String comma(long num) {
		return formatter.format(num);
	}

	private final List<String> summary = new ArrayList<>();
	private final List<String> details = new ArrayList<>();

	public static List<String> build() {
		List<String> ans = new ArrayList<>();
		ans.add("Summary:");
		ans.add("");
		ans.addAll(INS.summary);
		ans.add("------------------------------------------------------------");
		ans.add("");
		ans.add("Details:");
		ans.add("");
		ans.addAll(INS.details);
		return ans;
	}

	public void all(String str) {
		summary.add(str);
		details.add(str);
	}

	public void line(boolean important, String str) {
		if (important) summary.add(str);
		details.add(str);
	}

	public void detail(String str) {
		details.add(str);
	}

	public void summary(String str) {
		summary.add(str);
	}

	public void dash() {
		all("--------------------");
	}


	public static void reportLoadTime() {
		long launcher = 0;
		long total = 0;
		for (var chart : LPBootCore.TIME_CHART) {
			if (chart.stage().group() == LoadingStageGroup.LAUNCHER) {
				launcher += chart.time();
			}
			total += chart.time();
		}
		INS.all("Game takes %d seconds to bootstrap".formatted(total / 1000));
		int index = 0;
		for (var chart : LPBootCore.TIME_CHART) {
			index++;
			INS.line(chart.time() >= 2000, "%2d. %s: %s ms".formatted(index, chart.stage().getText(), comma(chart.time())));
		}
	}

	public static void reportMod(boolean isClient) {
		int modDisplayThres = 100;
		int modDetailThres = 1000;
		int detailDisplayThres = 100;
		for (var mod : ModTracker.MAP.entrySet()) {
			for (var event : mod.getValue().eventStartTime.keySet()) {
				LPBootCore.LOGGER.error("Unfinished event recording for {} - {}", mod.getKey(), LPEarly.parseClassName(event.getClass()));
			}
		}
		long gatherTime = ModTracker.GATHER_TIME.get();
		long gatherActual = LPBootCore.TIME_CHART.stream().filter(e -> e.stage().toString().startsWith("GATHER"))
				.reduce(0L, (a, b) -> a + b.time(), Long::sum);
		INS.all("Mods use %d seconds to initialize".formatted(
				gatherActual / 1000L
		));
		INS.all("Mod init happens in parallel as part of Game Bootstrap, in stage <Constructing Mods> ~ <Loading Registries>");
		INS.all("Mods use %d seconds to load".formatted(
				ModTracker.RELOAD_TIME.get() / 1000000000L));
		INS.all("Mod init takes %.2f seconds CPU time. Thread utilization: %.2f%%".formatted(
				gatherTime * 1e-9, gatherTime * 1e-4 / gatherActual));
		if (isClient) INS.all("Mod loading is part of reload tasks, in task <ClientModLoader>");
		var mods = ModTracker.MAP.entrySet().stream().sorted(Comparator.comparingLong(e -> -e.getValue().totalTime.get())).toList();
		long misc = 0;
		int miscCount = 0;
		for (var ent : mods) {
			long totalTime = ent.getValue().totalTime.get();
			long mil = totalTime / 1000000;
			if (mil < modDisplayThres) {
				misc += totalTime;
				miscCount++;
			} else {
				INS.line(mil >= modDetailThres, "- " + ent.getKey() + ": " + comma(mil) + " ms");
				if (mil >= modDetailThres) {
					var mod = ent.getValue();
					long construct = mod.constructTime / 1000000;
					if (construct >= detailDisplayThres) {
						INS.detail("  | - Construct: " + comma(construct) + " ms");
					}
					Map<Class<?>, Long> clsMap = new HashMap<>();
					for (var e : mod.eventTotalTime.entrySet()) {
						clsMap.compute(e.getKey().getClass(), (k, v) -> (v == null ? 0 : v) + e.getValue());
					}
					var events = clsMap.entrySet().stream().sorted(Comparator.comparingLong(e -> -e.getValue())).toList();
					long other = 0;
					for (var eve : events) {
						long time = eve.getValue() / 1000000;
						if (time < detailDisplayThres) {
							other += eve.getValue();
						} else {
							String name = LPEarly.parseClassName(eve.getKey());
							INS.detail("  | - " + name + ": " + comma(time) + " ms");
						}
					}
					INS.detail("  | - Others: " + comma(other / 1000000) + " ms");
				}
			}
		}
		INS.detail("Misc " + miscCount + " mods: " + comma(misc / 1000000) + " ms");
	}

}
