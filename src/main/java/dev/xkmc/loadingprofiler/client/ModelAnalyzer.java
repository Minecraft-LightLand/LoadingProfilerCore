package dev.xkmc.loadingprofiler.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

public class ModelAnalyzer extends SimplePreparableReloadListener<ModelAnalyzer.Data> {

	public record Data(int count, int state, long size, Map<String, Long> map) {

	}

	private static String getSource(PackResources pack) {
		return pack.packId();
	}

	public static Map<ResourceLocation, Resource> listActual(List<PackResources> packs, String prefix) {
		Map<ResourceLocation, Resource> map = new TreeMap<>();
		List<PackResources> all = new ArrayList<>();
		for (var e : packs) {
			all.add(e);
		}
		for (var e : all) {
			var ids = e.getNamespaces(PackType.CLIENT_RESOURCES);
			for (var id : ids) {
				e.listResources(PackType.CLIENT_RESOURCES, id, prefix, (rl, io) -> {
					if (rl.getPath().endsWith(".json")) {
						map.put(rl, new Resource(e, io, ResourceMetadata.EMPTY_SUPPLIER));
					}
				});
			}
		}
		return map;
	}

	@Override
	protected Data prepare(ResourceManager manager, ProfilerFiller profiler) {
		var packs = Minecraft.getInstance().getResourcePackRepository().openAllSelected();
		var models = listActual(packs, "models");
		var states = listActual(packs, "blockstates");
		Map<String, Long> map = new LinkedHashMap<>();
		int modelCount = 0, blockCount = 0;
		long totalSize = 0;
		for (var ent : models.entrySet()) {
			modelCount++;
			String source = getSource(ent.getValue().source());
			try {
				var stream = ent.getValue().open();
				int size = stream.readAllBytes().length;
				stream.close();
				map.compute(source, (k, v) -> (v == null ? 0 : v) + size);
				totalSize += size;
			} catch (Exception ignored) {
			}
		}
		for (var ent : states.entrySet()) {
			blockCount++;
			String source = getSource(ent.getValue().source());
			try {
				var stream = ent.getValue().open();
				int size = stream.readAllBytes().length;
				stream.close();
				map.compute(source, (k, v) -> (v == null ? 0 : v) + size);
				totalSize += size;
			} catch (Exception ignored) {
			}
		}
		return new Data(modelCount, blockCount, totalSize, map);
	}

	@Override
	protected void apply(Data data, ResourceManager manager, ProfilerFiller profiler) {
		LPClientTracker.putModelData(data);
	}

}
