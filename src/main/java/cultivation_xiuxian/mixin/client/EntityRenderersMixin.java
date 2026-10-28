package cultivation_xiuxian.mixin.client;

import java.util.Map;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import cultivation_xiuxian.beasts.CommonBeasts;
import cultivation_xiuxian.beasts.CommonSnakeBeast;
import cultivation_xiuxian.client.renderer.CommonSnakeRenderer;

// snake renderer provider, vanilla's noop entry would crash on screen
@Mixin(EntityRenderers.class)
public abstract class EntityRenderersMixin {
    @Shadow
    @Final
    private static Map<EntityType<?>, EntityRendererProvider<?>> PROVIDERS;

    @Inject(method = "<clinit>*", at = @At("TAIL"))
    private static void cultivation$snakeRenderer(CallbackInfo ci) {
        EntityRendererProvider<CommonSnakeBeast> snakeProvider = CommonSnakeRenderer::new;
        PROVIDERS.put(CommonBeasts.COMMON_SNAKE_BEAST, snakeProvider);
    }
}
