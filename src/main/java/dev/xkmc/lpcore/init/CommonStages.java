package dev.xkmc.lpcore.init;

public enum CommonStages {
	BOOTSTRAP(ClientStages.BOOTSTRAP, ServerStages.BOOTSTRAP),
	BOOTSTRAP_END(ClientStages.BOOTSTRAP_END, ServerStages.BOOTSTRAP_END),
	GATHER_CONSTRUCT(ClientStages.GATHER_CONSTRUCT, ServerStages.GATHER_CONSTRUCT),
	GATHER_CREATE_REGISTRIES(ClientStages.GATHER_CREATE_REGISTRIES, ServerStages.GATHER_CREATE_REGISTRIES),
	GATHER_OBJECT_HOLDERS(ClientStages.GATHER_OBJECT_HOLDERS, ServerStages.GATHER_OBJECT_HOLDERS),
	GATHER_INJECT_CAPABILITIES(ClientStages.GATHER_INJECT_CAPABILITIES, ServerStages.GATHER_INJECT_CAPABILITIES),
	GATHER_LOAD_REGISTRIES(ClientStages.GATHER_LOAD_REGISTRIES, ServerStages.GATHER_LOAD_REGISTRIES),
	;
	public final ClientStages client;
	public final ServerStages server;

	CommonStages(ClientStages client, ServerStages server) {
		this.client = client;
		this.server = server;
	}

}
