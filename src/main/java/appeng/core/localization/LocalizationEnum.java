package appeng.core.localization;

import net.minecraft.util.text.TranslationTextComponent;

/**
 * Small source-compatibility bridge for localization enums used by newer AE2
 * addon code. AE2 8.4.x predates this helper, while Minecraft 1.16.5 already
 * provides TranslationTextComponent with the behavior AdvancedAE needs.
 */
public interface LocalizationEnum {

    String getEnglishText();

    String getTranslationKey();

    default TranslationTextComponent text(Object... args) {
        return new TranslationTextComponent(getTranslationKey(), args);
    }
}
