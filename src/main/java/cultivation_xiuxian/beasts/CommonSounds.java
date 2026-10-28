package cultivation_xiuxian.beasts;

import cultivation_xiuxian.CultivationXiuxian;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class CommonSounds {
    public static final SoundEvent SNAKE_HURT = register("entity.common_snake_beast.hurt");
    public static final SoundEvent SNAKE_ALARM = register("entity.common_snake_beast.alarm");
    public static final SoundEvent SNAKE_DEATH = register("entity.common_snake_beast.death");
    public static final SoundEvent SNAKE_SLITHER = register("entity.common_snake_beast.slither");
    public static final SoundEvent SNAKE_LAY = register("entity.common_snake_beast.lay");

    private static SoundEvent register(String path) {
        Identifier id = Identifier.fromNamespaceAndPath(CultivationXiuxian.MOD_ID, path);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id,
                SoundEvent.createVariableRangeEvent(id));
    }

    public static void init() {
    }
}
