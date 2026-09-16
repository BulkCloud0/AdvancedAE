package net.pedroksl.advanced_ae.client.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        this(type, settings, enabled, currentValue, Collections.emptyList());
    }

    public UpgradeType type() {
        return this.type;
    }

    public UpgradeSettings settings() {
        return this.settings;
    }

    public boolean enabled() {
        return this.enabled;
    }

    public int currentValue() {
        return this.currentValue;
    }

    @Nullable
    public List<GenericStack> filter() {
        return this.filter;
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
        data.writeEnum(this.type);
        this.settings.toBytes(data);
        data.writeBoolean(this.enabled);
        data.writeInt(this.currentValue);

        int size = this.filter == null ? 0 : this.filter.size();
        data.writeInt(size);
        for (int i = 0; i < size; i++) {
            GenericStack.writeBuffer(this.filter.get(i), data);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UpgradeState)) return false;
        UpgradeState other = (UpgradeState) obj;
        return this.enabled == other.enabled
                && this.currentValue == other.currentValue
                && this.type == other.type
                && Objects.equals(this.settings, other.settings)
                && Objects.equals(this.filter, other.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.settings, this.enabled, this.currentValue, this.filter);
    }

    @Override
    public String toString() {
        return "UpgradeState[type=" + this.type
                + ", settings=" + this.settings
                + ", enabled=" + this.enabled
                + ", currentValue=" + this.currentValue
                + ", filter=" + this.filter + "]";
    }
}
