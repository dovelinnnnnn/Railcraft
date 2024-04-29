package mods.railcraft.world.item.enchantment;

import mods.railcraft.api.item.Crowbar;
import mods.railcraft.tags.RailcraftTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class DestructionEnchantment extends Enchantment {

  /*public DestructionEnchantment(Rarity rarity, EquipmentSlot... slots) {
    super(rarity, RailcraftEnchantmentCategories.RAILWAY_TOOL, slots);
  }*/

  public DestructionEnchantment(EnchantmentDefinition definition) {
    super(definition);
  }

  @Override
  public boolean canEnchant(ItemStack stack) {
    return stack.getItem() instanceof Crowbar;
  }
}
