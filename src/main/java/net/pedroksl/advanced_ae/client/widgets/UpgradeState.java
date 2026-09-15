package net.pedroksl.advanced_ae.client.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeSettings;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.api.stacks.GenericStack;

public final class UpgradeState {
    private final UpgradeType type;
    private final UpgradeSettings settings;
    private final boolean enabled;
    private final int currentValue;
    @Nullable
    private final List<GenericStack> filter;

    public UpgradeState(
            UpgradeType type,
            UpgradeSettings settings,
            boolean enabled,
            int currentValue,
            @Nullable List<GenericStack> filter) {
        this.type = type;
        this.settings = settings;
        this.enabled = enabled;
        this.currentValue = currentValue;
        this.filter = filter;
    }

    public UpgradeState(UpgradeType type, UpgradeSettings settings, boolean enabled, int currentValue) {
        this(type, settings, enabled, currentValue, Collections.<GenericStack>emptyList());
    }

    public UpgradeType type() {
        return type;
    }

    public UpgradeSettings settings() {
        return settings;
    }

    public boolean enabled() {
        return enabled;
    }

    public int currentValue() {
        return currentValue;
    }

    @Nullable
    public List<GenericStack> filter() {
        return filter;
    }

    public static UpgradeState fromBytes(FriendlyByteBuf stream) {
        UpgradeType type = stream.readEnum(UpgradeType.class);
        UpgradeSettings settings = UpgradeSettings.fromBytes(stream);
        boolean enabled = stream.readBoolean();
        int currentValue = stream.readInt();

        int size = stream.readInt();
        List<GenericStack> filter = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            filter.add(GenericStack.readBuffer(stream));
        }

        return new UpgradeState(type, settings, enabled, currentValue, filter);
    }

    public void toBytes(FriendlyByteBuf data) {
        data.writeEnum(type);
        settings.toBytes(data);
        data.writeBoolean(enabled);
        data.writeInt(currentValue);

        if (filter != null) {
            data.writeInt(filter.size());
            for (int i = 0; i < filter.size(); i++) {
                GenericStack.writeBuffer(filter.get(i), data);
            }
        }
    }
}
