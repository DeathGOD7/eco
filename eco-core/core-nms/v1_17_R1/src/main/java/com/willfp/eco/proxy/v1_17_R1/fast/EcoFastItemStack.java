package com.willfp.eco.proxy.v1_17_R1.fast;

import com.willfp.eco.core.fast.FastItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EcoFastItemStack implements FastItemStack {
    private final ItemStack handle;
    private final boolean isCIS;

    public EcoFastItemStack(@NotNull final org.bukkit.inventory.ItemStack itemStack) {
        this.handle = FastItemStackUtils.getNMSStack(itemStack);
        this.isCIS = itemStack instanceof CraftItemStack;
    }

    @Override
    public Map<Enchantment, Integer> getEnchantmentsOnItem(final boolean checkStored) {
        ListTag enchantmentNBT = checkStored && handle.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(handle) : handle.getEnchantmentTags();
        Map<Enchantment, Integer> foundEnchantments = new HashMap<>();

        for (Tag base : enchantmentNBT) {
            CompoundTag compound = (CompoundTag) base;
            String key = compound.getString("id");
            int level = '\uffff' & compound.getShort("lvl");

            Enchantment found = Enchantment.getByKey(CraftNamespacedKey.fromStringOrNull(key));
            if (found != null) {
                foundEnchantments.put(found, level);
            }
        }
        return foundEnchantments;
    }

    @Override
    public int getLevelOnItem(@NotNull final Enchantment enchantment,
                              final boolean checkStored) {
        ListTag enchantmentNBT = checkStored && handle.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(handle) : handle.getEnchantmentTags();

        for (Tag base : enchantmentNBT) {
            CompoundTag compound = (CompoundTag) base;
            String key = compound.getString("id");
            if (!key.equals(enchantment.getKey().toString())) {
                continue;
            }

            return '\uffff' & compound.getShort("lvl");
        }
        return 0;
    }

    @Override
    public org.bukkit.inventory.ItemStack unwrap() {
        return CraftItemStack.asCraftMirror(handle);
    }

    @Override
    public boolean isModifyingInstance() {
        return isCIS;
    }
}
