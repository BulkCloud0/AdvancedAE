package net.pedroksl.advanced_ae.common.items.upgrades;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.helpers.MagnetHelpers;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.armors.QuantumBoots;
import net.pedroksl.advanced_ae.common.items.armors.QuantumChestplate;
import net.pedroksl.advanced_ae.common.items.armors.QuantumHelmet;
import net.pedroksl.advanced_ae.common.items.armors.QuantumLeggings;
import net.pedroksl.advanced_ae.xmod.Addons;
import net.pedroksl.advanced_ae.xmod.appflux.AppliedFluxPlugin;
import net.pedroksl.advanced_ae.xmod.curios.CuriosPlugin;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

public class UpgradeCards {
    public static boolean walkSpeed(Level level, Player player, ItemStack stack) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        QuantumArmorBase chestArmor = chest.getItem() instanceof QuantumArmorBase ? (QuantumArmorBase) chest.getItem() : null;
        boolean canFly = chestArmor != null && chestArmor.isUpgradeEnabledAndPowered(chest, UpgradeType.FLIGHT);
        boolean isNotFlying = !player.getAbilities().flying;
        if (!player.isSprinting() && (canFly || isNotFlying) && !player.isInWaterOrBubble()) {
            UpgradeType upgrade = UpgradeType.WALK_SPEED;
            if (stack.getItem() instanceof QuantumLeggings) {
                QuantumLeggings legs = (QuantumLeggings) stack.getItem();
                if (legs.isUpgradeEnabledAndPowered(stack, upgrade)) {
                    return processMovementSpeed(upgrade, player, canFly, stack, chest);
                }
            } else if (canFly && player.getAbilities().flying) {
                return processFlightSpeed(player, chest);
            }
        }
        return false;
    }

    public static boolean sprintSpeed(Level level, Player player, ItemStack stack) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        QuantumArmorBase chestArmor = chest.getItem() instanceof QuantumArmorBase ? (QuantumArmorBase) chest.getItem() : null;
        boolean canFly = chestArmor != null && chestArmor.isUpgradeEnabledAndPowered(chest, UpgradeType.FLIGHT);
        boolean isNotFlying = player.fallDistance <= 0 && !player.isFallFlying();
        if (player.isSprinting() && (canFly || isNotFlying) && !player.isInWaterOrBubble()) {
            UpgradeType upgrade = UpgradeType.SPRINT_SPEED;
            if (stack.getItem() instanceof QuantumLeggings) {
                QuantumLeggings legs = (QuantumLeggings) stack.getItem();
                if (legs.isUpgradeEnabledAndPowered(stack, upgrade)) {
                    return processMovementSpeed(upgrade, player, canFly, stack, chest);
                }
            } else if (canFly && player.getAbilities().flying) {
                return processFlightSpeed(player, chest);
            }
        }
        return false;
    }

    public static boolean swimSpeed(Level level, Player player, ItemStack stack) {
        if (player.isInWaterOrBubble()) {
            UpgradeType upgrade = UpgradeType.SWIM_SPEED;
            if (stack.getItem() instanceof QuantumLeggings) {
                QuantumLeggings legs = (QuantumLeggings) stack.getItem();
                if (legs.isUpgradeEnabledAndPowered(stack, upgrade)) {
                    return processMovementSpeed(upgrade, player, false, stack, null);
                }
            }
        }
        return false;
    }

    private static boolean processMovementSpeed(
            UpgradeType upgrade, Player player, boolean canFly, ItemStack stack, ItemStack chest) {
        if (stack.getItem() instanceof QuantumLeggings) {
            QuantumLeggings legs = (QuantumLeggings) stack.getItem();
            boolean slowDown = true;
            float value = upgrade.getSettings().multiplier * legs.getUpgradeValue(stack, upgrade, 0);

            if (!(value > 0 && value < 1)) {
                slowDown = false;
                value /= 25f;
            }

            if (canFly && player.getAbilities().flying && chest != null && chest.getItem() instanceof QuantumChestplate) {
                QuantumChestplate armor = (QuantumChestplate) chest.getItem();
                float flight = armor.getUpgradeValue(chest, UpgradeType.FLIGHT, 0) / 25f;
                value = !slowDown ? value + flight : flight;
                slowDown = false;
            }

            if (slowDown && player.onGround()) {
                Vec3 motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.multiply(value, 1, value));
                return true;
            } else if (!slowDown && value > 0) {
                if (!player.onGround()) value /= 4f;
                if (player.zza < 0F) value /= 2f;
                player.moveRelative(value, new Vec3(Math.signum(player.xxa), Math.signum(player.yya), Math.signum(player.zza)));
                return true;
            }
        }
        return false;
    }

    private static boolean processFlightSpeed(Player player, ItemStack chest) {
        if (chest.getItem() instanceof QuantumArmorBase) {
            QuantumArmorBase armor = (QuantumArmorBase) chest.getItem();
            float value = armor.getUpgradeValue(chest, UpgradeType.FLIGHT, 0) / 25f;
            if (value > 0) {
                if (!player.onGround()) value /= 4f;
                if (player.zza < 0F) value /= 2f;
                player.moveRelative(value, new Vec3(Math.signum(player.xxa), Math.signum(player.yya), Math.signum(player.zza)));
                return true;
            }
        }
        return false;
    }

    public static boolean creativeFlight(Level level, Player player, ItemStack stack) {
        return creativeFlight(level, player, stack, true);
    }

    public static boolean creativeFlight(Level level, Player player, ItemStack stack, boolean equipped) {
        if (stack.getItem() instanceof QuantumChestplate) {
            QuantumChestplate chest = (QuantumChestplate) stack.getItem();
            UpgradeType upgrade = UpgradeType.FLIGHT;
            if (equipped && chest.isUpgradeEnabledAndPowered(stack, upgrade)) {
                if (!player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = true;
                    player.getPersistentData().putBoolean("aaeFlightCard", true);
                    player.onUpdateAbilities();
                    return true;
                }
            } else if (player.getAbilities().mayfly && player.getPersistentData().getBoolean("aaeFlightCard")) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.getPersistentData().remove("aaeFlightCard");
                player.onUpdateAbilities();
            }
        }
        return false;
    }

    public static boolean jumpHeight(Level level, Player player, ItemStack stack) {
        if (!player.isInWaterOrBubble() && !player.isFallFlying()) {
            UpgradeType upgrade = UpgradeType.JUMP_HEIGHT;
            if (stack.getItem() instanceof QuantumBoots) {
                QuantumBoots boots = (QuantumBoots) stack.getItem();
                if (boots.isUpgradeEnabledAndPowered(stack, upgrade)) {
                    float value = boots.getUpgradeValue(stack, UpgradeType.JUMP_HEIGHT, -1) / 8f;
                    if (value > 0) {
                        if (player.isSprinting()) value *= 2;
                        player.moveRelative(value, new Vec3(0, 1, 0));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean autoFeed(Level level, Player player, ItemStack stack) {
        if (!player.getFoodData().needsFood() || !(stack.getItem() instanceof QuantumHelmet)) {
            return false;
        }
        QuantumHelmet helmet = (QuantumHelmet) stack.getItem();
        if (!helmet.isUpgradeEnabledAndPowered(stack, UpgradeType.AUTO_FEED) || helmet.getLinkedPosition(stack) == null) {
            return false;
        }
        IGrid grid = helmet.getLinkedGrid(stack, level, player);
        if (grid != null) {
            for (GenericStack genStack : helmet.getFilter(stack, UpgradeType.AUTO_FEED)) {
                if (grid.getStorageService().getInventory().extract(genStack.what(), 1, Actionable.SIMULATE, IActionSource.ofPlayer(player)) > 0
                        && genStack.what() instanceof AEItemKey) {
                    AEItemKey itemKey = (AEItemKey) genStack.what();
                    ItemStack foodStack = itemKey.toStack();
                    if (foodStack.getFoodProperties(player) != null) {
                        grid.getStorageService().getInventory().extract(genStack.what(), 1, Actionable.MODULATE, IActionSource.ofPlayer(player));
                        foodStack = foodStack.finishUsingItem(level, player);
                        if (!foodStack.isEmpty()) {
                            grid.getStorageService().getInventory().insert(AEItemKey.of(foodStack), foodStack.getCount(), Actionable.MODULATE, IActionSource.ofPlayer(player));
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean autoStock(Level level, Player player, ItemStack stack) {
        if (!(stack.getItem() instanceof QuantumHelmet)) return false;
        QuantumHelmet helmet = (QuantumHelmet) stack.getItem();
        if (!helmet.isUpgradeEnabledAndPowered(stack, UpgradeType.AUTO_STOCK) || helmet.getLinkedPosition(stack) == null) return false;
        if (player.containerMenu != null && !player.containerMenu.getCarried().isEmpty()) return false;

        IGrid grid = helmet.getLinkedGrid(stack, level, player);
        if (grid == null) return false;
        boolean didSomething = false;
        for (GenericStack genStack : helmet.getFilter(stack, UpgradeType.AUTO_STOCK)) {
            if (!(genStack.what() instanceof AEItemKey)) continue;
            AEItemKey itemKey = (AEItemKey) genStack.what();
            long desiredAmount = genStack.amount();
            long currentAmount = 0;
            List<Integer> slots = new ArrayList<Integer>();
            for (int x = 0; x < player.getInventory().getContainerSize(); x++) {
                ItemStack currentStack = player.getInventory().getItem(x);
                if (itemKey.toStack().getItem() == currentStack.getItem()) {
                    currentAmount += currentStack.getCount();
                    slots.add(Integer.valueOf(x));
                }
            }
            long amountDelta = desiredAmount - currentAmount;
            if (amountDelta > 0 && grid.getStorageService().getInventory().getAvailableStacks().get(itemKey) > 0) {
                long extracted = grid.getStorageService().getInventory().extract(genStack.what(), amountDelta, Actionable.MODULATE, IActionSource.ofPlayer(player));
                ItemStack stackToInsert = itemKey.toStack((int) extracted);
                player.addItem(stackToInsert);
                grid.getStorageService().getInventory().insert(genStack.what(), stackToInsert.getCount(), Actionable.MODULATE, IActionSource.ofPlayer(player));
                didSomething |= extracted > 0;
            } else if (amountDelta < 0) {
                amountDelta = -amountDelta;
                long inserted = grid.getStorageService().getInventory().insert(genStack.what(), amountDelta, Actionable.MODULATE, IActionSource.ofPlayer(player));
                int amountToLeave = (int) desiredAmount + (int) (amountDelta - inserted);
                for (Integer slot : slots) {
                    ItemStack item = player.getInventory().getItem(slot.intValue()).copy();
                    int amountToSet = Math.max(0, Math.min(item.getCount(), amountToLeave));
                    item.setCount(amountToSet);
                    player.getInventory().setItem(slot.intValue(), item);
                    amountToLeave = Math.max(0, amountToLeave - amountToSet);
                }
                didSomething |= inserted > 0;
            }
        }
        return didSomething;
    }

    public static boolean magnet(Level level, Player player, ItemStack stack) {
        if (!player.isSpectator() && stack.getItem() instanceof QuantumHelmet) {
            QuantumHelmet helmet = (QuantumHelmet) stack.getItem();
            if (helmet.isUpgradeEnabledAndPowered(stack, UpgradeType.MAGNET)) {
                int range = helmet.getUpgradeValue(stack, UpgradeType.MAGNET, 5);
                Vec3 pos = player.position();
                AABB area = MagnetHelpers.getBoundingBox(pos, range);
                final List<GenericStack> filter = helmet.getFilter(stack, UpgradeType.MAGNET);
                final boolean blacklist = helmet.getUpgradeExtra(stack, UpgradeType.MAGNET, false);
                List<ItemEntity> items = level.getEntities(EntityType.ITEM, area,
                        obj -> MagnetHelpers.validEntities(obj, player, filter, blacklist));
                items.forEach(itemEntity -> {
                    if (!level.isClientSide() && player.getInventory().getSlotWithRemainingSpace(itemEntity.getItem()) != -1) {
                        itemEntity.playerTouch(player);
                    }
                    itemEntity.setPos(pos);
                });
                if (!level.isClientSide()) {
                    List<ExperienceOrb> xps = level.getEntitiesOfClass(ExperienceOrb.class, area);
                    xps.forEach(xp -> {
                        xp.invulnerableTime = 0;
                        player.takeXpDelay = 0;
                        xp.playerTouch(player);
                    });
                }
                return true;
            }
        }
        return false;
    }

    public static boolean regeneration(Level level, Player player, ItemStack stack) {
        if (stack.getItem() instanceof QuantumChestplate) {
            QuantumChestplate chest = (QuantumChestplate) stack.getItem();
            if (chest.isUpgradeEnabledAndPowered(stack, UpgradeType.REGENERATION)) {
                player.heal((float) (0.1 * AAEConfig.instance().getRenegerationPerTick()));
                return true;
            }
        }
        return false;
    }

    public static boolean recharging(Level level, Player player, ItemStack stack) {
        if (!(stack.getItem() instanceof QuantumArmorBase)) return false;
        QuantumArmorBase armor = (QuantumArmorBase) stack.getItem();
        if (!armor.isUpgradeEnabledAndPowered(stack, UpgradeType.CHARGING) || armor.getLinkedPosition(stack) == null) return false;
        IGrid grid = armor.getLinkedGrid(stack, level, player);
        if (grid == null) return false;
        IEnergyService energy = grid.getEnergyService();
        double currentPower = armor.getAECurrentPower(stack);
        double rate = armor.getChargeRate(stack);
        int afRate = Integer.MAX_VALUE;
        double maxPower = armor.getAEMaxPower(stack);
        double neededPower = Math.min(rate, maxPower - currentPower);

        if (neededPower > 0 && Addons.APPFLUX.isLoaded()) {
            neededPower = Math.min(afRate, maxPower - currentPower);
            neededPower = AppliedFluxPlugin.rechargeAeStorageItem(grid, neededPower, player, stack, armor);
        }
        if (neededPower > 0 && energy.getStoredPower() > 0) {
            double extracted = energy.extractAEPower(rate, Actionable.MODULATE, PowerMultiplier.CONFIG);
            double remainder = armor.injectAEPower(stack, extracted, Actionable.MODULATE);
            energy.injectPower(remainder, Actionable.MODULATE);
        }

        if (stack.getItem() instanceof QuantumChestplate) {
            for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (!item.isEmpty()) rechargeItem(player, item, grid, rate, energy);
            }
            if (!player.getOffhandItem().isEmpty()) rechargeItem(player, player.getOffhandItem(), grid, rate, energy);
            if (Addons.CURIOS.isLoaded()) {
                CuriosPlugin.getCuriosInventory(player).ifPresent(inv -> {
                    for (int i = 0; i < inv.getEquippedCurios().getSlots(); i++) {
                        ItemStack item = inv.getEquippedCurios().getStackInSlot(i);
                        if (!item.isEmpty()) rechargeItem(player, item, grid, rate, energy);
                    }
                });
            }
        }
        return false;
    }

    private static void rechargeItem(Player player, ItemStack stack, IGrid grid, double rate, IEnergyService energyService) {
        final int afRate = Integer.MAX_VALUE;
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
            if (cap.canReceive() && cap.getEnergyStored() < cap.getMaxEnergyStored()) {
                if (Addons.APPFLUX.isLoaded()) {
                    AppliedFluxPlugin.rechargeEnergyStorage(grid, afRate, IActionSource.ofPlayer(player), cap);
                }
                if (energyService.getStoredPower() > 0) {
                    double extracted = energyService.extractAEPower(rate, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    int inserted = cap.receiveEnergy((int) extracted, false);
                    energyService.injectPower(extracted - inserted, Actionable.MODULATE);
                }
            }
        });
    }
}
