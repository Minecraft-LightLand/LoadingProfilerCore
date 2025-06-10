package dev.xkmc.loadingprofiler.mixin.init;

import dev.xkmc.loadingprofiler.client.LPClientTracker;
import dev.xkmc.loadingprofiler.client.WrappedResourceManager;
import dev.xkmc.loadingprofiler.init.ClientStages;
import dev.xkmc.loadingprofiler.init.CommonStages;
import dev.xkmc.loadingprofiler.init.LPEarly;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.neoforged.neoforge.client.loading.ClientModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientModLoader.class)
public class ClientModLoaderMixin {

	@Inject(method = "begin", at = @At(value = "HEAD"))
	private static void loadingprofiler$startBegin(Minecraft minecraft, PackRepository pack, ReloadableResourceManager manager, CallbackInfo ci) {
		LPEarly.info(ClientStages.LANGUAGE);
		minecraft.resourceManager = new WrappedResourceManager(PackType.CLIENT_RESOURCES);
	}

	@Inject(method = "begin", at = @At(value = "TAIL"))
	private static void loadingprofiler$endBegin(Minecraft minecraft, PackRepository pack, ReloadableResourceManager manager, CallbackInfo ci) {
		LPEarly.info(ClientStages.ADD_TASKS);
		for (var e : manager.listeners)
			LPClientTracker.MANAGER.registerReloadListener(e);
	}

	@Inject(method = "begin", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/loading/ClientModLoader;begin(Ljava/lang/Runnable;Z)V"))
	private static void loadingprofiler$gatherStart(CallbackInfo ci) {
		LPEarly.info(CommonStages.GATHER_CONSTRUCT);
	}

	@Inject(method = "begin", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/resource/ResourcePackLoader;populatePackRepository(Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/server/packs/PackType;Z)V"))
	private static void loadingprofiler$gatherEnd(CallbackInfo ci) {
		LPEarly.info(ClientStages.LOAD_MODDED_PACK);
	}

}
