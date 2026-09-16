package net.pedroksl.advanced_ae.common.items.armors;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pedroksl.advanced_ae.client.renderer.QuantumArmorRenderer;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.helpers.PickCraftMenuHost;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeCards;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.MenuSelectionPacket;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.networking.IGrid;
import appeng.api.parts.SelectedPart;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.ISubMenuHost;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.core.localization.Tooltips;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuLocators;
import appeng.menu.me.crafting.CraftAmountMenu;

import software.bernie.geckolib.animatable.GeoItem;

public class QuantumChestplate extends QuantumArmorBase implements GeoItem, ISubMenuHost {

    private static final double MAX_POWER_STORAGE = 300000000;

    public QuantumChestplate(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY, Type.CHESTPLATE, properties, () -> MAX_POWER_STORAGE);

        registerUpgrades(
                UpgradeType.FLIGHT,
                UpgradeType.HP_BUFFER,
                UpgradeType.LAVA_IMMUNITY,
                UpgradeType.REGENERATION,
                UpgradeType.STRENGTH,
                UpgradeType.ATTACK_SPEED,
                UpgradeType.CHARGING,
                UpgradeType.PICK_CRAFT);
    }

    @Override
    protected void appendExtraHoverText(
            ItemStack stack, @NotNull Level context, List<Component> lines, TooltipFlag advancedTooltips) {
        lines.add(AAEText.QuantumArmorStableFootingTooltip.text().withStyle(Tooltips.NUMBER_TEXT));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (slotId == EquipmentSlot.CHEST.getIndex()) {
                if (!getPassiveUpgrades(stack).isEmpty()) {
                    tickUpgrades(level, player, stack);
                }
                if (level.isClientSide()) {
                    toggleBoneVisibilities(stack, player);
                }
            } else {
                tickCreativeFlight(level, player, stack);
            }
        }
    }

    public void tickCreativeFlight(Level level, Player player, ItemStack stack) {
        if (getPassiveUpgrades(stack).contains(UpgradeType.FLIGHT)) {
            UpgradeCards.creativeFlight(level, player, stack, false);
            if (player.getAbilities().flying) {
                consumeEnergy(player, stack, UpgradeType.FLIGHT);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void toggleBoneVisibilities(ItemStack stack, Player player) {
        QuantumArmorBase item = (QuantumArmorBase) stack.getItem();
        HumanoidModel<?> renderer = IClientItemExtensions.of(item)
                .getHumanoidArmorModel(player, stack, EquipmentSlot.CHEST, null);
        if (renderer instanceof QuantumArmorRenderer) {
            boolean visible = item.hasUpgrade(stack, UpgradeType.STRENGTH);
            QuantumArmorRenderer quantumRenderer = (QuantumArmorRenderer) renderer;
            quantumRenderer.setBoneVisible(QuantumArmorRenderer.LEFT_BLADE_BONE, visible);
            quantumRenderer.setBoneVisible(QuantumArmorRenderer.RIGHT_BLADE_BONE, visible);
        }
    }

    @Override
    public boolean openFromEquipmentSlot(
            Player player, int inventorySlot, ItemStack stack, boolean returningFromSubmenu) {
        if (player instanceof ServerPlayer && checkPreconditions(stack)) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            player.getPersistentData().putInt(MENU_TYPE, MenuId.STANDARD.id);
            AAENetworkHandler.INSTANCE.sendTo(new MenuSelectionPacket(MENU_TYPE, MenuId.STANDARD.id), serverPlayer);
        }
        return super.openFromEquipmentSlot(player, inventorySlot, stack, returningFromSubmenu);
    }

    public boolean attemptCraftingTarget(Player player, int inventorySlot, ItemStack stack) {
        if (player instanceof ServerPlayer && checkPreconditions(stack)) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            QuantumChestplate chestStack = (QuantumChestplate) stack.getItem();
            UpgradeType upgrade = UpgradeType.PICK_CRAFT;
            if (chestStack.hasUpgrade(stack, upgrade)) {
                if (chestStack.isUpgradeEnabled(stack, upgrade)) {
                    AEKey key = targetKey(player);
                    if (key != null) {
                        if (keyIsCraftable(player, stack, key)) {
                            player.getPersistentData().putInt(MENU_TYPE, MenuId.CRAFTING.id);
                            AAENetworkHandler.INSTANCE.sendTo(
                                    new MenuSelectionPacket(MENU_TYPE, MenuId.CRAFTING.id), serverPlayer);
                            CraftAmountMenu.open(serverPlayer, MenuLocators.forInventorySlot(inventorySlot), key, 1);
                        }
                    } else {
                        player.displayClientMessage(AAEText.NoAvailableTarget.text(), true);
                    }
                } else {
                    Component id = Component.translatable(upgrade.item().asItem().getDescriptionId());
                    player.displayClientMessage(AAEText.UpgradeNotEnabledMessage.text(id), true);
                }
            } else {
                Component id = Component.translatable(upgrade.item().asItem().getDescriptionId());
                player.displayClientMessage(AAEText.UpgradeNotInstalledMessage.text(id), true);
            }
        }
        return false;
    }

    @Override
    public ItemMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack, @Nullable BlockPos pos) {
        if (player.getPersistentData().contains(MENU_TYPE)
                && player.getPersistentData().getInt(MENU_TYPE) == MenuId.STANDARD.id) {
            player.getPersistentData().remove(MENU_TYPE);
            return super.getMenuHost(player, inventorySlot, stack, pos);
        }
        player.getPersistentData().remove(MENU_TYPE);
        return new PickCraftMenuHost<>(player, inventorySlot, stack);
    }

    private AEKey targetKey(Player player) {
        HitResult hitResult = player.pick(player.getBlockReach(), 0f, false);
        if (hitResult instanceof BlockHitResult && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();
            BlockState blockState = player.level().getBlockState(blockPos);
            ItemStack itemStack = blockState.getBlock().asItem().getDefaultInstance();
            if (blockState.hasBlockEntity()) {
                BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
                if (blockEntity instanceof CableBusBlockEntity) {
                    SelectedPart part = getSelectedPart((CableBusBlockEntity) blockEntity, hitResult);
                    if (part != null && part.part != null) {
                        itemStack = new ItemStack(part.part.getPartItem().asItem(), 1);
                    }
                } else if (blockEntity != null) {
                    blockEntity.saveToItem(itemStack);
                }
            }
            return AEItemKey.of(itemStack.getItem().getDefaultInstance());
        }
        return null;
    }

    private static SelectedPart getSelectedPart(CableBusBlockEntity cable, HitResult hitResult) {
        Vec3 loc = hitResult.getLocation();
        double x = loc.x - (int) loc.x;
        if (x < 0) x++;
        double y = loc.y - (int) loc.y;
        if (y < 0) y++;
        double z = loc.z - (int) loc.z;
        if (z < 0) z++;
        return cable.selectPartLocal(new Vec3(x, y, z));
    }

    public boolean keyIsCraftable(Player player, ItemStack stack, AEKey whatToCraft) {
        IGrid grid = this.getLinkedGrid(stack, player.level(), player);
        if (grid != null) {
            if (grid.getCraftingService().isCraftable(whatToCraft)) {
                return true;
            }
            player.displayClientMessage(AAEText.ItemNotCraftable.text(), true);
        }
        return false;
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {}

    @Override
    public ItemStack getMainMenuIcon() {
        return AAEItems.QUANTUM_CHESTPLATE.stack();
    }
}
