package net.pedroksl.advanced_ae.common.blocks;

import net.minecraft.item.Item;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.ae2addonlib.registry.helpers.LibBlockDefinition;

public enum AAECraftingUnitType {
    QUANTUM_UNIT(0, "quantum_unit"),
    QUANTUM_CORE(256, "quantum_core"),
    STORAGE_128M(128, "quantum_storage_128"),
    STORAGE_256M(256, "quantum_storage_256"),
    STORAGE_MULTIPLIER(0, "data_entangler"),
    QUANTUM_ACCELERATOR(0, "quantum_accelerator"),
    MULTI_THREADER(0, "quantum_multi_threader"),
    STRUCTURE(0, "quantum_structure");

    private final int storageMb;
    private final String affix;

    AAECraftingUnitType(int storageMb, String affix) {
        this.storageMb = storageMb;
        this.affix = affix;
    }

    public long getStorageBytes() {
        return 1024L * 1024L * storageMb;
    }

    public int getStorageMultiplier() {
        return this == STORAGE_MULTIPLIER
                ? AAEConfig.instance().getQuantumComputerDataEntanglerMultiplication()
                : 0;
    }

    public int getAcceleratorThreads() {
        switch (this) {
            case QUANTUM_ACCELERATOR:
            case QUANTUM_CORE:
                return AAEConfig.instance().getQuantumComputerAcceleratorThreads();
            default:
                return 0;
        }
    }

    public int getAccelerationMultiplier() {
        return this == MULTI_THREADER
                ? AAEConfig.instance().getQuantumComputerMultiThreaderMultiplication()
                : 0;
    }

    public String getAffix() {
        return this.affix;
    }

    public LibBlockDefinition<?> getDefinition() {
        switch (this) {
            case QUANTUM_UNIT:
                return AAEBlocks.QUANTUM_UNIT;
            case QUANTUM_CORE:
                return AAEBlocks.QUANTUM_CORE;
            case STORAGE_128M:
                return AAEBlocks.QUANTUM_STORAGE_128M;
            case STORAGE_256M:
                return AAEBlocks.QUANTUM_STORAGE_256M;
            case STORAGE_MULTIPLIER:
                return AAEBlocks.DATA_ENTANGLER;
            case QUANTUM_ACCELERATOR:
                return AAEBlocks.QUANTUM_ACCELERATOR;
            case MULTI_THREADER:
                return AAEBlocks.QUANTUM_MULTI_THREADER;
            case STRUCTURE:
                return AAEBlocks.QUANTUM_STRUCTURE;
            default:
                throw new IllegalStateException("Unhandled crafting unit type: " + this);
        }
    }

    public Item getItemFromType() {
        return getDefinition().block().asItem();
    }
}
