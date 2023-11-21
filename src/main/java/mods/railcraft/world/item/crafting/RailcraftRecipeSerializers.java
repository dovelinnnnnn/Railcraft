package mods.railcraft.world.item.crafting;

import java.util.function.Supplier;
import mods.railcraft.api.core.RailcraftConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RailcraftRecipeSerializers {

  private static final DeferredRegister<RecipeSerializer<?>> deferredRegister =
      DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, RailcraftConstants.ID);

  public static final Supplier<RecipeSerializer<?>> ROLLING =
      deferredRegister.register("rolling", RollingRecipe.Serializer::new);

  public static final Supplier<RecipeSerializer<?>> COKING =
      deferredRegister.register("coking", CokeOvenRecipe.Serializer::new);

  public static final Supplier<RecipeSerializer<?>> BLASTING =
      deferredRegister.register("blasting", BlastFurnaceRecipe.Serializer::new);

  public static final Supplier<RecipeSerializer<?>> CRUSHER =
      deferredRegister.register("crusher", CrusherRecipe.Serializer::new);

  public static final Supplier<RecipeSerializer<TicketDuplicateRecipe>> TICKET_DUPLICATE =
      deferredRegister.register("ticket_duplicate",
          () -> new SimpleCraftingRecipeSerializer<>(TicketDuplicateRecipe::new));

  public static final Supplier<RecipeSerializer<LocomotivePaintingRecipe>> LOCOMOTIVE_PAINTING =
      deferredRegister.register("locomotive_painting",
          () -> new SimpleCraftingRecipeSerializer<>(LocomotivePaintingRecipe::new));

  public static final Supplier<RecipeSerializer<RotorRepairRecipe>> ROTOR_REPAIR =
      deferredRegister.register("rotor_repair",
          () -> new SimpleCraftingRecipeSerializer<>(RotorRepairRecipe::new));

  public static final Supplier<RecipeSerializer<CartDisassemblyRecipe>> CART_DISASSEMBLY =
      deferredRegister.register("cart_disassembly",
          () -> new SimpleCraftingRecipeSerializer<>(CartDisassemblyRecipe::new));

  public static void register(IEventBus modEventBus) {
    deferredRegister.register(modEventBus);
  }
}
