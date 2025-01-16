package dev.xkmc.lpcore.mixin.gather;

import dev.xkmc.lpcore.init.CommonStages;
import dev.xkmc.lpcore.init.LPEarly;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapabilityManager.class)
public class CapabilityManagerMixin {

	@Inject(method = "injectCapabilities", remap = false, at = @At("HEAD"))
	private void loadingprofiler$watch(CallbackInfo ci) {
		LPEarly.info(CommonStages.GATHER_INJECT_CAPABILITIES);
	}

}
