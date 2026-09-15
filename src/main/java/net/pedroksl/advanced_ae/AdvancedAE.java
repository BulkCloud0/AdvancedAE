package net.pedroksl.advanced_ae;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.pedroksl.advanced_ae.common.definitions.*;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;

public class AdvancedAE {
    public static final String MOD_ID = "advanced_ae";

    static AdvancedAE INSTANCE;

    public AdvancedAE() {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
        INSTANCE = this;

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AAEConfig.register(MOD_ID);
        AAEItems.INSTANCE.register(eventBus);
        AAEBlocks.INSTANCE.register(eventBus);
        AAEBlockEntities.INSTANCE.register(eventBus);
        AAEFluids.INSTANCE.register(eventBus);
        AAECreativeTab.INSTANCE.register(eventBus);

        eventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        eventBus.addListener(AdvancedAE::initUpgrades);

        AAENbt.init();
    }

    public static AdvancedAE instance() {
        return INSTANCE;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Runtime services are re-enabled as their AE2 8 adapters land.
    }

    private static void initUpgrades(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Upgrades.add(AEItems.SPEED_CARD, AAEBlocks.QUANTUM_CRAFTER, 4);
            Upgrades.add(AEItems.REDSTONE_CARD, AAEBlocks.QUANTUM_CRAFTER, 1);
            Upgrades.add(AEItems.SPEED_CARD, AAEItems.STOCK_EXPORT_BUS, 4);
            Upgrades.add(AEItems.CAPACITY_CARD, AAEItems.STOCK_EXPORT_BUS, 5);
            Upgrades.add(AEItems.REDSTONE_CARD, AAEItems.STOCK_EXPORT_BUS, 1);
            Upgrades.add(AEItems.CRAFTING_CARD, AAEItems.STOCK_EXPORT_BUS, 1);
            Upgrades.add(AEItems.FUZZY_CARD, AAEItems.STOCK_EXPORT_BUS, 1);
            Upgrades.add(AEItems.SPEED_CARD, AAEItems.IMPORT_EXPORT_BUS, 4);
            Upgrades.add(AEItems.CAPACITY_CARD, AAEItems.IMPORT_EXPORT_BUS, 5);
            Upgrades.add(AEItems.REDSTONE_CARD, AAEItems.IMPORT_EXPORT_BUS, 1);
            Upgrades.add(AEItems.CRAFTING_CARD, AAEItems.IMPORT_EXPORT_BUS, 1);
            Upgrades.add(AEItems.FUZZY_CARD, AAEItems.IMPORT_EXPORT_BUS, 1);
            Upgrades.add(AEItems.SPEED_CARD, AAEItems.ADVANCED_IO_BUS, 4);
            Upgrades.add(AEItems.CAPACITY_CARD, AAEItems.ADVANCED_IO_BUS, 5);
            Upgrades.add(AEItems.REDSTONE_CARD, AAEItems.ADVANCED_IO_BUS, 1);
            Upgrades.add(AEItems.CRAFTING_CARD, AAEItems.ADVANCED_IO_BUS, 1);
            Upgrades.add(AEItems.FUZZY_CARD, AAEItems.ADVANCED_IO_BUS, 1);
        });
    }

    public static ResourceLocation makeId(String id) {
        return new ResourceLocation(MOD_ID, id);
    }
}
