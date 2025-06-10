package dev.xkmc.loadingprofiler.init;

import dev.xkmc.loadingprofiler.bootstrap.ILoadingStage;
import dev.xkmc.loadingprofiler.bootstrap.LoadingStageGroup;
import dev.xkmc.loadingprofiler.logdelegate.ModTracker;

import java.util.function.Consumer;

public enum ServerStages implements ILoadingStage {
	SERVER_START("Server Early Initialization", LPEarly::nop),
	BOOTSTRAP("Vanilla Bootstrap Start", LPEarly::nop),
	BOOTSTRAP_END("Load Language", LPEarly::nop),
	GATHER_CONSTRUCT(ModStage.GATHER_CONSTRUCT),
	GATHER_CREATE_REGISTRIES(ModStage.GATHER_CREATE_REGISTRIES),
	GATHER_LOAD_REGISTRIES(ModStage.GATHER_LOAD_REGISTRIES),
	GATHER_FREEZE_REGISTRIES(ModStage.GATHER_FREEZE_REGISTRIES),
	GATHER_COMPLETE_REGISTRY(ModStage.GATHER_COMPLETE_REGISTRY),
	MOD_SETUP("Mod Setup", ModTracker::switchTracker),
	MOD_TALK("Inter-Mod Communication", LPEarly::nop),
	COMPLETE("Complete Loading", LPEarly::finish),
	;

	private final String text;
	private final Consumer<ServerStages> task;

	ServerStages(String text, Consumer<ServerStages> task) {
		this.text = text;
		this.task = task;
	}

	ServerStages(ModStage stage) {
		this(stage.text(), LPEarly::nop);
	}

	public LoadingStageGroup group() {
		return LoadingStageGroup.SERVER;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void run() {
		task.accept(this);
	}

}
