package dev.xkmc.lpcore.init;

import dev.xkmc.loadingprofiler.bootstrap.ILoadingStage;
import dev.xkmc.loadingprofiler.bootstrap.LPBootCore;
import dev.xkmc.loadingprofiler.bootstrap.LoadingStageGroup;
import dev.xkmc.lpcore.logdelegate.LogDelegator;
import dev.xkmc.lpcore.logdelegate.LoggerForFMLModContainer;
import dev.xkmc.lpcore.logdelegate.ModTracker;
import dev.xkmc.lpcore.reporting.ReportWriter;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LPEarly {

	public static final List<String> LIST = new ArrayList<>();

	public static void info(ILoadingStage stage) {
		LPBootCore.info(stage);
	}

	public static void info(CommonStages stage) {
		var prev = LPBootCore.prev();
		if (prev == null) return;
		switch (prev.group()) {
			case CLIENT -> info(stage.client);
			case SERVER -> info(stage.server);
		}
	}

	public static void nop(ILoadingStage stage) {

	}

	public static void finish(ILoadingStage stage) {
		long launcher = 0;
		long total = 0;
		for (var chart : LPBootCore.TIME_CHART) {
			if (chart.stage().group() == LoadingStageGroup.LAUNCHER) {
				launcher += chart.time();
			}
			total += chart.time();
		}
		LIST.add("Game took " + total / 1000 + " seconds to bootstrap");
		for (var chart : LPBootCore.TIME_CHART) {
			LIST.add("- " + chart.stage().getText() + ": " + comma(chart.time()) + " ms");
		}
		LIST.add("--------------------");
		ModTracker.getReport(LIST);
		if (stage.group() == LoadingStageGroup.SERVER) {
			ReportWriter.generate(LIST);
		}
	}

	public static void delegateLoggers(ClientStages stage) {
		LogDelegator.delegate(FMLModContainer.class, LoggerForFMLModContainer::new);
	}

	public static String parseClassName(Class<?> cls) {
		String[] names = cls.getName().split("\\.");
		String name = names[names.length - 1];
		if (name.contains("$$Lambda")) {
			String str = name.split("\\$\\$Lambda")[0];
			if (!str.isEmpty()) {
				name = str;
			}
		}
		return name;
	}

	private static final DecimalFormat formatter = new DecimalFormat("#,###");

	public static String comma(long num) {
		return formatter.format(num);
	}


}
