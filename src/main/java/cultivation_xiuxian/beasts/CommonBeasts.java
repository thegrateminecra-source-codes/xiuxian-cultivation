package cultivation_xiuxian.beasts;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import cultivation_xiuxian.CultivationXiuxian;
import cultivation_xiuxian.mixin.DefaultAttributesAccess;

public class CommonBeasts {
    // Common Snake Beast Registering
    public static final ResourceKey<EntityType<?>> COMMON_SNAKE_BEAST_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(CultivationXiuxian.MOD_ID, "common_snake_beast"));
    public static final EntityType<CommonSnakeBeast> COMMON_SNAKE_BEAST = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, COMMON_SNAKE_BEAST_KEY,
            EntityType.Builder.of(CommonSnakeBeast::new, MobCategory.CREATURE)
                    .sized(0.9F, 0.3F)
                    .eyeHeight(0.22F)
                    .clientTrackingRange(10)
                    .build(COMMON_SNAKE_BEAST_KEY));

    public static void init() {
    
        CommonSounds.init();

  
        DefaultAttributesAccess.suppliers().put(COMMON_SNAKE_BEAST, CommonSnakeBeast.createAttributes().build());


        SpawnPlacements.register(COMMON_SNAKE_BEAST, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> {
                    boolean brightEnough = EntitySpawnReason.ignoresLightRequirements(reason) || level.getRawBrightness(pos, 0) > 8;
                    return level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && brightEnough;
                });
    }
}
