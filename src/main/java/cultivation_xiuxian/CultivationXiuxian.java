package cultivation_xiuxian;

import net.fabricmc.api.ModInitializer;
import cultivation_xiuxian.beasts.CommonBeasts;
import cultivation_xiuxian.blocks.CultivationBlocks;
import cultivation_xiuxian.items.CultivationItems;

public class CultivationXiuxian implements ModInitializer {
    public static final String MOD_ID = "cultivation_xiuxian";

    @Override
    public void onInitialize() {
        CommonBeasts.init();
        CultivationBlocks.init();
        CultivationItems.init();
    }
}
