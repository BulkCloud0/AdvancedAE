package net.pedroksl.advanced_ae.common.items.upgrades;

import net.pedroksl.advanced_ae.AdvancedAE;

/**
 * Lightweight 1.16.5 metadata for Quantum Armor upgrade cards.
 * Runtime abilities are restored separately after the item layer is stable.
 */
public enum UpgradeType {
    EMPTY("Empty", SettingType.NONE, 0, ApplicationType.PASSIVE),
    WALK_SPEED("Walk Speed", SettingType.NUM_INPUT, 10, ApplicationType.PASSIVE),
    SPRINT_SPEED("Sprint Speed", SettingType.NUM_INPUT, 10, ApplicationType.PASSIVE),
    STEP_ASSIST("Step Assist", SettingType.NUM_INPUT, 5, ApplicationType.PASSIVE_USE),
    JUMP_HEIGHT("Jump Height", SettingType.NUM_INPUT, 10, ApplicationType.PASSIVE_USE),
    LAVA_IMMUNITY("Lava Immunity", SettingType.NONE, 10, ApplicationType.PASSIVE_USE),
    FLIGHT("Flight", SettingType.NUM_INPUT, 10, ApplicationType.PASSIVE),
    WATER_BREATHING("Water Breathing", SettingType.NONE, 10, ApplicationType.PASSIVE_USE),
    AUTO_FEED("Auto Feed", SettingType.FILTER, 5, ApplicationType.PASSIVE),
    AUTO_STOCK("Auto Stock", SettingType.FILTER, 5, ApplicationType.PASSIVE),
    MAGNET("Magnet", SettingType.NUM_AND_FILTER, 5, ApplicationType.PASSIVE, ExtraSettings.BLACKLIST),
    HP_BUFFER("HP Buffer", SettingType.NONE, 10, ApplicationType.BUFF),
    EVASION("Evasion", SettingType.NONE, 10, ApplicationType.BUFF),
    REGENERATION("Regeneration", SettingType.NONE, 10, ApplicationType.PASSIVE),
    STRENGTH("Strength", SettingType.NONE, 10, ApplicationType.BUFF),
    ATTACK_SPEED("Attack Speed", SettingType.NONE, 10, ApplicationType.BUFF),
    LUCK("Luck Boost", SettingType.NONE, 10, ApplicationType.BUFF),
    REACH("Reach Boost", SettingType.NUM_INPUT, 10, ApplicationType.BUFF),
    SWIM_SPEED("Swim Speed", SettingType.NUM_INPUT, 5, ApplicationType.PASSIVE),
    NIGHT_VISION("Night Vision", SettingType.NONE, 10, ApplicationType.BUFF),
    FLIGHT_DRIFT("No Flight Drift", SettingType.NUM_INPUT, 10, ApplicationType.BUFF),
    CHARGING("ME Recharging", SettingType.NONE, 0, ApplicationType.PASSIVE),
    WORKBENCH("Portable Workbench", SettingType.NONE, 0, ApplicationType.PASSIVE_USE),
    PICK_CRAFT("Pick-Craft", SettingType.NONE, 1000, ApplicationType.PASSIVE_USE);

    public enum SettingType {
        NONE,
        NUM_INPUT,
        FILTER,
        NUM_AND_FILTER,
        BOOL_LIST
    }

    public enum ApplicationType {
        PASSIVE,
        PASSIVE_USE,
        BUFF
    }

    public enum ExtraSettings {
        NONE,
        BLACKLIST
    }

    public final String name;
    private final SettingType settingType;
    private final int cost;
    private final ApplicationType applicationType;
    private final ExtraSettings extra;

    UpgradeType(String name, SettingType settingType, int cost, ApplicationType applicationType) {
        this(name, settingType, cost, applicationType, ExtraSettings.NONE);
    }

    UpgradeType(
            String name,
            SettingType settingType,
            int cost,
            ApplicationType applicationType,
            ExtraSettings extra) {
        this.name = name;
        this.settingType = settingType;
        this.cost = cost;
        this.applicationType = applicationType;
        this.extra = extra;
    }

    public SettingType getSettingType() {
        return settingType;
    }

    public int getCost() {
        return cost;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public ExtraSettings getExtraSettings() {
        return extra;
    }

    public String getTranslationKey() {
        return "gui.upgrades." + AdvancedAE.MOD_ID + "." + name.replaceAll("\\s+", "") + "Upgrade";
    }
}
