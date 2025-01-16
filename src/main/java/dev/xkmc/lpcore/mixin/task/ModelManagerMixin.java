package dev.xkmc.lpcore.mixin.task;

import dev.xkmc.lpcore.client.ModelStateTracker;
import dev.xkmc.lpcore.client.ModelStates;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
public class ModelManagerMixin {

	@Inject(method = "reload", at = @At("HEAD"))
	public void loadingprofiler$onReload(PreparableReloadListener.PreparationBarrier p_249079_, ResourceManager p_251134_, ProfilerFiller p_250336_, ProfilerFiller p_252324_, Executor p_250550_, Executor p_249221_, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		ModelStateTracker.step(ModelStates.READING);
	}

	@Inject(method = "loadModels", at = @At("HEAD"))
	public void loadingprofiler$onLoadModels(ProfilerFiller p_252136_, Map<ResourceLocation, AtlasSet.StitchResult> p_250646_, ModelBakery p_248945_, CallbackInfoReturnable cir) {
		ModelStateTracker.step(ModelStates.BAKING);
	}

	@Inject(method = "loadModels", at = @At("TAIL"))
	public void loadingprofiler$finishLoadModels(ProfilerFiller p_252136_, Map<ResourceLocation, AtlasSet.StitchResult> p_250646_, ModelBakery p_248945_, CallbackInfoReturnable cir) {
		ModelStateTracker.step(ModelStates.WAIT);
	}

	@Inject(method = "apply", at = @At("HEAD"))
	public void loadingprofiler$onApply(CallbackInfo ci) {
		ModelStateTracker.step(ModelStates.UPLOAD);
	}

	@Inject(method = "apply", at = @At("TAIL"))
	public void loadingprofiler$finishApply(CallbackInfo ci) {
		ModelStateTracker.step(ModelStates.COMPLETE);
	}

}
