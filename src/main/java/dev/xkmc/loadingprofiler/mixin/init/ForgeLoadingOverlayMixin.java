package dev.xkmc.loadingprofiler.mixin.init;

import dev.xkmc.loadingprofiler.client.LPClientTracker;
import dev.xkmc.loadingprofiler.init.ClientStages;
import dev.xkmc.loadingprofiler.init.LPEarly;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public class ForgeLoadingOverlayMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	private void loadingprofiler$mainInit(CallbackInfo ci) {
		LPEarly.info(ClientStages.END_MAIN);
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void loadingprofiler$render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		LPClientTracker.tickReload(Util.getMillis());
	}

}
