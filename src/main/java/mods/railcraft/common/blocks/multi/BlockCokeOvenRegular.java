/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.multi;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 *
 */
public final class BlockCokeOvenRegular extends BlockCokeOven {

    @Override
    public void defineRecipes() {
        ItemStack stack = new ItemStack(this);
        CraftingPlugin.addRecipe(stack,
                "MBM",
                "BMB",
                "MBM",
                'B', "ingotBrick",
                'M', Blocks.SAND);
        Crafters.rockCrusher().makeRecipe(CraftingPlugin.getIngredient(this))
                .addOutput(new ItemStack(Items.BRICK, 3))
                .addOutput(new ItemStack(Items.BRICK), 0.5f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .addOutput(new ItemStack(Blocks.SAND), 0.25f)
                .register();
    }
}
