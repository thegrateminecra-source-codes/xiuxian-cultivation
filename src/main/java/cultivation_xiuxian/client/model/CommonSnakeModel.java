package cultivation_xiuxian.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import cultivation_xiuxian.client.renderer.CommonSnakeRenderState;
import net.minecraft.util.Mth;

public class CommonSnakeModel extends EntityModel<CommonSnakeRenderState> {
    private static final int SEGMENT_COUNT = 9;
    private final ModelPart[] segments = new ModelPart[SEGMENT_COUNT];

    private static final int[][] SIZES = {
            {3, 3, 4},  // head
            {3, 2, 4}, {3, 2, 4}, {3, 2, 4}, {3, 2, 4}, {3, 2, 4}, {3, 2, 4},
            {2, 2, 3},  // tail
            {1, 1, 3}   // tip
    };
   
    private static final int[][] TEX = {
            {0, 0},
            {0, 7}, {0, 7}, {0, 7}, {0, 7}, {0, 7}, {0, 7},
            {8, 15},
            {14, 0}
    };

    public CommonSnakeModel(final ModelPart root) {
        super(root);
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            this.segments[i] = root.getChild(segName(i));
        }
    }

    private static String segName(final int i) {
        return "seg" + i;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        float z = -6.5F; 
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            int[] s = SIZES[i];
            CubeListBuilder cubes = CubeListBuilder.create()
                    .texOffs(TEX[i][0], TEX[i][1])
                    .addBox(s[0] * -0.5F, -s[1], s[2] * -0.5F, s[0], s[1], s[2]);
            PartDefinition part = root.addOrReplaceChild(
                    segName(i),
                    cubes,
                    PartPose.offset(0.0F, 24.0F - s[1], z)
            );
            if (i == 0) {
  
                part.addOrReplaceChild("snout", CubeListBuilder.create()
                                .texOffs(14, 8)
                                .addBox(-1.0F, -1.0F, -1.0F, 2, 1, 1),
                        PartPose.offset(0.0F, -0.5F, -2.0F));
            }
            z += s[2] - 1.0F; 
        }
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(final CommonSnakeRenderState state) {
        super.setupAnim(state);
        float move = Math.min(state.walkAnimationSpeed, 1.0F);
        float amplitude = 0.04F + move * 0.22F;
        for (int i = 0; i < this.segments.length; i++) {

            this.segments[i].yRot = Mth.sin(state.ageInTicks * 0.45F + i * 0.55F)
                    * amplitude * (1.0F + (SEGMENT_COUNT - i) * 0.12F);
        }
    }
}
