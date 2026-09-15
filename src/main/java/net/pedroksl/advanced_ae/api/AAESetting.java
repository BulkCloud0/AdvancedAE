package net.pedroksl.advanced_ae.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Minimal addon-owned setting descriptor for AE2 8.x.
 *
 * AE2 8.4.7 exposes its built-in settings through the closed {@code Settings}
 * enum and therefore cannot register addon settings. Keeping the descriptor
 * local lets AdvancedAE retain its own setting names and enum values without
 * depending on ae2addonlib's newer Setting API.
 */
public final class AAESetting<T extends Enum<T>> {
    private final String name;
    private final List<T> values;

    @SafeVarargs
    public AAESetting(String name, T firstValue, T... moreValues) {
        this.name = name;
        List<T> values = new ArrayList<>();
        values.add(firstValue);
        Collections.addAll(values, moreValues);
        this.values = Collections.unmodifiableList(values);
    }

    public AAESetting(String name, Class<T> enumClass) {
        this.name = name;
        T[] constants = enumClass.getEnumConstants();
        List<T> values = new ArrayList<>(constants.length);
        Collections.addAll(values, constants);
        this.values = Collections.unmodifiableList(values);
    }

    public String getName() {
        return name;
    }

    public List<T> getValues() {
        return values;
    }

    public boolean isValidValue(T value) {
        return values.contains(value);
    }
}
