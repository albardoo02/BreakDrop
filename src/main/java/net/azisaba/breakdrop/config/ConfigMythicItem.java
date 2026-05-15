package net.azisaba.breakdrop.config;

import net.azisaba.breakdrop.util.ItemUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ConfigMythicItem implements ConfigItem {
    private final String mythicType;

    public ConfigMythicItem(@NotNull String mythicType) {
        this.mythicType = mythicType;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ConfigMythicItem load(@NotNull ConfigurationSection section) {
        String mythicType = Objects.requireNonNull(section.getString("mythic_type"), "mythic_type");
        return new ConfigMythicItem(mythicType);
    }

    @Contract(pure = true)
    public @NotNull String getMythicType() {
        return mythicType;
    }

    @Override
    public boolean check(@Nullable ItemStack item) {
        return getMythicType().equals(ItemUtil.getMythicType(item));
    }
}
