package net.pedroksl.advanced_ae.client.gui.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.pedroksl.advanced_ae.api.AAESetting;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.api.ShowQuantumCrafters;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

import appeng.api.config.YesNo;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.ITooltip;
import appeng.util.EnumCycler;

/**
 * AE2 8.x compatible toggle button for AdvancedAE-owned settings.
 */
public class AAESettingToggleButton<T extends Enum<T>> extends Button implements ITooltip {
    private static final Map<EnumPair, ButtonAppearance> APPEARANCES = new HashMap<>();

    private final AAESetting<T> buttonSetting;
    private final IHandler<AAESettingToggleButton<T>> onPress;
    private final EnumSet<T> validValues;
    private T currentValue;

    @FunctionalInterface
    public interface IHandler<B extends AAESettingToggleButton<?>> {
        void handle(B button, boolean backwards);
    }

    public AAESettingToggleButton(
            AAESetting<T> setting, T val, IHandler<AAESettingToggleButton<T>> onPress) {
        this(setting, val, t -> true, onPress);
    }

    public AAESettingToggleButton(
            AAESetting<T> setting,
            T val,
            Predicate<T> isValidValue,
            IHandler<AAESettingToggleButton<T>> onPress) {
        super(0, 0, 16, 16, StringTextComponent.EMPTY, AAESettingToggleButton::onPress);
        this.buttonSetting = setting;
        this.currentValue = val;
        this.onPress = onPress;

        EnumSet<T> validValues = EnumSet.allOf(val.getDeclaringClass());
        validValues.removeIf(isValidValue.negate());
        validValues.removeIf(value -> !setting.getValues().contains(value));
        this.validValues = validValues;

        EnumPair key = new EnumPair(setting, val);
        if (!APPEARANCES.containsKey(key)) {
            registerAppearances();
        }
        if (!APPEARANCES.containsKey(key)) {
            throw new IllegalArgumentException("No button appearance for setting " + setting.getName() + "=" + val);
        }
    }

    /**
     * Temporary client-side cycling factory used while the addon setting packet
     * is being backported away from ae2addonlib. The button no longer requires
     * the missing library; server synchronization is handled in the networking
     * port rather than hidden inside the widget.
     */
    public static <T extends Enum<T>> AAESettingToggleButton<T> serverButton(AAESetting<T> setting, T val) {
        return new AAESettingToggleButton<>(setting, val, (button, backwards) ->
                button.set(button.getNextValue(backwards)));
    }

    private static void onPress(Button button) {
        if (button instanceof AAESettingToggleButton) {
            ((AAESettingToggleButton<?>) button).triggerPress();
        }
    }

    private void triggerPress() {
        boolean backwards = false;
        Screen currentScreen = Minecraft.getInstance().currentScreen;
        if (currentScreen instanceof AEBaseScreen) {
            backwards = ((AEBaseScreen<?>) currentScreen).isHandlingRightClick();
        }
        onPress.handle(this, backwards);
    }

    private void registerAppearances() {
        registerApp(AAEIcon.ME_EXPORT_ON, AAESettings.ME_EXPORT, YesNo.YES, AAEText.MeExport, AAEText.MeExportOn);
        registerApp(AAEIcon.ME_EXPORT_OFF, AAESettings.ME_EXPORT, YesNo.NO, AAEText.MeExport, AAEText.MeExportOff);
        registerApp(
                AAEIcon.FILTERED_IMPORT_ON,
                AAESettings.FILTERED_IMPORT,
                YesNo.YES,
                AAEText.FilteredImport,
                AAEText.FilteredImportOn);
        registerApp(
                AAEIcon.FILTERED_IMPORT_OFF,
                AAESettings.FILTERED_IMPORT,
                YesNo.NO,
                AAEText.FilteredImport,
                AAEText.FilteredImportOff);
        registerApp(
                AAEIcon.CRAFTER_TERMINAL_VISIBLE,
                AAESettings.TERMINAL_SHOW_QUANTUM_CRAFTERS,
                ShowQuantumCrafters.VISIBLE,
                AAEText.ShowCraftersCategory,
                AAEText.ShowVisibleCrafters);
        registerApp(
                AAEIcon.CRAFTER_TERMINAL_ALL,
                AAESettings.TERMINAL_SHOW_QUANTUM_CRAFTERS,
                ShowQuantumCrafters.ALL,
                AAEText.ShowCraftersCategory,
                AAEText.ShowAllCrafters);
        registerApp(
                AAEIcon.CRAFTER_TERMINAL_NOT_FULL,
                AAESettings.TERMINAL_SHOW_QUANTUM_CRAFTERS,
                ShowQuantumCrafters.NOT_FULL,
                AAEText.ShowCraftersCategory,
                AAEText.ShowNonFullCrafters);
        registerApp(
                AAEIcon.SHOW_ON_CRAFTER_TERMINAL,
                AAESettings.QUANTUM_CRAFTER_TERMINAL,
                YesNo.YES,
                AAEText.CrafterTerminalSetting,
                AAEText.ShowOnCrafterTerminal);
        registerApp(
                AAEIcon.HIDE_ON_CRAFTER_TERMINAL,
                AAESettings.QUANTUM_CRAFTER_TERMINAL,
                YesNo.NO,
                AAEText.CrafterTerminalSetting,
                AAEText.HideOnCrafterTerminal);
        registerApp(
                AAEIcon.REGULATE_ON,
                AAESettings.REGULATE_STOCK,
                YesNo.YES,
                AAEText.RegulateCategory,
                AAEText.RegulateOn);
        registerApp(
                AAEIcon.REGULATE_OFF,
                AAESettings.REGULATE_STOCK,
                YesNo.NO,
                AAEText.RegulateCategory,
                AAEText.RegulateOff);
    }

    private static <E extends Enum<E>> void registerApp(
            AAEIcon icon, AAESetting<E> setting, E value, AAEText title, AAEText hint) {
        List<ITextComponent> tooltip = new ArrayList<>(2);
        tooltip.add(title.text());
        tooltip.add(hint.text());
        APPEARANCES.put(new EnumPair(setting, value), new ButtonAppearance(icon, tooltip));
    }

    private ButtonAppearance getAppearance() {
        return APPEARANCES.get(new EnumPair(buttonSetting, currentValue));
    }

    @Override
    public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }

        ButtonAppearance appearance = getAppearance();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(x, y).blit(matrixStack, getBlitOffset());
        if (appearance != null) {
            appearance.icon.getBlitter().dest(x, y).blit(matrixStack, getBlitOffset());
        }
        RenderSystem.enableDepthTest();
    }

    public AAESetting<T> getSetting() {
        return buttonSetting;
    }

    public T getCurrentValue() {
        return currentValue;
    }

    public void set(T value) {
        if (buttonSetting.isValidValue(value)) {
            currentValue = value;
        }
    }

    public T getNextValue(boolean backwards) {
        return EnumCycler.rotateEnum(currentValue, backwards, validValues);
    }

    @Override
    public List<ITextComponent> getTooltipMessage() {
        ButtonAppearance appearance = getAppearance();
        return appearance == null ? Collections.emptyList() : appearance.tooltip;
    }

    @Override
    public int getTooltipAreaX() {
        return x;
    }

    @Override
    public int getTooltipAreaY() {
        return y;
    }

    @Override
    public int getTooltipAreaWidth() {
        return width;
    }

    @Override
    public int getTooltipAreaHeight() {
        return height;
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return visible;
    }

    private static final class EnumPair {
        private final AAESetting<?> setting;
        private final Enum<?> value;

        private EnumPair(AAESetting<?> setting, Enum<?> value) {
            this.setting = setting;
            this.value = value;
        }

        @Override
        public int hashCode() {
            return setting.hashCode() ^ value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EnumPair)) {
                return false;
            }
            EnumPair other = (EnumPair) obj;
            return other.setting == setting && other.value == value;
        }
    }

    private static final class ButtonAppearance {
        private final AAEIcon icon;
        private final List<ITextComponent> tooltip;

        private ButtonAppearance(AAEIcon icon, List<ITextComponent> tooltip) {
            this.icon = icon;
            this.tooltip = tooltip;
        }
    }
}
