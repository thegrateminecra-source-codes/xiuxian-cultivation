package cultivation_xiuxian.mixin;

import cultivation_xiuxian.beasts.PlainsSpawns;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftServer.class)
public abstract class MinecraftServerSpawnMixin {
    @Shadow
    public abstract RegistryAccess.Frozen registryAccess();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cultivation_xiuxian$applyPlainsSpawns(CallbackInfo ci) {
        PlainsSpawns.apply(this.registryAccess());
    }
}
