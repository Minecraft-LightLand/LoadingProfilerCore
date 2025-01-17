package dev.xkmc.lpcore.mixin.init;

import dev.xkmc.lpcore.client.LPClientTracker;
import dev.xkmc.lpcore.client.WrappedResourceManager;
import dev.xkmc.lpcore.init.ClientStages;
import dev.xkmc.lpcore.init.CommonStages;
import dev.xkmc.lpcore.init.LPEarly;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.client.loading.ClientModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientModLoader.class)
public class ClientModLoaderMixin {

	@Inject(method = "begin", remap = false, at = @At(value = "HEAD"))
	private static void loadingprofiler$startBegin(Minecraft minecraft, PackRepository pack, ReloadableResourceManager manager, CallbackInfo ci) {
		LPEarly.info(ClientStages.LANGUAGE);
		minecraft.resourceManager = new WrappedResourceManager(PackType.CLIENT_RESOURCES);
	}

	@Inject(method = "begin", remap = false, at = @At(value = "TAIL"))
	private static void loadingprofiler$endBegin(Minecraft minecraft, PackRepository pack, ReloadableResourceManager manager, CallbackInfo ci) {
		LPEarly.info(ClientStages.ADD_TASKS);
		for (var e : manager.listeners)
			LPClientTracker.MANAGER.registerReloadListener(e);
	}

	@Inject(method = "begin", remap = false, at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraftforge/client/loading/ClientModLoader;createRunnableWithCatch(Ljava/lang/Runnable;)Ljava/lang/Runnable;"))
	private static void loadingprofiler$gatherStart(CallbackInfo ci) {
		LPEarly.info(CommonStages.GATHER_CONSTRUCT);
	}

	@Inject(method = "begin", remap = false, at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraftforge/resource/ResourcePackLoader;loadResourcePacks(Lnet/minecraft/server/packs/repository/PackRepository;Ljava/util/function/Function;)V"))
	private static void loadingprofiler$gatherEnd(CallbackInfo ci) {
		LPEarly.info(ClientStages.LOAD_MODDED_PACK);
	}

}
