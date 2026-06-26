package net.azisaba.breakdrop;

import net.azisaba.breakdrop.config.PluginConfig;
import net.azisaba.breakdrop.listener.BreakListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BreakDrop extends JavaPlugin {
    private PluginConfig config;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.config = PluginConfig.load(this.getConfig());
        Bukkit.getPluginManager().registerEvents(new BreakListener(this), this);
    }

    @Contract(pure = true)
    public @NotNull PluginConfig getPluginConfig() {
        return config;
    }
}
