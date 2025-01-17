package dev.xkmc.lpcore.init;

import dev.xkmc.loadingprofiler.bootstrap.ILoadingStage;
import dev.xkmc.loadingprofiler.bootstrap.LoadingStageGroup;
import dev.xkmc.lpcore.logdelegate.ModTracker;

import java.util.function.Consumer;

public enum ServerStages implements ILoadingStage {
	SERVER_START("Server Early Initialization", LPEarly::nop),
	BOOTSTRAP("Vanilla Bootstrap Start", LPEarly::nop),
	BOOTSTRAP_END("Load Language", LPEarly::nop),
	GATHER_CONSTRUCT(ModStage.GATHER_CONSTRUCT),
	GATHER_CREATE_REGISTRIES(ModStage.GATHER_CREATE_REGISTRIES),
	GATHER_OBJECT_HOLDERS(ModStage.GATHER_OBJECT_HOLDERS),
	GATHER_INJECT_CAPABILITIES(ModStage.GATHER_INJECT_CAPABILITIES),
	GATHER_LOAD_REGISTRIES(ModStage.GATHER_LOAD_REGISTRIES),
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
