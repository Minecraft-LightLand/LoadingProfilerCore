package dev.xkmc.loadingprofiler.init;

public enum CommonStages {
	BOOTSTRAP(ClientStages.BOOTSTRAP, ServerStages.BOOTSTRAP),
	BOOTSTRAP_END(ClientStages.BOOTSTRAP_END, ServerStages.BOOTSTRAP_END),
	GATHER_CONSTRUCT(ClientStages.GATHER_CONSTRUCT, ServerStages.GATHER_CONSTRUCT),
	GATHER_CREATE_REGISTRIES(ClientStages.GATHER_CREATE_REGISTRIES, ServerStages.GATHER_CREATE_REGISTRIES),
	GATHER_LOAD_REGISTRIES(ClientStages.GATHER_LOAD_REGISTRIES, ServerStages.GATHER_LOAD_REGISTRIES),
	GATHER_FREEZE_REGISTRIES(ClientStages.GATHER_FREEZE_REGISTRIES, ServerStages.GATHER_FREEZE_REGISTRIES),
	GATHER_COMPLETE_REGISTRY(ClientStages.GATHER_COMPLETE_REGISTRY, ServerStages.GATHER_COMPLETE_REGISTRY),
	;
	public final ClientStages client;
	public final ServerStages server;

	CommonStages(ClientStages client, ServerStages server) {
		this.client = client;
		this.server = server;
	}

}
