package mods.railcraft.client.gui.screen;

import java.util.List;
import com.mojang.blaze3d.vertex.PoseStack;
import mods.railcraft.Railcraft;
import mods.railcraft.Translations;
import mods.railcraft.client.gui.widget.button.ButtonTexture;
import mods.railcraft.client.gui.widget.button.RailcraftButton;
import mods.railcraft.client.util.GuiUtil;
import mods.railcraft.network.NetworkChannel;
import mods.railcraft.network.play.EditTicketAttributeMessage;
import mods.railcraft.world.item.TicketItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class GoldenTicketScreen extends IngameWindowScreen {

  private static final ResourceLocation TICKET_LOCATION =
      new ResourceLocation(Railcraft.ID, "textures/gui/item/golden_ticket.png");
  private static final int IMAGE_WIDTH = 256;
  private static final int IMAGE_HEIGHT = 136;
  private static final String PREFIX = "Dest=";

  private final ItemStack itemStack;
  private final InteractionHand hand;
  private String dest;
  private RailcraftButton helpButton;
  private EditBox editBoxDest;
  private boolean readingManual;

  public GoldenTicketScreen(ItemStack itemStack, InteractionHand hand) {
    super(GameNarrator.NO_TITLE, TICKET_LOCATION, IMAGE_WIDTH, IMAGE_HEIGHT);
    this.itemStack = itemStack;
    this.hand = hand;
    this.dest = PREFIX + TicketItem.getDestination(this.itemStack);
    this.readingManual = false;
  }

  @Override
  protected void init() {
    var buttons = List.of(
        RailcraftButton
            .builder(CommonComponents.GUI_DONE, button -> {
              sendMessageToServer();
              this.minecraft.setScreen(null);
            }, ButtonTexture.LARGE_BUTTON)
            .pos(0, this.height / 2 + 75)
            .size(65, 20)
            .build(),
        this.helpButton = RailcraftButton
            .builder(Translations.Screen.HELP, button -> {
              this.readingManual = !this.readingManual;
            }, ButtonTexture.LARGE_BUTTON)
            .pos(0, this.height / 2 + 75)
            .size(65, 20)
            .build(),
        RailcraftButton
            .builder(CommonComponents.GUI_CANCEL, button -> {
              this.minecraft.setScreen(null);
            }, ButtonTexture.LARGE_BUTTON)
            .pos(0, this.height / 2 + 75)
            .size(65, 20)
            .build()
    );
    GuiUtil.newButtonRowAuto(this::addRenderableWidget, this.width / 2 - 100, 200, buttons);

    this.editBoxDest = new EditBox(font, this.width / 2 - (234 / 2), this.height / 2 + 23,
        234, 20, Component.empty());
    this.editBoxDest.setValue(this.dest);
    this.editBoxDest.setBordered(false);
    this.addRenderableWidget(this.editBoxDest);
  }

  @Override
  protected void renderContent(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
    if (this.readingManual) {
      this.editBoxDest.setVisible(false);
      var about = Component.translatable(Translations.Screen.GOLDEN_TICKET_ABOUT);
      var help = Component.translatable(Translations.Screen.GOLDEN_TICKET_HELP)
          .withStyle(ChatFormatting.BLACK);

      drawCenteredString(poseStack, about, 15, false);
      this.font.drawWordWrap(poseStack, help, 15, 30, 230, IngameWindowScreen.TEXT_COLOR);
      this.helpButton.setMessage(CommonComponents.GUI_BACK);
    } else {
      var title = Component.translatable(Translations.Screen.GOLDEN_TICKET_TITLE)
          .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD);
      var desc1 = Component.translatable(Translations.Screen.GOLDEN_TICKET_DESC_1);
      var desc2 = Component.translatable(Translations.Screen.GOLDEN_TICKET_DESC_2);
      poseStack.pushPose();
      poseStack.scale(2, 2, 2);
      drawCenteredString(poseStack, title, IMAGE_WIDTH / 2, 8, true);
      poseStack.popPose();
      drawCenteredString(poseStack, desc1, 45, false);
      drawCenteredString(poseStack, desc2, 60, false);
      this.editBoxDest.setVisible(true);
      this.helpButton.setMessage(Component.translatable(Translations.Screen.HELP));
    }
  }

  private void sendMessageToServer() {
    this.dest = this.editBoxDest.getValue();
    var modified = this.dest.startsWith(PREFIX) && !this.dest.equals(PREFIX);
    if (!modified)
      return;

    var destWithoutPrefix = this.dest.substring(PREFIX.length());
    var success = TicketItem.setTicketData(this.itemStack, destWithoutPrefix,
        this.minecraft.player.getGameProfile());
    if (success) {
      NetworkChannel.GAME.sendToServer(new EditTicketAttributeMessage(this.hand, destWithoutPrefix));
    }
  }
}