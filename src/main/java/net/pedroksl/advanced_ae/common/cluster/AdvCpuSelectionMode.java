package net.pedroksl.advanced_ae.common.cluster;

/**
 * Selection policy for Quantum Computer crafting CPUs.
 *
 * AE2 8.4.7 does not expose the CpuSelectionMode enum that newer AE2 versions
 * provide, so AdvancedAE keeps this behavior as an addon-owned setting.
 */
public enum AdvCpuSelectionMode {
    ANY,
    PLAYER_ONLY,
    MACHINE_ONLY;

    public static AdvCpuSelectionMode fromName(String name) {
        if (name != null) {
            try {
                return valueOf(name);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return ANY;
    }
}
