package dev.xkmc.lpcore.init;

import dev.xkmc.loadingprofiler.bootstrap.ILoadingStage;
import dev.xkmc.loadingprofiler.bootstrap.LoadingStageGroup;

import java.util.function.Consumer;

public enum ClientStages implements ILoadingStage {
	CLIENT_START("Client Early Initialization", LPEarly::nop),
	BOOTSTRAP("Vanilla Bootstrap Start", LPEarly::nop),
	BOOTSTRAP_END("Loading User / Options", LPEarly::nop),
	MINECRAFT_START("Minecraft Client Initialization", LPEarly::nop),
	LANGUAGE("Loading Languages", LPEarly::nop),
	GATHER_CONSTRUCT(ModStage.GATHER_CONSTRUCT),
	GATHER_CREATE_REGISTRIES(ModStage.GATHER_CREATE_REGISTRIES),
	GATHER_OBJECT_HOLDERS(ModStage.GATHER_OBJECT_HOLDERS),
	GATHER_INJECT_CAPABILITIES(ModStage.GATHER_INJECT_CAPABILITIES),
	GATHER_LOAD_REGISTRIES(ModStage.GATHER_LOAD_REGISTRIES),
	LOAD_MODDED_PACK("Gather Modded Resource Packs", LPEarly::nop),
	GATHER_TASKS("Gather Tasks", LPEarly::nop),
	INIT_CLIENT_HOOK("Dispatch Client Events", LPEarly::nop),
	SETUP_VANILLA("Vanilla Client Setup", LPEarly::nop),
	END_MAIN("Parallel Dispatch", LPEarly::finish),
	;

	private final String text;
	private final Consumer<ClientStages> task;

	ClientStages(String text, Consumer<ClientStages> task) {
		this.text = text;
		this.task = task;
	}

	ClientStages(ModStage stage) {
		this(stage.text(), LPEarly::nop);
	}

	public LoadingStageGroup group() {
		return LoadingStageGroup.CLIENT;
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
