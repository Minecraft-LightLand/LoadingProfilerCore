package dev.xkmc.lpcore.client;

import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ModelStateTracker {

	private static ModelStates state;
	private static long prevTime;

	public static List<Pair<ModelStates, Long>> TIME_CHART = new ArrayList<>();

	public static String getModelState() {
		if (state == null)
			return "Running Model Manager";
		return "Model Manager " + (state.ordinal() + 1) + " - " + state.text;
	}

	public static void step(ModelStates next) {
		if (state == null || next.ordinal() > state.ordinal()) {
			long time = System.nanoTime();
			if (state != null) {
				long diff = time - prevTime;
				TIME_CHART.add(Pair.of(state, diff));
			}
			prevTime = time;
			state = next;
		}
	}

}
