package net.azisaba.breakdrop.util;

import io.lumine.mythic.bukkit.MythicBukkit;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemUtil {
    public static @Nullable CompoundTag getCustomData(@Nullable ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        CustomData customData = nmsStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return null;
        return customData.copyTag();
    }

    @Contract("null -> null")
    public static @Nullable String getMythicType(@Nullable ItemStack stack) {
        if(stack == null || stack.getType().isAir()) return null;
        return MythicBukkit.inst().getItemManager().getMythicTypeFromItem(stack);
    }

    @Contract("null, _ -> null")
    public static @Nullable String getStringTag(@Nullable ItemStack stack, @NotNull String key) {
        CompoundTag tag = getCustomData(stack);
        if (tag == null) return null;
        return tag.getString(key).orElse(null);
    }
}
