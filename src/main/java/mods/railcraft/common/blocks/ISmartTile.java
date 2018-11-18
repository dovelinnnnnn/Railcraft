/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.api.items.IActivationBlockingItem;
import mods.railcraft.common.blocks.interfaces.ITile;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 *
 */
public interface ISmartTile extends ITile {

    default boolean canCreatureSpawn(EntityLiving.SpawnPlacementType type) {
        return true;
    }

    default void onBlockAdded() {
    }

    /**
     * Called before the block is removed.
     */
    default void onBlockRemoval() {
        if (this instanceof IInventory)
            InvTools.dropInventory((IInventory) this, tile().getWorld(), tile().getPos());
    }

    default boolean blockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking())
            return false;
        ItemStack heldItem = player.getHeldItem(hand);
        if (!InvTools.isEmpty(heldItem)) {
            if (heldItem.getItem() instanceof IActivationBlockingItem)
                return false;
            if (TrackTools.isRailItem(heldItem.getItem()))
                return false;
        }
        return openGui(player);
    }

    default boolean openGui(EntityPlayer player) {
        EnumGui gui = getGui();

        if (gui != null) {
            BlockPos pos = tile().getPos();
            GuiHandler.openGui(gui, player, tile().getWorld(), pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }

    default @Nullable EnumGui getGui() {
        return null;
    }

    default boolean isSideSolid(EnumFacing side) {
        return true;
    }

    default float getResistance(@Nullable Entity exploder) {
        return 4.5f;
    }

    default float getHardness() {
        return 2.0f;
    }

    default boolean canConnectRedstone(@Nullable EnumFacing dir) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    default void randomDisplayTick(Random rand) {
    }

    default IPostConnection.ConnectStyle connectsToPost(EnumFacing side) {
        if (isSideSolid(side.getOpposite()))
            return IPostConnection.ConnectStyle.TWO_THIN;
        return IPostConnection.ConnectStyle.NONE;
    }

    void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack);

    default void onNeighborBlockChange(IBlockState state, Block neighborBlock, BlockPos neighborPos) {

    }

    default void notifyBlocksOfNeighborChange() {
        WorldPlugin.notifyBlocksOfNeighborChange(tile().getWorld(), tile().getPos(), tile().getBlockType());
    }

    default IBlockState getActualState(IBlockState state) {
        return state;
    }

    default IBlockState getExtendedState(IBlockState state) {
        return state;
    }

}
