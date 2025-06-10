package dev.xkmc.loadingprofiler.mixin.task;

import dev.xkmc.loadingprofiler.client.ModelStateTracker;
import dev.xkmc.loadingprofiler.client.ModelStates;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {

	@Inject(method = "loadBlockModel", at = @At("HEAD"))
	public void loadingprofiler$onReload(ResourceLocation p_119365_, CallbackInfoReturnable<BlockModel> cir) {
		ModelStateTracker.step(ModelStates.PARSING);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void loadingprofiler$finishInit(BlockColors p_249183_, ProfilerFiller p_252014_, Map p_251087_, Map p_250416_, CallbackInfo ci) {
		ModelStateTracker.step(ModelStates.STITCHING);
	}



}
