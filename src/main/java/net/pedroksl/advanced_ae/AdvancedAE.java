package net.pedroksl.advanced_ae;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.pedroksl.advanced_ae.common.definitions.AAEBlockEntities;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAENbt;

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
        eventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        AAENbt.init();
    }

    public static AdvancedAE instance() {
        return INSTANCE;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Upgrade registration and optional integrations are restored after the core baseline compiles.
    }

    public static ResourceLocation makeId(String id) {
        return new ResourceLocation(MOD_ID, id);
    }
}
