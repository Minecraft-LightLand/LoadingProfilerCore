package dev.xkmc.loadingprofiler.init;

public record ModStage(String text) {

	public static final ModStage GATHER_CONSTRUCT = new ModStage("Constructing Mods");
	public static final ModStage GATHER_CREATE_REGISTRIES = new ModStage("Creating Registries");
	public static final ModStage GATHER_LOAD_REGISTRIES = new ModStage("Loading Registries");
	public static final ModStage GATHER_FREEZE_REGISTRIES = new ModStage("Freezing Registries");
	public static final ModStage GATHER_COMPLETE_REGISTRY = new ModStage("Registration Complete");

}
