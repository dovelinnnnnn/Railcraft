package mods.railcraft;

import mods.railcraft.api.core.RailcraftConstants;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(RailcraftConstants.ID)
@PrefixGameTestTemplate(false)
public class GatedTrackTest {

  @GameTest(template = "gated_track_active")
  public static void gatedTrackActive(GameTestHelper helper) {
    helper.pressButton(0, 3, 1);
    helper.succeedWhenEntityPresent(EntityType.MINECART, 5, 2, 1);
  }

  @GameTest(template = "gated_track_passive")
  public static void gatedTrackPassive(GameTestHelper helper) {
    helper.pressButton(0, 3, 1);
    helper.succeedWhenEntityPresent(EntityType.MINECART, 3, 2, 1);
  }
}
