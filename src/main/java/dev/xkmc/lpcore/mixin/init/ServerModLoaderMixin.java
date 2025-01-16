package dev.xkmc.lpcore.mixin.init;

import dev.xkmc.lpcore.init.CommonStages;
import dev.xkmc.lpcore.init.LPEarly;
import dev.xkmc.lpcore.init.ServerStages;
import net.minecraftforge.server.loading.ServerModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerModLoader.class)
public class ServerModLoaderMixin {

	@Inject(method = "load", remap = false, at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraftforge/fml/ModLoader;gatherAndInitializeMods(Lnet/minecraftforge/fml/ModWorkManager$DrivenExecutor;Ljava/util/concurrent/Executor;Ljava/lang/Runnable;)V"))
	private static void loadingprofiler$gatherStart(CallbackInfo ci) {
		LPEarly.info(CommonStages.GATHER_CONSTRUCT);
	}

	@Inject(method = "load", remap = false, at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraftforge/fml/ModLoader;loadMods(Lnet/minecraftforge/fml/ModWorkManager$DrivenExecutor;Ljava/util/concurrent/Executor;Ljava/lang/Runnable;)V"))
	private static void loadingprofiler$LoadStart(CallbackInfo ci) {
		LPEarly.info(ServerStages.MOD_SETUP);
	}

	@Inject(method = "load", remap = false, at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraftforge/fml/ModLoader;finishMods(Lnet/minecraftforge/fml/ModWorkManager$DrivenExecutor;Ljava/util/concurrent/Executor;Ljava/lang/Runnable;)V"))
	private static void loadingprofiler$finishStart(CallbackInfo ci) {
		LPEarly.info(ServerStages.MOD_TALK);
	}

	@Inject(method = "load", remap = false, at = @At(value = "TAIL"))
	private static void loadingprofiler$complete(CallbackInfo ci) {
		LPEarly.info(ServerStages.COMPLETE);
	}

}
