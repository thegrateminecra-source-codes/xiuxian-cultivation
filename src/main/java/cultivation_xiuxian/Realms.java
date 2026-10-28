package cultivation_xiuxian;

public final class Realms {
    public static final String[] NAMES = {
            "Mortal", // The base Relm
            "Qi Condensation", 
            "Foundation Establishment", 
            "Golden Core",
            "Nascent Soul",
            "Soul Formation",
            "Void Refinement",
            "Itinerant Immortal", 
            "True Immortal",
            "True Venerated",
            "Venerated Immortal",
            "Dao Immortal", // im not 100% about these as i didnt have a live memory so i asked gemini ai to give me the list after but all before i can confrim 100 % correct
            "Dao Venerable",
            "Dao God",
            "Dao Ancestor",
            "Human Ancestor",
            "Earth Ancestor"
    };

    public static final String[] KEYS = {
            "mortal",
            "qi_condensation",
            "foundation_establishment",
            "golden_core",
            "nascent_soul",
            "soul_formation",
            "void_refinement",
            "itinerant_immortal",
            "true_immortal",
            "true_venerated",
            "venerated_immortal",
            "dao_immortal",
            "dao_venerable",
            "dao_god",
            "dao_ancestor",
            "human_ancestor",
            "earth_ancestor"
    };

    public static final int[] COLORS = {
            0xFFB0AFA6, // Mortal                      grey
            0xFF7FE3D2, // Qi Condensation             cyan
            0xFF6FCF97, // Foundation Establishment   green
            0xFFF2C94C, // Golden Core                gold
            0xFFBB86FC, // Nascent Soul               violet
            0xFFE57FD2, // Soul Formation             orchid
            0xFF9BAAB2, // Void Refinement            mist grey
            0xFF56CCF2, // Itinerant Immortal         sky blue
            0xFF7CB9FF, // True Immortal              true blue
            0xFF6EE7B7, // True Venerated             spring green
            0xFFB9E870, // Venerated Immortal         jade
            0xFFE8C170, // Dao Immortal               dawn gold
            0xFFF2925A, // Dao Venerable              ember
            0xFFEB5757, // Dao God                    blood red
            0xFFC9A9FF, // Dao Ancestor               divine violet
            0xFFF5D9A8, // Human Ancestor             warm ivory
            0xFFE0B84A  // Earth Ancestor             deep gold
    };

    public static final String[] STAGE_NAMES = {"Early", "Middle", "Late", "Peak"};

    public static final int REALM_COUNT = NAMES.length;
    public static final int STAGES_PER_REALM = STAGE_NAMES.length;

    public static String displayName(int realmIdx, int stageIdx) {
        return NAMES[clampRealm(realmIdx)] + " [" + STAGE_NAMES[clampStage(stageIdx)] + "]";
    }

    public static int color(int realmIdx) {
        return COLORS[clampRealm(realmIdx)];
    }

    public static int realmFromKey(String input) {
        String normalized = input.trim().toLowerCase().replace(' ', '_');
        for (int i = 0; i < KEYS.length; i++) {
            if (KEYS[i].equals(normalized)) {
                return i;
            }
        }
        try {
            int numeric = Integer.parseInt(normalized);
            if (numeric >= 0 && numeric < REALM_COUNT) {
                return numeric;
            }
        } catch (NumberFormatException ignored) {
        }
        return -1;
    }

    private static int clampRealm(int i) {
        return Math.max(0, Math.min(REALM_COUNT - 1, i));
    }

    private static int clampStage(int i) {
        return Math.max(0, Math.min(STAGES_PER_REALM - 1, i));
    }

    private Realms() {
    }
}
