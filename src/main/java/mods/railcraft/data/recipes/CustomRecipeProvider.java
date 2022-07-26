package mods.railcraft.data.recipes;

import java.util.function.Consumer;

import mods.railcraft.Railcraft;
import mods.railcraft.util.ColorVariantRegistrar;
import mods.railcraft.world.item.RailcraftItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class CustomRecipeProvider extends RecipeProvider {
  public CustomRecipeProvider(DataGenerator gen) {
    super(gen);
  }

  protected static void tankWall(Consumer<FinishedRecipe> consumer,
                          ItemLike ingredient,
                          ColorVariantRegistrar<BlockItem> colorItems,
                          TagKey<Item> tagItem) {
    var result = colorItems.variantFor(DyeColor.WHITE).get();
    var name = ForgeRegistries.ITEMS.getKey(result).getPath();
    ShapedRecipeBuilder.shaped(result, 8)
      .pattern("aa")
      .pattern("aa")
      .define('a', ingredient)
      .unlockedBy(getHasName(ingredient), has(ingredient))
      .save(consumer, Railcraft.ID + ":" + name.substring(name.indexOf('_') + 1));

    for (var dyeColor : DyeColor.values()) {
      ShapedRecipeBuilder.shaped(colorItems.variantFor(dyeColor).get(), 8)
        .pattern("aaa")
        .pattern("aba")
        .pattern("aaa")
        .define('a', tagItem)
        .define('b', DyeItem.byColor(dyeColor))
        .unlockedBy(getHasName(result), has(result))
        .save(consumer);
    }
  }

  protected static void tankValve(Consumer<FinishedRecipe> consumer,
                                 ItemLike ingredient,
                                 ColorVariantRegistrar<BlockItem> colorItems,
                                 TagKey<Item> tagItem) {
    var result = colorItems.variantFor(DyeColor.WHITE).get();
    var name = ForgeRegistries.ITEMS.getKey(result).getPath();
    ShapedRecipeBuilder.shaped(result, 8)
      .pattern("aba")
      .pattern("bcb")
      .pattern("aba")
      .define('a', Items.IRON_BARS)
      .define('b', ingredient)
      .define('c', Items.LEVER)
      .unlockedBy(getHasName(ingredient), has(ingredient))
      .save(consumer, new ResourceLocation(Railcraft.ID, name.substring(name.indexOf('_') + 1)));

    for (var dyeColor : DyeColor.values()) {
      ShapedRecipeBuilder.shaped(colorItems.variantFor(dyeColor).get(), 8)
        .pattern("aaa")
        .pattern("aba")
        .pattern("aaa")
        .define('a', tagItem)
        .define('b', DyeItem.byColor(dyeColor))
        .unlockedBy(getHasName(result), has(result))
        .save(consumer);
    }
  }

}
