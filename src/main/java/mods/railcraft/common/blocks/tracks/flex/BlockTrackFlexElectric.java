/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.tracks.flex;

import mods.railcraft.api.tracks.TrackType;
import mods.railcraft.api.charge.ChargeNodeDefinition;
import mods.railcraft.common.blocks.charge.ChargeManager;
import mods.railcraft.api.charge.ConnectType;
import mods.railcraft.api.charge.IChargeBlock;
import mods.railcraft.common.blocks.tracks.TrackIngredients;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Created by CovertJaguar on 8/2/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockTrackFlexElectric extends BlockTrackFlex implements IChargeBlock {
    public static ChargeNodeDefinition CHARGE_DEF = new ChargeNodeDefinition(ConnectType.TRACK, 0.01);

    public BlockTrackFlexElectric(TrackType trackType) {
        super(trackType);
        setTickRandomly(true);
    }

    @Override
    public void defineTrackRecipe() {
        CraftingPlugin.addRecipe(getRecipeOutput(),
                "IcI",
                "I#I",
                "IcI",
                'I', getTrackType().getRail(),
                'c', TrackIngredients.RAIL_ELECTRIC,
                '#', getTrackType().getRailbed());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TrackTools.throwSparks(stateIn, worldIn, pos, rand);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        registerNode(state, worldIn, pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        ChargeManager.getDimension(worldIn).deregisterChargeNode(pos);
    }

    @Nullable
    @Override
    public ChargeNodeDefinition getChargeDef(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CHARGE_DEF;
    }
}
