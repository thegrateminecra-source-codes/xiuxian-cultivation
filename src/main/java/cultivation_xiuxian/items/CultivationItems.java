package cultivation_xiuxian.items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import cultivation_xiuxian.CultivationXiuxian;
import cultivation_xiuxian.tabs.TabPages;

import cultivation_xiuxian.beasts.CommonBeasts;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
// - this file registers all of the items.
public class CultivationItems {
    public static final List<Item> ALL = new ArrayList<>();

    public static final Item SHED_SNAKE_SKIN = register("shed_snake_skin");

    public static final Item COMMON_SNAKE_SPAWN_EGG = register("common_snake_spawn_egg",
            p -> p.spawnEgg(CommonBeasts.COMMON_SNAKE_BEAST));

    public static final ResourceKey<CreativeModeTab> MAIN_TAB_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(CultivationXiuxian.MOD_ID, "main"));

   
    public static final CreativeModeTab MAIN_TAB = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + CultivationXiuxian.MOD_ID + ".main"))
            .icon(() -> new ItemStack(SHED_SNAKE_SKIN))
            .displayItems((parameters, output) -> ALL.forEach(item -> output.accept(item)))
            .build();

    static {
        TabPages.assign(MAIN_TAB, 1);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MAIN_TAB_KEY, MAIN_TAB);
    }

    public static void init() {
    }

    private static Item register(String name) {
        return register(name, p -> p);
    }

    private static Item register(String name, Function<Item.Properties, Item.Properties> props) {
        ResourceKey<Item> key = ResourceKey.create(
                Registries.ITEM, Identifier.fromNamespaceAndPath(CultivationXiuxian.MOD_ID, name));
        Item item = new Item(props.apply(new Item.Properties().setId(key)));
        ALL.add(item);
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }
}
