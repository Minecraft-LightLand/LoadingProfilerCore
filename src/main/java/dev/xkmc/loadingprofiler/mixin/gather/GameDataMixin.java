package dev.xkmc.loadingprofiler.mixin.gather;

import dev.xkmc.loadingprofiler.init.CommonStages;
import dev.xkmc.loadingprofiler.init.LPEarly;
import net.neoforged.neoforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameData.class)
public class GameDataMixin {

	@Inject(method = "postRegisterEvents", remap = false, at = @At("HEAD"))
	private static void loadingprofiler$watch(CallbackInfo ci) {
		LPEarly.info(CommonStages.GATHER_LOAD_REGISTRIES);
	}

	@Inject(method = "freezeData", remap = false, at = @At("HEAD"))
	private static void loadingprofiler$freezing(CallbackInfo ci) {
		LPEarly.info(CommonStages.GATHER_FREEZE_REGISTRIES);
	}

	@Inject(method = "freezeData", remap = false, at = @At("TAIL"))
	private static void loadingprofiler$frozen(CallbackInfo ci) {
		LPEarly.info(CommonStages.GATHER_COMPLETE_REGISTRY);
	}

}
