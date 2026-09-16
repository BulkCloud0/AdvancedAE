package net.pedroksl.advanced_ae.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.guisync.GuiSync;
import appeng.menu.locator.MenuLocator;

public class QuantumArmorNumInputConfigMenu extends AEBaseMenu implements ISubMenu {

    private final ISubMenuHost host;

    @GuiSync(7)
    public UpgradeType type;

    @GuiSync(9)
    public int currentValue;

    public int slotIndex;

    private static final String SET_CURRENT_VALUE = "set_current_value";

    public QuantumArmorNumInputConfigMenu(int id, Inventory playerInventory, ISubMenuHost host) {
        super(AAEMenus.QUANTUM_ARMOR_NUM_INPUT.get(), id, playerInventory, host);
        this.host = host;
        registerClientAction(SET_CURRENT_VALUE, Integer.class, this::setCurrentValue);
    }

    @Override
    public ISubMenuHost getHost() {
        return host;
    }

    public static void open(
            ServerPlayer player, MenuLocator locator, int slotIndex, UpgradeType type, int currentValue) {
        MenuOpener.open(AAEMenus.QUANTUM_ARMOR_NUM_INPUT.get(), player, locator);

        if (player.containerMenu instanceof QuantumArmorNumInputConfigMenu) {
            QuantumArmorNumInputConfigMenu cca = (QuantumArmorNumInputConfigMenu) player.containerMenu;
            cca.setUpgradeType(type);
            cca.setSlotIndex(slotIndex);
            cca.setCurrentValue(currentValue);
            cca.broadcastChanges();
        }
    }

    public void setSlotIndex(int index) {
        this.slotIndex = index;
    }

    public void setUpgradeType(UpgradeType type) {
        this.type = type;
    }

    public void setCurrentValue(int value) {
        if (isClientSide()) {
            sendClientAction(SET_CURRENT_VALUE, value);
            return;
        }

        this.currentValue = value;
        ItemStack stack = getPlayer().getInventory().getItem(this.slotIndex);
        if (stack.getItem() instanceof QuantumArmorBase) {
            QuantumArmorBase item = (QuantumArmorBase) stack.getItem();
            if (item.getPossibleUpgrades().contains(this.type) && item.hasUpgrade(stack, this.type)) {
                item.setUpgradeValue(stack, this.type, value);
            }
        }
    }
}
