package dev.xkmc.loadingprofiler.mixin.gather;

import dev.xkmc.loadingprofiler.init.CommonStages;
import dev.xkmc.loadingprofiler.init.LPEarly;
import net.neoforged.neoforge.registries.RegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegistryManager.class)
public class RegistryManagerMixin {

	@Inject(method = "postNewRegistryEvent", remap = false, at = @At("HEAD"))
	private static void loadingprofiler$watch(CallbackInfo ci) {
		LPEarly.info(CommonStages.GATHER_CREATE_REGISTRIES);
	}

}
