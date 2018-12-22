/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.api.core.IIngredientSource;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.Ingredients;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreIngredient;

import java.util.Locale;

import static mods.railcraft.common.items.ItemCharge.EnumCharge.*;

/**
 * Created by CovertJaguar on 6/17/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCharge extends ItemRailcraftSubtyped {

    public ItemCharge() {
        super(EnumCharge.class);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(getStack(COIL),
                "CCC",
                "III",
                "CCC",
                'C', RailcraftItems.CHARGE, SPOOL_SMALL,
                'I', "plateIron");

        CraftingPlugin.addRecipe(getStack(TERMINAL),
                " P ",
                "BBB",
                'B', "ingotBrass",
                'P', "plateBrass");

        CraftingPlugin.addRecipe(getStack(3, SPOOL_SMALL),
                "W",
                'W', getStack(SPOOL_MEDIUM));

        CraftingPlugin.addRecipe(getStack(3, SPOOL_MEDIUM),
                "W",
                'W', getStack(SPOOL_LARGE));

        Ingredient ingotCopper = new OreIngredient("ingotCopper");
        Ingredient ingotConductiveIron = new OreIngredient("ingotConductiveIron");


        //TODO configure time
        Crafters.rollingMachine().newShapelessRecipeBuilder()
                .output(getStack(SPOOL_SMALL))
                .add(ingotCopper)
                .time(100)
                .buildAndRegister();

        Crafters.rollingMachine().newShapelessRecipeBuilder()
                .output(getStack(SPOOL_SMALL))
                .add(ingotConductiveIron)
                .time(100)
                .buildAndRegister();

//        Crafters.rollingMachine.addRecipe(
//                getStack(SPOOL_SMALL),
//                "C",
//                'C', "ingotCopper");

        Ingredient blockCopper = new OreIngredient("blockCopper");
        Ingredient blockConductiveIron = new OreIngredient("blockConductiveIron");

        //TODO configure time
        Crafters.rollingMachine().newShapelessRecipeBuilder()
                .output(getStack(SPOOL_LARGE))
                .add(blockCopper)
                .time(300)
                .buildAndRegister();

        Crafters.rollingMachine().newShapelessRecipeBuilder()
                .output(getStack(SPOOL_LARGE))
                .add(blockConductiveIron)
                .time(300)
                .buildAndRegister();

//        Crafters.rollingMachine().addRecipe(
//                getStack(SPOOL_LARGE),
//                "C",
//                'C', "blockConductiveIron");
//
//        Crafters.rollingMachine().addRecipe(
//                getStack(SPOOL_LARGE),
//                "C",
//                'C', "blockCopper");

        CraftingPlugin.addRecipe(getStack(MOTOR),
                " S ",
                "PCP",
                " T ",
                'C', RailcraftItems.CHARGE, COIL,
                'S', "ingotSteel",
                'T', RailcraftItems.CHARGE, TERMINAL,
                'P', "plateTin");

        Crafters.rollingMachine().addRecipe(
                getStack(ELECTRODE_NICKEL),
                " P ",
                " P ",
                " P ",
                'P', "plateNickel");

        Crafters.rollingMachine().addRecipe(
                getStack(ELECTRODE_IRON),
                " P ",
                " P ",
                " P ",
                'P', "plateIron");

        Crafters.rollingMachine().addRecipe(
                getStack(ELECTRODE_ZINC),
                " P ",
                " P ",
                " P ",
                'P', "plateZinc");

        Crafters.rollingMachine().addRecipe(
                getStack(ELECTRODE_CARBON),
                " P ",
                " P ",
                " P ",
                'P', "coal");

        Crafters.rollingMachine().addRecipe(
                getStack(ELECTRODE_SILVER),
                " P ",
                " P ",
                " P ",
                'P', "plateSilver");
    }

    public enum EnumCharge implements IVariantEnum {

        COIL("blockCopper"),
        TERMINAL("plateBrass"),
        SPOOL_SMALL("ingotCopper"),
        SPOOL_MEDIUM("blockCopper"),
        SPOOL_LARGE("blockCopper"),
        MOTOR("blockCopper"),
        ELECTRODE_NICKEL("plateNickel"),
        ELECTRODE_IRON("plateIron"),
        ELECTRODE_ZINC("plateZinc"),
        ELECTRODE_CARBON("coal"),
        ELECTRODE_SILVER("plateSilver"),
        ;
        public static EnumCharge[] VALUES = values();
        private final Ingredient alternate;

        EnumCharge(Object alt) {
            this.alternate = Ingredients.from(alt);
        }

        @Override
        public Ingredient getAlternate(IIngredientSource container) {
            return alternate;
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
