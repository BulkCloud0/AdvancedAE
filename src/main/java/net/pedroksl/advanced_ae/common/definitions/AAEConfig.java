package net.pedroksl.advanced_ae.common.definitions;

/**
 * Minimal 1.16.5 baseline configuration.
 *
 * The modern configuration wrapper depends on a newer AE2AddonLib API. Keep the
 * Quantum Computer defaults stable while the ForgeConfigSpec-backed UI is ported.
 */
public final class AAEConfig {
    private static AAEConfig INSTANCE = new AAEConfig();

    private AAEConfig() {}

    public static void register(String modId) {
        INSTANCE = new AAEConfig();
    }

    public static AAEConfig instance() {
        return INSTANCE;
    }

    public boolean getEnableEffects() {
        return true;
    }

    public int getQuantumComputerMaxSize() {
        return 7;
    }

    public int getQuantumComputerAcceleratorThreads() {
        return 8;
    }

    public int getQuantumComputerMaxMultiThreaders() {
        return 1;
    }

    public int getQuantumComputermaxDataEntanglers() {
        return 1;
    }

    public int getQuantumComputerMultiThreaderMultiplication() {
        return 4;
    }

    public int getQuantumComputerDataEntanglerMultiplication() {
        return 4;
    }

    public void save() {
        // Deferred until the Forge 1.16.5 config UI/backing spec is restored.
    }
}
