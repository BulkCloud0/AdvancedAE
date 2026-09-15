package net.pedroksl.advanced_ae.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import appeng.api.config.YesNo;

/**
 * AdvancedAE-owned settings for AE2 8.x.
 *
 * AE2 8.4.7 uses a closed enum for its built-in settings, so addon-specific
 * settings are kept in this small local registry instead of ae2addonlib.
 */
public final class AAESettings {
    private static final Map<String, AAESetting<?>> SETTINGS = new LinkedHashMap<>();

    public static final AAESetting<YesNo> ME_EXPORT = register("me_export", YesNo.YES, YesNo.NO);
    public static final AAESetting<YesNo> FILTERED_IMPORT = register("filtered_import", YesNo.YES, YesNo.NO);
    public static final AAESetting<YesNo> QUANTUM_CRAFTER_TERMINAL =
            register("quantum_crafter_terminal", YesNo.YES, YesNo.NO);
    public static final AAESetting<ShowQuantumCrafters> TERMINAL_SHOW_QUANTUM_CRAFTERS =
            register("show_quantum_crafters", ShowQuantumCrafters.class);
    public static final AAESetting<YesNo> REGULATE_STOCK = register("regulate_stock", YesNo.YES, YesNo.NO);

    private AAESettings() {
    }

    @SafeVarargs
    private static synchronized <T extends Enum<T>> AAESetting<T> register(
            String name, T firstOption, T... moreOptions) {
        AAESetting<T> setting = new AAESetting<>(name, firstOption, moreOptions);
        SETTINGS.put(name, setting);
        return setting;
    }

    private static synchronized <T extends Enum<T>> AAESetting<T> register(String name, Class<T> enumClass) {
        AAESetting<T> setting = new AAESetting<>(name, enumClass);
        SETTINGS.put(name, setting);
        return setting;
    }

    public static AAESetting<?> getOrThrow(String name) {
        AAESetting<?> setting = SETTINGS.get(name);
        if (setting == null) {
            throw new IllegalArgumentException("Unknown AdvancedAE setting: " + name);
        }
        return setting;
    }

    public static Map<String, AAESetting<?>> all() {
        return Collections.unmodifiableMap(SETTINGS);
    }
}
