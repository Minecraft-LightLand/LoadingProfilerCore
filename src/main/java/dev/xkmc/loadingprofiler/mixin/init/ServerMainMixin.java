package dev.xkmc.loadingprofiler.mixin.init;

import dev.xkmc.loadingprofiler.init.LPEarly;
import dev.xkmc.loadingprofiler.init.ServerStages;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class ServerMainMixin {

	@Inject(method = "main", remap = false, at = @At("HEAD"))
	private static void loadingprofiler$mainInit(String[] args, CallbackInfo ci) {
		LPEarly.info(ServerStages.SERVER_START);
	}

}
