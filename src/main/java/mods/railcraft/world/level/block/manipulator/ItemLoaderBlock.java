package mods.railcraft.world.level.block.manipulator;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import com.mojang.serialization.MapCodec;
import mods.railcraft.Translations;
import mods.railcraft.world.level.block.entity.RailcraftBlockEntityTypes;
import mods.railcraft.world.level.block.entity.manipulator.ItemLoaderBlockEntity;
import mods.railcraft.world.level.block.entity.manipulator.ManipulatorBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ItemLoaderBlock extends ManipulatorBlock<ItemLoaderBlockEntity> {

  private static final MapCodec<ItemLoaderBlock> CODEC = simpleCodec(ItemLoaderBlock::new);

  public ItemLoaderBlock(Properties properties) {
    super(ItemLoaderBlockEntity.class, properties);
    this.registerDefaultState(this.stateDefinition.any()
        .setValue(POWERED, false));
  }

  @Override
  protected MapCodec<? extends ManipulatorBlock<ItemLoaderBlockEntity>> codec() {
    return CODEC;
  }

  @Override
  public Direction getFacing(BlockState blockState) {
    return Direction.DOWN;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new ItemLoaderBlockEntity(blockPos, blockState);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState,
      BlockEntityType<T> type) {
    return level.isClientSide() ? null
        : createTickerHelper(type, RailcraftBlockEntityTypes.ITEM_LOADER.get(),
            ManipulatorBlockEntity::serverTick);
  }

  @Override
  public void appendHoverText(ItemStack stack, Item.TooltipContext context,
      List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, context, tooltip, flag);
    tooltip.add(Component.translatable(Translations.Tips.ITEM_LOADER)
        .withStyle(ChatFormatting.GRAY));
    tooltip.add(Component.translatable(Translations.Tips.PLACE_OVER_TRACK)
        .withStyle(ChatFormatting.RED));
  }
}
