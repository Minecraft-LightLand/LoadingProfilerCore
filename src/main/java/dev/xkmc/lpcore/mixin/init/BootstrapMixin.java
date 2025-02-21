package dev.xkmc.lpcore.mixin.init;

import dev.xkmc.lpcore.init.CommonStages;
import dev.xkmc.lpcore.init.LPEarly;
import net.minecraft.server.Bootstrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bootstrap.class)
public class BootstrapMixin {


	@Inject(method = "bootStrap", at = @At("HEAD"))
	private static void loadingprofiler$bootstrapStart(CallbackInfo ci) {
		LPEarly.info(CommonStages.BOOTSTRAP);
	}

	@Inject(method = "bootStrap", at = @At("TAIL"))
	private static void loadingprofiler$bootstrapEnd(CallbackInfo ci) {
		LPEarly.info(CommonStages.BOOTSTRAP_END);
	}

}
