package mods.railcraft.world.item;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import com.mojang.authlib.GameProfile;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.plugins.PlayerPlugin;
import mods.railcraft.util.inventory.InvTools;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemTicket extends Item {

  public static final Predicate<ItemStack> FILTER =
      stack -> stack != null && stack.getItem() instanceof ItemTicket;
  public static final int LINE_LENGTH = 32;

  public ItemTicket(Properties properties) {
    super(properties);
  }

  public boolean validateNBT(CompoundNBT nbt) {
    String dest = nbt.getString("dest");
    return dest.length() < LINE_LENGTH;
  }

  // @Override
  // public String getItemDisplayName(ItemStack stack) {
  // String dest = getDestination(stack);
  //
  // if (!dest.equals("")) {
  // return super.getItemDisplayName(stack) + " - " + dest.substring(dest.lastIndexOf("/") + 1);
  // }
  //
  // return super.getItemDisplayName(stack);
  // }

  /**
   * allows items to add custom lines of information to the mouse over description
   */
  @Override
  public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list,
      ITooltipFlag par4) {
    if (stack.hasTag()) {
      GameProfile owner = getOwner(stack);
      if (owner.getId() != null) {
        list.add(new TranslationTextComponent("gui.railcraft.routing.ticket.tips.issuer"));
        list.add(PlayerPlugin.getUsername(world, owner).copy().withStyle(TextFormatting.GRAY));
      }

      String dest = getDestination(stack);
      if (!"".equals(dest)) {
        list.add(new TranslationTextComponent("gui.railcraft.routing.ticket.tips.dest"));
        list.add(new StringTextComponent(dest).withStyle(TextFormatting.GRAY));
      }
    } else
      list.add(new TranslationTextComponent("gui.railcraft.routing.ticket.tips.blank"));
  }

  public static boolean isNBTValid(@Nullable CompoundNBT nbt) {
    if (nbt == null)
      return false;
    else if (!nbt.contains("dest", Constants.NBT.TAG_STRING))
      return false;

    String dest = nbt.getString("dest");
    return !dest.isEmpty() && dest.length() <= LINE_LENGTH;
  }

  public static ItemStack copyTicket(ItemStack source) {
    if (InvTools.isEmpty(source))
      return ItemStack.EMPTY;
    if (source.getItem() instanceof ItemTicket) {
      ItemStack ticket = RailcraftItems.TICKET.get().getDefaultInstance();
      if (InvTools.isEmpty(ticket))
        return ItemStack.EMPTY;
      CompoundNBT nbt = source.getTag();
      if (nbt != null)
        ticket.setTag(nbt.copy());
      return ticket;
    }
    return ItemStack.EMPTY;
  }

  public static boolean setTicketData(ItemStack ticket, String dest, String title,
      @Nullable GameProfile owner) {
    if (InvTools.isEmpty(ticket) || !(ticket.getItem() instanceof ItemTicket))
      return false;
    if (dest.length() > LINE_LENGTH)
      return false;
    if (owner == null)
      return false;
    CompoundNBT data = InvTools.getItemData(ticket);
    data.putString("dest", dest);
    data.putString("title", title);
    PlayerPlugin.writeOwnerToNBT(data, owner);
    return true;
  }

  public static String getDestination(ItemStack ticket) {
    if (InvTools.isEmpty(ticket) || !(ticket.getItem() instanceof ItemTicket))
      return "";
    CompoundNBT nbt = ticket.getTag();
    if (nbt == null)
      return "";
    return nbt.getString("dest");
  }

  public static boolean matchesOwnerOrOp(ItemStack ticket, GameProfile player) {
    return ticket.getItem() instanceof ItemTicket
        && PlayerPlugin.isOwnerOrOp(getOwner(ticket), player);
  }

  public static GameProfile getOwner(ItemStack ticket) {
    if (InvTools.isEmpty(ticket) || !(ticket.getItem() instanceof ItemTicket))
      return new GameProfile(null, RailcraftConstantsAPI.UNKNOWN_PLAYER);
    CompoundNBT nbt = ticket.getTag();
    if (nbt == null)
      return new GameProfile(null, RailcraftConstantsAPI.UNKNOWN_PLAYER);
    return PlayerPlugin.readOwnerFromNBT(nbt);
  }
}