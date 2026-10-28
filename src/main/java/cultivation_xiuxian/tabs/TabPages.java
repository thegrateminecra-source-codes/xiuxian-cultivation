package cultivation_xiuxian.tabs;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.world.item.CreativeModeTab;


public final class TabPages {
    private static final Map<CreativeModeTab, Integer> PAGES = new IdentityHashMap<>();
    public static final boolean FABRIC_TAB_API_PRESENT = detectFabric();

    private TabPages() {
    }

    private static boolean detectFabric() {
        try {
            Class.forName("net.fabricmc.fabric.impl.creativetab.FabricCreativeModeTabImpl", false,
                    TabPages.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void assign(CreativeModeTab tab, int page) {
        PAGES.put(tab, page);
    }

    public static int pageOf(CreativeModeTab tab) {
        return PAGES.getOrDefault(tab, 0);
    }

    public static int pageCount() {
        int max = 0;
        for (int p : PAGES.values()) {
            max = Math.max(max, p);
        }
        return max + 1;
    }
}
