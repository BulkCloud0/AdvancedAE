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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.pedroksl.advanced_ae.common.definitions.AAEBlockEntities;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.entities.SmallAdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.parts.SmallAdvPatternProviderPart;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.AEBasePart;

public class AdvPatternProviderCapacityUpgradeItem extends BlockUpgradeItem {

    public AdvPatternProviderCapacityUpgradeItem(Properties properties) {
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
            Class<? extends BlockEntity> tClazz = entity.getClass();
            if (tClazz == SmallAdvPatternProviderEntity.class) {
                BlockState originState = world.getBlockState(pos);
                BlockState state = AAEBlocks.ADV_PATTERN_PROVIDER.block().getStateForPlacement(ctx);
                if (state == null) {
                    return InteractionResult.PASS;
                }
                for (Map.Entry<Property<?>, Comparable<?>> sp : originState.getValues().entrySet()) {
                    Property pt = sp.getKey();
                    Comparable va = sp.getValue();
                    try {
                        if (state.hasProperty(pt)) {
                            state = state.<Comparable, Comparable>setValue(pt, va);
                        }
                    } catch (Exception ignore) {
                        // NO-OP
                    }
                }
                BlockEntity te = AAEBlockEntities.ADV_PATTERN_PROVIDER.get().create(pos, state);
                replaceTile(world, pos, entity, te, state);
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;

            } else if (entity instanceof CableBusBlockEntity) {
                CableBusBlockEntity cable = (CableBusBlockEntity) entity;
                Vec3 hitVec = context.getClickLocation();
                Vec3 hitInBlock = new Vec3(hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
                IPart part = cable.getCableBus().selectPartLocal(hitInBlock).part;
                if (part instanceof AEBasePart && part.getClass() == SmallAdvPatternProviderPart.class) {
                    AEBasePart basePart = (AEBasePart) part;
                    Direction side = basePart.getSide();
                    CompoundTag contents = new CompoundTag();
                    IPartItem<?> partItem = AAEItems.ADV_PATTERN_PROVIDER.get();

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
                .append(AAEText.PatternProviderCapacityUpgrade.text().withStyle(AAEText.TOOLTIP_DEFAULT_COLOR)));
    }
}
