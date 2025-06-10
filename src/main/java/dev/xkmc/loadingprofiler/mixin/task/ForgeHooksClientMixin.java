package dev.xkmc.loadingprofiler.mixin.task;

import dev.xkmc.loadingprofiler.client.LPClientTracker;
import dev.xkmc.loadingprofiler.client.ModelStateTracker;
import dev.xkmc.loadingprofiler.client.ModelStates;
import dev.xkmc.loadingprofiler.init.ClientStages;
import dev.xkmc.loadingprofiler.init.LPEarly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(value = ClientHooks.class, remap = false)
public class ForgeHooksClientMixin {

	@Inject(method = "initClientHooks", at = @At("HEAD"))
	private static void loadingprofiler$startInit(Minecraft mc, ReloadableResourceManager resourceManager, CallbackInfo ci) {
		LPEarly.info(ClientStages.INIT_CLIENT_HOOK);
	}

	@Inject(method = "initClientHooks", at = @At("TAIL"))
	private static void loadingprofiler$endInit(Minecraft mc, ReloadableResourceManager resourceManager, CallbackInfo ci) {
		LPEarly.info(ClientStages.SETUP_VANILLA);
	}
	@Inject(method = "onRegisterAdditionalModels", at = @At("HEAD"))
	private static void loadingprofiler$startAdditional(Set<ResourceLocation> additionalModels, CallbackInfo ci) {
		ModelStateTracker.step(ModelStates.MODDED);
	}

	@Inject(method = "onRegisterAdditionalModels", at = @At("TAIL"))
	private static void loadingprofiler$endAdditional(Set<ResourceLocation> additionalModels, CallbackInfo ci) {
		ModelStateTracker.step(ModelStates.RESOLVING);
	}

	@Inject(method = "onModifyBakingResult", at = @At("HEAD"))
	private static void loadingprofiler$startModify(Map<ModelResourceLocation, BakedModel> models, Map<ResourceLocation, AtlasSet.StitchResult> stitchResults, ModelBakery modelBakery, CallbackInfo ci) {
		ModelStateTracker.step(ModelStates.MODIFY);
	}

	@Inject(method = "onModifyBakingResult", at = @At("TAIL"))
	private static void loadingprofiler$endModify(Map<ModelResourceLocation, BakedModel> models, Map<ResourceLocation, AtlasSet.StitchResult> stitchResults, ModelBakery modelBakery, CallbackInfo ci) {
		ModelStateTracker.step(ModelStates.DISPATCHING);
		LPClientTracker.setBakedCount(new HashSet<>(models.values()).size());
	}

}
