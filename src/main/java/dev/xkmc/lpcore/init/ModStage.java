package dev.xkmc.lpcore.init;

public record ModStage(String text) {

	public static final ModStage GATHER_CONSTRUCT = new ModStage("Constructing Mods");
	public static final ModStage GATHER_CREATE_REGISTRIES = new ModStage("Creating Registries");
	public static final ModStage GATHER_OBJECT_HOLDERS = new ModStage("Gathering Object Holders");
	public static final ModStage GATHER_INJECT_CAPABILITIES = new ModStage("Injecting Capabilities");
	public static final ModStage GATHER_LOAD_REGISTRIES = new ModStage("Loading Registries");

}
