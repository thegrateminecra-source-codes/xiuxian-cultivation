package cultivation_xiuxian.client.renderer;

import cultivation_xiuxian.beasts.CommonSnakeBeast;
import cultivation_xiuxian.client.model.CommonSnakeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class CommonSnakeRenderer extends MobRenderer<CommonSnakeBeast, CommonSnakeRenderState, CommonSnakeModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            "cultivation_xiuxian", "textures/entity/common_snake_beast/common_snake_beast.png");
    private static final Identifier TEXTURE_SHEARED = Identifier.fromNamespaceAndPath(
            "cultivation_xiuxian", "textures/entity/common_snake_beast/common_snake_beast_sheared.png");

    public CommonSnakeRenderer(final EntityRendererProvider.Context context) {
        super(context, new CommonSnakeModel(CommonSnakeModel.createBodyLayer().bakeRoot()), 0.3F);
    }

    @Override
    public Identifier getTextureLocation(final CommonSnakeRenderState state) {
        return state.sheared ? TEXTURE_SHEARED : TEXTURE;
    }

    @Override
    public CommonSnakeRenderState createRenderState() {
        return new CommonSnakeRenderState();
    }

    @Override
    public void extractRenderState(final CommonSnakeBeast entity, final CommonSnakeRenderState state, final float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.sheared = entity.isSheared();
    }
}
