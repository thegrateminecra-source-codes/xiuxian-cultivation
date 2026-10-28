package cultivation_xiuxian.mixin;

import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CreativeModeTabs.class)
public abstract class CreativeModeTabsMixin {
    @Inject(method = "validate", at = @At("HEAD"), cancellable = true)
    private static void cultivation$skipDupPositionCheck(CallbackInfo ci) {
        ci.cancel();
    }
}
