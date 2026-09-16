package net.pedroksl.advanced_ae.common.items;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.pedroksl.advanced_ae.common.definitions.AAEBlockEntities;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.xmod.Addons;
import net.pedroksl.advanced_ae.xmod.eae.ExtendedAEPlugin;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.AEBasePart;
import appeng.parts.crafting.PatternProviderPart;

public class AdvPatternProviderUpgradeItem extends BlockUpgradeItem {

    public AdvPatternProviderUpgradeItem(Properties properties) {
        super(properties);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity != null) {
            BlockPlaceContext ctx = new BlockPlaceContext(context);
            Class<?> tClazz = entity.getClass();
            if (tClazz == PatternProviderBlockEntity.class
                    || (Addons.EXPATTERNPROVIDER.isLoaded() && ExtendedAEPlugin.isEntityProvider(tClazz))) {

                BlockState originState = world.getBlockState(pos);
                boolean isSmall = tClazz == PatternProviderBlockEntity.class;

                BlockState state = isSmall
                        ? AAEBlocks.SMALL_ADV_PATTERN_PROVIDER.block().getStateForPlacement(ctx)
                        : AAEBlocks.ADV_PATTERN_PROVIDER.block().getStateForPlacement(ctx);
                if (state == null) {
                    return InteractionResult.PASS;
                }
                for (Map.Entry<Property<?>, Comparable<?>> sp : originState.getValues().entrySet()) {
                    Property pt = sp.getKey();
                    Comparable va = sp.getValue();
                    try {
                        if (state.hasProperty(pt)) {
                            state = state.setValue(pt, va);
                        }
                    } catch (Exception ignore) {
                        // NO-OP
                    }
                }

                BlockEntityType<?> tileType = isSmall
                        ? AAEBlockEntities.SMALL_ADV_PATTERN_PROVIDER.get()
                        : AAEBlockEntities.ADV_PATTERN_PROVIDER.get();
                BlockEntity te = tileType.create(pos, state);
                replaceTile(world, pos, entity, te, state);
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;

            } else if (entity instanceof CableBusBlockEntity) {
                CableBusBlockEntity cable = (CableBusBlockEntity) entity;
                Vec3 hitVec = context.getClickLocation();
                Vec3 hitInBlock = new Vec3(hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
                IPart part = cable.getCableBus().selectPartLocal(hitInBlock).part;
                if (part instanceof AEBasePart
                        && (part.getClass() == PatternProviderPart.class
                                || (Addons.EXPATTERNPROVIDER.isLoaded()
                                        && ExtendedAEPlugin.isPartProvider(part.getClass())))) {
                    AEBasePart basePart = (AEBasePart) part;
                    Direction side = basePart.getSide();
                    CompoundTag contents = new CompoundTag();
                    boolean isSmall = part.getClass() == PatternProviderPart.class;
                    IPartItem<?> partItem = (IPartItem<?>) (isSmall
                            ? AAEItems.SMALL_ADV_PATTERN_PROVIDER.get()
                            : AAEItems.ADV_PATTERN_PROVIDER.get());

                    part.writeToNBT(contents);
                    IPart p = cable.replacePart(partItem, side, context.getPlayer(), null);
                    if (p != null) {
                        p.readFromNBT(contents);
                    }
                } else {
                    return InteractionResult.PASS;
                }
                context.getItemInHand().shrink(1);
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void appendHoverText(
            ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        pTooltipComponents.add(Component.empty()
                .append(AAEText.PatternProviderUpgrade.text().withStyle(AAEText.TOOLTIP_DEFAULT_COLOR)));
    }
}
