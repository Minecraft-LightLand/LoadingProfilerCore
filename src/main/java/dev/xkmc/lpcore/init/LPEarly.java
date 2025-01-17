package dev.xkmc.lpcore.init;

import dev.xkmc.loadingprofiler.bootstrap.ILoadingStage;
import dev.xkmc.loadingprofiler.bootstrap.LPBootCore;
import dev.xkmc.loadingprofiler.bootstrap.LoadingStageGroup;
import dev.xkmc.lpcore.logdelegate.LogDelegator;
import dev.xkmc.lpcore.logdelegate.LoggerForFMLModContainer;
import dev.xkmc.lpcore.reporting.ReportWriter;
import dev.xkmc.lpcore.reporting.Summarizer;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

public class LPEarly {

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
		Summarizer.reportLoadTime();
		if (stage.group() == LoadingStageGroup.SERVER) {
			Summarizer.INS.dash();
			Summarizer.reportMod(false);
			ReportWriter.generate(Summarizer.build());
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


}
