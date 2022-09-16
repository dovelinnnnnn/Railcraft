package mods.railcraft.world.item;

import java.util.List;
import mods.railcraft.Translations.Tips;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class OverallsArmor extends ArmorItem {

  public OverallsArmor(Properties properties) {
    super(RailcraftArmorMaterial.OVERALLS, EquipmentSlot.LEGS, properties);
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level,
      List<Component> tooltipComponents, TooltipFlag isAdvanced) {
    super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    tooltipComponents.add(Component.translatable(Tips.OVERALLS).withStyle(ChatFormatting.GRAY));
  }
}
