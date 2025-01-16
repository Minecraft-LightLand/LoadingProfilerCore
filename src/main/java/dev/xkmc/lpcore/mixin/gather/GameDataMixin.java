package dev.xkmc.lpcore.mixin.gather;

import dev.xkmc.lpcore.init.CommonStages;
import dev.xkmc.lpcore.init.LPEarly;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameData.class)
public class GameDataMixin {

	@Inject(method = "postRegisterEvents", remap = false, at = @At("HEAD"))
	private static void loadingprofiler$watch(CallbackInfo ci) {
		LPEarly.info(CommonStages.GATHER_LOAD_REGISTRIES);
	}

}
