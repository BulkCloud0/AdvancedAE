package net.pedroksl.advanced_ae.common.definitions;

/** NBT keys shared by the 1.16.5 baseline. Upgrade-specific maps are restored with Quantum Armor. */
public final class AAENbt {
    public static final String STACK_TAG = "generic_nbt";
    public static final String TINT_COLOR_TAG = "tint_color";
    public static final String PORTABLE_CELL_STACK_TAG = "portable_cell_stack";
    public static final String NIGHT_VISION_ACTIVATED = "night_vision_on";
    public static final String UPGRADE_TOGGLE = "enabled";
    public static final String UPGRADE_VALUE = "value";
    public static final String UPGRADE_FILTER = "filter";
    public static final String UPGRADE_EXTRA = "extra";

    private AAENbt() {}

    public static void init() {
        // Upgrade metadata is deferred together with the Quantum Armor subsystem.
    }
}
