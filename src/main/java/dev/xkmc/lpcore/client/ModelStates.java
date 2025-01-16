package dev.xkmc.lpcore.client;

public enum ModelStates {
	READING("Reading jsons"),
	PARSING("Parsing jsons"),
	MODDED("Adding modded special models"),
	RESOLVING("Resolving hierarchy"),
	STITCHING("Stitching Textures"),
	BAKING("Baking models"),
	MODIFY("Mods altering models"),
	DISPATCHING("Assigning models to block"),
	WAIT("Waiting for barrier"),
	UPLOAD("Uploading stitch"),
	COMPLETE("Complete")
	;

	public final String text;

	ModelStates(String text) {
		this.text = text;
	}
}
