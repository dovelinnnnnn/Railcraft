package mods.railcraft.util.container;

import java.util.function.Predicate;
import java.util.stream.IntStream;
import org.jetbrains.annotations.Nullable;
import mods.railcraft.api.core.CompoundTagKeys;
import mods.railcraft.api.item.Filter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class ContainerTools {

  public static int[] buildSlotArray(int start, int size) {
    return IntStream.range(0, size).map(i -> start + i).toArray();
  }

  public static ItemStack depleteItem(ItemStack stack) {
    if (stack.getCount() == 1)
      return stack.getItem().getCraftingRemainingItem(stack);
    else {
      stack.split(1);
      return stack;
    }
  }

  public static void dropIfInvalid(Level level, BlockPos blockPos, Container container, int index) {
    drop(level, blockPos, container, index, item -> container.canPlaceItem(index, item));
  }

  public static void drop(Level level, BlockPos blockPos, Container container, int index,
      Predicate<ItemStack> predicate) {
    var item = container.getItem(index);
    if (!item.isEmpty() && !predicate.test(item)) {
      container.setItem(index, ItemStack.EMPTY);
      Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), item);
    }
  }

  public static boolean matchesFilter(ItemStack filter, ItemStack stack) {
    if (stack.isEmpty() || filter.isEmpty())
      return false;
    if (filter.getItem() instanceof Filter filterItem) {
      return filterItem.matches(filter, stack);
    }
    return ItemStack.isSameItem(stack, filter);
  }

  public static ListTag writeContainer(Container container, HolderLookup.Provider provider) {
    var tag = new ListTag();
    for (byte i = 0; i < container.getContainerSize(); i++) {
      var itemStack = container.getItem(i);
      if (!itemStack.isEmpty()) {
        var slotTag = new CompoundTag();
        slotTag.putByte(CompoundTagKeys.INDEX, i);
        tag.add(itemStack.save(provider, slotTag));
      }
    }
    return tag;
  }

  public static void readContainer(Container container, ListTag tag, HolderLookup.Provider provider) {
    for (byte i = 0; i < tag.size(); i++) {
      var slotTag = tag.getCompound(i);
      int slot = slotTag.getByte(CompoundTagKeys.INDEX);
      if (slot >= 0 && slot < container.getContainerSize()) {
        ItemStack.parse(provider, slotTag)
            .ifPresent(itemStack -> container.setItem(slot, itemStack));
      }
    }
  }

  public static boolean isItemStackBlock(ItemStack itemStack, Block block) {
    return !itemStack.isEmpty()
        && itemStack.getItem() instanceof BlockItem item
        && item.getBlock() == block;
  }

  public static Block getBlockFromStack(ItemStack stack) {
    if (stack.isEmpty())
      return Blocks.AIR;
    var item = stack.getItem();
    return item instanceof BlockItem blockItem ? blockItem.getBlock() : Blocks.AIR;
  }

  public static BlockState getBlockStateFromStack(ItemStack stack) {
    if (stack.isEmpty())
      return Blocks.AIR.defaultBlockState();
    return getBlockFromStack(stack).defaultBlockState();
  }

  @Nullable
  public static BlockState getBlockStateFromStack(ItemStack stack, Level level,
      BlockPos pos) {
    if (stack.isEmpty()) {
      return null;
    }
    if (stack.getItem() instanceof BlockItem blockItem) {
      return blockItem.getBlock().getStateForPlacement(
          new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND, stack,
              new BlockHitResult(new Vec3(0.5D, 0.5D, 0.5D), Direction.UP, pos.above(), false)));
    }
    return null;
  }

  /**
   * Checks if a stack can have more items filled in.
   *
   * <p>
   * Callers: Be warned that you need to check slot stack limit as well!
   *
   * @param stack the stack to check
   * @return whether the stack needs filling
   */
  public static boolean isStackFull(ItemStack stack) {
    return !stack.isEmpty() && stack.getCount() == stack.getMaxStackSize();
  }
}
