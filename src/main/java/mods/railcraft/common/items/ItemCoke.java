/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemCoke extends ItemRailcraft {
    private static final int COKE_COOK_TIME = 1800;
    private static final int COKE_COOK_CREOSOTE = 500;
    public static final int COKE_HEAT = 3200;

    @Override
    public void initializeDefinition() {
        OreDictionary.registerOre("fuelCoke", new ItemStack(this));
    }

    @Override
    public void defineRecipes() {
        Crafters.blastFurnace().addFuel(getRegistryName(), getIngredient(), COKE_HEAT);
        if (RailcraftConfig.coalCokeTorchOutput() > 0) {
            CraftingPlugin.addRecipe(new ItemStack(Blocks.TORCH, RailcraftConfig.coalCokeTorchOutput()),
                    "C",
                    "S",
                    'C', "fuelCoke",
                    'S', "stickWood");
        }
        FluidStack creosoteStack = Fluids.CREOSOTE.get(COKE_COOK_CREOSOTE);
        Crafters.cokeOven().addRecipe(Ingredient.fromStacks(new ItemStack(Items.COAL, 1, 0)), new ItemStack(this), creosoteStack, COKE_COOK_TIME);
    }

    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return COKE_HEAT;
    }
}
