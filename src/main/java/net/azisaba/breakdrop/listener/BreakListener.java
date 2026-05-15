package net.azisaba.breakdrop.listener;

import com.gmail.nossr50.mcMMO;
import net.azisaba.breakdrop.BreakDrop;
import net.azisaba.breakdrop.config.ConfigDrop;
import net.azisaba.breakdrop.config.ConfigDropFunction;
import net.azisaba.breakdrop.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BreakListener implements Listener {
    private static final Set<String> EXCLUDED_LIFE_ITEM_ID = Set.of("56fabea9-e1f9-4e7f-ae78-83e07e8b8767");
    private final BreakDrop plugin;

    public BreakListener(@NotNull BreakDrop plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent e) {
        var mainLifeItemId = ItemUtil.getStringTag(e.getPlayer().getInventory().getItemInMainHand(), "LifeItemId");
        var offLifeItemId = ItemUtil.getStringTag(e.getPlayer().getInventory().getItemInOffHand(), "LifeItemId");
        if (EXCLUDED_LIFE_ITEM_ID.contains(mainLifeItemId) || EXCLUDED_LIFE_ITEM_ID.contains(offLifeItemId)) return;

        if (Bukkit.getPluginManager().isPluginEnabled("mcMMO") && checkPlaceStore(e.getBlock().getState())) {
            return;
        }

        for (ConfigDrop drop : plugin.getPluginConfig().getDrops()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                if (!drop.canExecute(e.getPlayer(), e.getBlock())) {
                    return;
                }
                for (ConfigDropFunction function : drop.getFunctions()) {
                    if (!function.canExecute(e.getPlayer(), e.getBlock())) {
                        continue;
                    }
                    for (int i = 0; i < function.getCount(); i++) {
                        if (!function.rollChance()) {
                            continue;
                        }
                        Bukkit.getScheduler().runTask(plugin, () -> function.execute(e.getPlayer(), e.getBlock()));
                    }
                }
            });
        }
    }

    @SuppressWarnings("removal")
    private static boolean checkPlaceStore(BlockState blockState) {
        return mcMMO.getPlaceStore().isTrue(blockState);
    }
}
