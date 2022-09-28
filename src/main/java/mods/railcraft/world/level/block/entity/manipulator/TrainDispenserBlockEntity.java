package mods.railcraft.world.level.block.entity.manipulator;

import java.util.function.Predicate;
import mods.railcraft.RailcraftConfig;
import mods.railcraft.api.carts.CartUtil;
import mods.railcraft.api.item.MinecartFactory;
import mods.railcraft.util.EntitySearcher;
import mods.railcraft.util.container.AdvancedContainer;
import mods.railcraft.util.container.ContainerManifest;
import mods.railcraft.world.entity.vehicle.CartTools;
import mods.railcraft.world.inventory.TrainDispenserMenu;
import mods.railcraft.world.item.CartItem;
import mods.railcraft.world.level.block.entity.RailcraftBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TrainDispenserBlockEntity extends CartDispenserBlockEntity {

  private static final int PATTERN_SIZE = 9;
  private static final int BUFFER_SIZE = 18;
  private final AdvancedContainer invPattern =
      new AdvancedContainer(PATTERN_SIZE).listener((Container) this).phantom();
  private byte patternIndex;
  private boolean spawningTrain;
  private @Nullable AbstractMinecart lastCart;

  public TrainDispenserBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(RailcraftBlockEntityTypes.TRAIN_DISPENSER.get(), blockPos, blockState);
    this.setContainerSize(BUFFER_SIZE);
  }

  public static void serverTick(Level level, BlockPos blockPos, BlockState blockState,
      TrainDispenserBlockEntity blockEntity) {
    if (blockEntity.spawningTrain && blockEntity.timeSinceLastSpawn % 4 == 0) {
      blockEntity.spawnNextCart((ServerLevel) level);
    }
    blockEntity.timeSinceLastSpawn++;
  }

  private boolean spawnNextCart(ServerLevel serverLevel) {
    ItemStack spawn = this.invPattern.getItem(patternIndex);
    if (spawn.isEmpty()) {
      this.resetSpawnSequence();
      return false;
    }
    Predicate<ItemStack> filter = new MinecartItemType(spawn);
    if (countItems(filter) == 0) {
      this.resetSpawnSequence();
      return false;
    }
    var offset = this.getBlockPos().offset(this.getFacing().getNormal());
    if (EntitySearcher.findMinecarts().around(offset).search(serverLevel).isEmpty()) {
      ItemStack cartItem = removeOneItem(filter);
      if (!cartItem.isEmpty()) {
        AbstractMinecart placedCart = CartTools.placeCart(cartItem, serverLevel, offset);

        if (placedCart != null) {
          if (this.lastCart != null) {
            CartUtil.linkageManager().createLink(placedCart, lastCart);
          }
          this.lastCart = placedCart;
          this.patternIndex++;
          if (this.patternIndex >= this.invPattern.getContainerSize()) {
            this.resetSpawnSequence();
          }
          return true;
        } else {
          this.addStack(cartItem);
        }
      }
    }
    return false;
  }

  private void resetSpawnSequence() {
    this.patternIndex = 0;
    this.spawningTrain = false;
    this.timeSinceLastSpawn = 0;
    this.lastCart = null;
  }

  @Override
  protected void onPulse(ServerLevel serverLevel) {
    var cart = EntitySearcher.findMinecarts()
        .around(this.getBlockPos().offset(this.getFacing().getNormal()))
        .search(serverLevel).any();
    if (cart != null) {
      return;
    }
    if (!spawningTrain && this.canBuildTrain()) {
      if (timeSinceLastSpawn > RailcraftConfig.server.cartDispenserDelay.get() * 20) {
        spawningTrain = true;
      }
    }
  }

  private boolean canBuildTrain() {
    var pattern = ContainerManifest.create(this.invPattern);
    var buffer = ContainerManifest.create(this.getContainer(), pattern.keySet());

    return pattern.values().stream().anyMatch(e -> buffer.count(e.key()) >= e.count());
  }

  public AdvancedContainer getInvPattern() {
    return invPattern;
  }

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    tag.put("trainDispenserFilters", this.invPattern.createTag());
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    this.invPattern.fromTag(tag.getList("trainDispenserFilters", Tag.TAG_COMPOUND));
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
    return new TrainDispenserMenu(id, inventory, this);
  }

  private static class MinecartItemType implements Predicate<ItemStack> {

    private final ItemStack original;

    public MinecartItemType(ItemStack cart) {
      original = cart;
    }

    @Override
    public boolean test(ItemStack itemStack) {
      if (itemStack.isEmpty()) {
        return false;
      }
      var item = itemStack.getItem();
      if (item instanceof MinecartItem ||
          item instanceof MinecartFactory ||
          item instanceof CartItem) {
        return this.original.is(item);
      }
      return false;
    }
  }
}
