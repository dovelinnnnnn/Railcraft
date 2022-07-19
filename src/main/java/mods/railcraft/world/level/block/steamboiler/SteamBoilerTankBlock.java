package mods.railcraft.world.level.block.steamboiler;

import mods.railcraft.world.level.block.entity.RailcraftBlockEntityTypes;
import mods.railcraft.world.level.block.entity.steamboiler.SteamBoilerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SteamBoilerTankBlock extends SteamBoilerBlock {

  public static final Property<ConnectionType> CONNECTION_TYPE =
      EnumProperty.create("connection_type", ConnectionType.class);

  public SteamBoilerTankBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.stateDefinition.any()
        .setValue(CONNECTION_TYPE, ConnectionType.NONE));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(CONNECTION_TYPE);
  }

  @Override
  public SteamBoilerBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
    return new SteamBoilerBlockEntity(blockPos, blockState);
  }

  @Override
  protected BlockEntityType<? extends SteamBoilerBlockEntity> getBlockEntityType() {
    return RailcraftBlockEntityTypes.STEAM_BOILER.get();
  }

  public enum ConnectionType implements StringRepresentable {

    ALL("all"),
    NONE("none"),
    NORTH_EAST("north_east"),
    SOUTH_EAST("south_east"),
    SOUTH_WEST("south_west"),
    NORTH_WEST("north_west"),
    NORTH_SOUTH_EAST("north_south_east"),
    SOUTH_EAST_WEST("south_east_west"),
    NORTH_EAST_WEST("north_east_west"),
    NORTH_SOUTH_WEST("north_south_west");

    private final String name;

    private ConnectionType(String name) {
      this.name = name;
    }

    @Override
    public String getSerializedName() {
      return this.name;
    }
  }
}
