package mods.railcraft.world.item.crafting;

import java.util.stream.IntStream;
import mods.railcraft.world.item.RailcraftItems;
import mods.railcraft.world.item.component.RailcraftDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TicketDuplicateRecipe extends CustomRecipe {

  private static final Ingredient SOURCE = Ingredient.of(RailcraftItems.GOLDEN_TICKET.get());
  private static final Ingredient BLANK = Ingredient.of(Items.PAPER);

  public TicketDuplicateRecipe(CraftingBookCategory category) {
    super(category);
  }

  @Override
  public boolean matches(CraftingInput craftingInput, Level level) {
    int numBlank = 0;
    int numSource = 0;
    for (int slot = 0; slot < craftingInput.size(); slot++) {
      var stack = craftingInput.getItem(slot);
      if (!stack.isEmpty()) {
        if (numSource == 0 && SOURCE.test(stack)) {
          numSource++;
        } else if (BLANK.test(stack)) {
          numBlank++;
        } else {
          return false;
        }
      }
    }
    return numSource == 1 && numBlank == 1;
  }

  @Override
  public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
    var source = IntStream.range(0, craftingInput.size())
        .mapToObj(craftingInput::getItem)
        .filter(TicketDuplicateRecipe.SOURCE)
        .findFirst()
        .orElse(ItemStack.EMPTY);
    var result = getResultItem(provider);
    if (!source.isEmpty()) {
      if (source.has(RailcraftDataComponents.TICKET)) {
        result.set(RailcraftDataComponents.TICKET, source.get(RailcraftDataComponents.TICKET));
      }
    }
    return result;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    NonNullList<Ingredient> ingredients = NonNullList.create();
    ingredients.add(Ingredient.of(RailcraftItems.GOLDEN_TICKET.get()));
    ingredients.add(Ingredient.of(Items.PAPER));
    return ingredients;
  }

  @Override
  public ItemStack getResultItem(HolderLookup.Provider provider) {
    return new ItemStack(RailcraftItems.TICKET.get());
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return RailcraftRecipeSerializers.TICKET_DUPLICATE.get();
  }
}
