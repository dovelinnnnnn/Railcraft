package mods.railcraft.world.level.block.track.outfitted;

import java.util.function.Supplier;
import mods.railcraft.api.track.TrackType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.RailShape;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ReversibleOutfittedTrackBlock extends OutfittedTrackBlock {

  public static final BooleanProperty REVERSED = BooleanProperty.create("reversed");

  public ReversibleOutfittedTrackBlock(Supplier<? extends TrackType> trackType,
      Properties properties) {
    super(trackType, properties);
  }

  @Override
  protected BlockState buildDefaultState(BlockState blockState) {
    return super.buildDefaultState(blockState)
        .setValue(REVERSED, false);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(REVERSED);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return super.getStateForPlacement(context)
        .setValue(REVERSED, context.getHorizontalDirection() == Direction.SOUTH
            || context.getHorizontalDirection() == Direction.WEST);
  }

  @Override
  public BlockState rotate(BlockState blockState, Rotation rotation) {
    return rotation == Rotation.CLOCKWISE_180 ? blockState.cycle(REVERSED) : blockState;
  }

  @Override
  protected boolean crowbarWhack(BlockState blockState, Level level, BlockPos pos,
      Player player, InteractionHand hand, ItemStack itemStack) {
    level.setBlockAndUpdate(pos, blockState.cycle(REVERSED));
    return true;
  }

  public static boolean isReversed(BlockState blockState) {
    return blockState.getValue(REVERSED);
  }

  public static Direction getFacing(BlockState blockState) {
    return getDirection(getRailShapeRaw(blockState), isReversed(blockState));
  }

  public static Direction getDirection(RailShape railShape, boolean reversed) {
    return railShape == RailShape.NORTH_SOUTH
        ? reversed ? Direction.SOUTH : Direction.NORTH
        : reversed ? Direction.WEST : Direction.EAST;
  }
}
