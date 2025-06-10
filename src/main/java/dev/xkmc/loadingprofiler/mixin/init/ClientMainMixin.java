package dev.xkmc.loadingprofiler.mixin.init;

import dev.xkmc.loadingprofiler.init.ClientStages;
import dev.xkmc.loadingprofiler.init.LPEarly;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class ClientMainMixin {

	@Inject(method = "main", remap = false, at = @At("HEAD"))
	private static void loadingprofiler$mainInit(String[] args, CallbackInfo ci) {
		LPEarly.info(ClientStages.CLIENT_START);
	}

	@Inject(method = "main", remap = false, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;beginInitialization()V"))
	private static void loadingprofiler$minecraftInit(String[] args, CallbackInfo ci) {
		LPEarly.info(ClientStages.MINECRAFT_START);
	}

}
