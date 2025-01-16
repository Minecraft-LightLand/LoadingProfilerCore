package dev.xkmc.lpcore.mixin.gather;

import dev.xkmc.lpcore.init.CommonStages;
import dev.xkmc.lpcore.init.LPEarly;
import net.minecraftforge.registries.ObjectHolderRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ObjectHolderRegistry.class)
public class ObjectHolderRegistryMixin {

	@Inject(method = "findObjectHolders", remap = false, at = @At("HEAD"))
	private static void loadingprofiler$watch(CallbackInfo ci) {
		LPEarly.info(CommonStages.GATHER_OBJECT_HOLDERS);
	}

}
