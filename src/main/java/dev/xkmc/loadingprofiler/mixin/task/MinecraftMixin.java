package dev.xkmc.loadingprofiler.mixin.task;

import dev.xkmc.loadingprofiler.client.LPClientTracker;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Inject(method = "onGameLoadFinished", at = @At("HEAD"))
	public void loadingprofiler$onFinish(@Nullable Minecraft.GameLoadCookie gameLoadCookie, CallbackInfo ci) {
		LPClientTracker.tickReload(Util.getMillis());
	}

}
