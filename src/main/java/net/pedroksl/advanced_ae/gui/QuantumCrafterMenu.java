package net.pedroksl.advanced_ae.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.PatternsUpdatePacket;
import net.pedroksl.ae2addonlib.gui.OutputDirectionMenu;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.inventories.InternalInventory;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.locator.MenuLocator;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.RestrictedInputSlot;

public class QuantumCrafterMenu extends UpgradeableMenu<QuantumCrafterEntity> {

    @GuiSync(2)
    public YesNo meExport = YesNo.YES;

    @GuiSync(3)
    public YesNo showOnTerminal = YesNo.YES;

    public List<Boolean> invalidPatterns = new ArrayList<Boolean>();
    public List<Boolean> enabledPatterns = new ArrayList<Boolean>();

    private static final String CONFIGURE_OUTPUT = "configureOutput";
    private static final String CONFIG_PATTERN = "configPattern";
    private static final String TOGGLE_ENABLE_PATTERN = "toggleEnablePattern";

    private final Slot[] patternSlots = new Slot[9];

    public QuantumCrafterMenu(int id, Inventory ip, QuantumCrafterEntity host) {
        super(AAEMenus.QUANTUM_CRAFTER.get(), id, ip, host);

        InternalInventory patterns = host.getPatternInventory();
        for (int x = 0; x < patterns.size(); x++) {
            this.patternSlots[x] = this.addSlot(
                    new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN, patterns, x),
                    SlotSemantics.MACHINE_INPUT);
        }

        InternalInventory outputs = host.getOutputInv();
        for (int x = 0; x < outputs.size(); x++) {
            this.addSlot(new AppEngSlot(outputs, x), SlotSemantics.MACHINE_OUTPUT);
        }

        setInvalidPatterns(host.getInvalidPatternSlots());
        setEnabledPatterns(host.getEnabledPatternSlots());

        registerClientAction(CONFIGURE_OUTPUT, this::configureOutput);
        registerClientAction(CONFIG_PATTERN, Integer.class, this::configPattern);
        registerClientAction(TOGGLE_ENABLE_PATTERN, Integer.class, this::toggleEnablePattern);
    }

    protected void loadSettingsFromHost(IConfigManager cm) {
        this.showOnTerminal = this.getHost().getConfigManager().getSetting(AAESettings.QUANTUM_CRAFTER_TERMINAL);
        this.meExport = this.getHost().getConfigManager().getSetting(AAESettings.ME_EXPORT);
        this.setRedStoneMode(this.getHost().getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED));
    }

    public YesNo getShowOnTerminal() {
        return showOnTerminal;
    }

    public YesNo getMeExport() {
        return meExport;
    }

    public void setInvalidPatterns(List<Boolean> invalidPatterns) {
        setInvalidPatterns(invalidPatterns, true);
    }

    public void setInvalidPatterns(List<Boolean> invalidPatterns, boolean broadcastUpdate) {
        this.invalidPatterns = new ArrayList<Boolean>(invalidPatterns);

        if (broadcastUpdate) {
            broadcastChanges();
        }
    }

    public void setEnabledPatterns(List<Boolean> enabledPatterns) {
        this.enabledPatterns = new ArrayList<Boolean>(enabledPatterns);
        broadcastChanges();
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (isServerSide() && getPlayer() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) getPlayer();
            setInvalidPatterns(getHost().getInvalidPatternSlots(), false);
            AAENetworkHandler.INSTANCE.sendTo(
                    new PatternsUpdatePacket(this.invalidPatterns, this.enabledPatterns), player);
        }
    }

    public void configureOutput() {
        if (isClientSide()) {
            sendClientAction(CONFIGURE_OUTPUT);
            return;
        }

        MenuLocator locator = getLocator();
        if (locator != null && isServerSide()) {
            OutputDirectionMenu.open(
                    (ServerPlayer) this.getPlayer(),
                    locator,
                    this.getHost().getAllowedOutputs());
        }
    }

    public void configPattern(int index) {
        if (isClientSide()) {
            sendClientAction(CONFIG_PATTERN, index);
            return;
        }

        MenuLocator locator = getLocator();
        if (locator != null && isServerSide()) {
            if (this.getHost().getPatternConfigInputs(index) == null
                    || this.getHost().getPatternConfigOutput(index) == null) {
                return;
            }

            QuantumCrafterConfigPatternMenu.open(
                    (ServerPlayer) this.getPlayer(),
                    locator,
                    getHost(),
                    index,
                    this.getHost().getPatternConfigInputs(index),
                    this.getHost().getPatternConfigOutput(index));
        }
    }

    public void toggleInvalidPattern(int index, boolean invalid) {
        this.invalidPatterns.set(index, invalid);
    }

    public void toggleEnablePattern(int index) {
        if (isClientSide()) {
            sendClientAction(TOGGLE_ENABLE_PATTERN, index);
            return;
        }

        this.getHost().toggleEnablePattern(index);
        setEnabledPatterns(this.getHost().getEnabledPatternSlots());
    }

    @Override
    public boolean isValidForSlot(Slot s, ItemStack is) {
        for (Slot ps : this.patternSlots) {
            if (s == ps) {
                return AEItems.CRAFTING_PATTERN.isSameAs(is);
            }
        }

        return super.isValidForSlot(s, is);
    }
}
