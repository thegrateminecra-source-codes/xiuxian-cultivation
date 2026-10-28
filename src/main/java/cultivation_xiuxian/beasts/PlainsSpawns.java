package cultivation_xiuxian.beasts;

import java.util.Optional;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;

// biome spawn tables ride on NATURAL_MOB_SPAWNS and stay editable until the server
// constructor returns, the same window fabric-biome-api hooks
public class PlainsSpawns {
    private static final int SPAWN_WEIGHT = 4;
    private static final int MIN_GROUP = 1;
    private static final int MAX_GROUP = 2;

    public static void apply(RegistryAccess access) {
        Registry<Biome> biomes = access.lookupOrThrow(Registries.BIOME);
        addSpawnTo(biomes, Biomes.PLAINS);
        addSpawnTo(biomes, Biomes.SUNFLOWER_PLAINS);
        // forest family too
        addSpawnTo(biomes, Biomes.FOREST);
        addSpawnTo(biomes, Biomes.BIRCH_FOREST);
        addSpawnTo(biomes, Biomes.FLOWER_FOREST);
        addSpawnTo(biomes, Biomes.DARK_FOREST);
    }

    private static void addSpawnTo(Registry<Biome> biomes, ResourceKey<Biome> key) {
        Biome biome = biomes.getOptional(key).orElse(null);
        if (biome == null) {
            return;
        }

        var entry = biome.getAttributes().get(EnvironmentAttributes.NATURAL_MOB_SPAWNS);
        MobSpawnSettings current = entry != null
                ? entry.applyModifier(EnvironmentAttributes.NATURAL_MOB_SPAWNS.defaultValue())
                : EnvironmentAttributes.NATURAL_MOB_SPAWNS.defaultValue();

        MobSpawnSettings.Builder builder = new MobSpawnSettings.Builder();
        for (MobCategory category : MobCategory.values()) {
            WeightedList<MobSpawnSettings.SpawnerData> existing = current.getMobsInCategory(category);
            if (existing != null) {
                builder.addAllSpawns(category, existing);
            }
        }

        builder.addSpawn(CommonBeasts.COMMON_SNAKE_BEAST, SPAWN_WEIGHT, MIN_GROUP, MAX_GROUP);
        builder.addAllCosts(current.allSpawnCosts());

        EnvironmentAttributeMap.Builder attributes = EnvironmentAttributeMap.builder().putAll(biome.getAttributes());
        attributes.set(EnvironmentAttributes.NATURAL_MOB_SPAWNS, builder.build());
        biome.attributes = attributes.build();

        // rebuilt biomes lose their pack source, leave that in or the freeze check trips
        if (biomes instanceof MappedRegistry<Biome> mapped) {
            RegistrationInfo info = mapped.registrationInfos.get(key);
            if (info != null) {
                mapped.registrationInfos.put(key, new RegistrationInfo(Optional.empty(), info.lifecycle()));
            }
        }
    }
}
