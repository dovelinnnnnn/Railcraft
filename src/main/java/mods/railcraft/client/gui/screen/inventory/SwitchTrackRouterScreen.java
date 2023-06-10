package mods.railcraft.client.gui.screen.inventory;

import java.util.List;
import java.util.Optional;
import com.mojang.blaze3d.vertex.PoseStack;
import mods.railcraft.Railcraft;
import mods.railcraft.Translations;
import mods.railcraft.client.gui.widget.button.ButtonTexture;
import mods.railcraft.client.gui.widget.button.MultiButton;
import mods.railcraft.network.NetworkChannel;
import mods.railcraft.network.play.SetSwitchTrackRouterAttributesMessage;
import mods.railcraft.util.routing.RoutingLogic;
import mods.railcraft.util.routing.RoutingLogicException;
import mods.railcraft.world.inventory.SwitchTrackRouterMenu;
import mods.railcraft.world.level.block.entity.SwitchTrackRouterBlockEntity;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SwitchTrackRouterScreen extends RailcraftMenuScreen<SwitchTrackRouterMenu> {

  private static final ResourceLocation BACKGROUND_TEXTURE =
      new ResourceLocation(Railcraft.ID, "textures/gui/container/routing.png");
  private static final Component ROUTING_TABLE =
      Component.translatable(Translations.Screen.ROUTING_TABLE_BOOK);
  private static final int REFRESH_INTERVAL_TICKS = 20;
  private final SwitchTrackRouterBlockEntity switchTrackRouter;

  private MultiButton<SwitchTrackRouterBlockEntity.Lock> lockButton;
  private MultiButton<SwitchTrackRouterBlockEntity.Railway> railwayButton;
  private int refreshTimer;

  public SwitchTrackRouterScreen(SwitchTrackRouterMenu menu, Inventory inventory,
      Component title) {
    super(menu, inventory, title);
    this.imageHeight = 158;
    this.imageWidth = 176;
    this.inventoryLabelY = this.imageHeight - 94;
    this.switchTrackRouter = menu.getSwitchTrackRouter();

    this.registerWidgetRenderer(new WidgetRenderer<>(menu.getErrorWidget()) {
      @Override
      public List<Component> getTooltip() {
        return menu.getLogic()
            .map(RoutingLogic::getError)
            .map(RoutingLogicException::getToolTip)
            .orElse(null);
      }

      @Override
      public void render(RailcraftMenuScreen<?> screen, PoseStack poseStack, int centreX,
          int centreY, int mouseX, int mouseY) {
        if (getTooltip() != null) {
          super.render(screen, poseStack, centreX, centreY, mouseX, mouseY);
        }
      }
    });
  }

  @Override
  public ResourceLocation getWidgetsTexture() {
    return BACKGROUND_TEXTURE;
  }

  @Override
  protected void init() {
    super.init();
    this.lockButton = this.addRenderableWidget(MultiButton
        .builder(ButtonTexture.SMALL_BUTTON, this.switchTrackRouter.getLock())
        .bounds(this.leftPos + 152, this.topPos + 8, 16, 16)
        .tooltipFactory(this::updateLockButtonTooltip)
        .stateCallback(this::setLock)
        .build());
    this.railwayButton = this.addRenderableWidget(MultiButton
        .builder(ButtonTexture.SMALL_BUTTON, this.switchTrackRouter.getRailway())
        .bounds(this.leftPos + 68, this.topPos + 50, 100, 16)
        .tooltipFactory(this::updateRailwayButtonTooltip)
        .stateCallback(this::setRailway)
        .build());
    this.updateButtons();
  }

  private void setLock(SwitchTrackRouterBlockEntity.Lock lock) {
    if (this.switchTrackRouter.getLock() != lock) {
      this.switchTrackRouter.setLock(
          lock.equals(SwitchTrackRouterBlockEntity.Lock.UNLOCKED)
          ? null : this.minecraft.getUser().getGameProfile());
      this.sendAttributes();
    }
  }

  private void setRailway(SwitchTrackRouterBlockEntity.Railway railway) {
    if (this.switchTrackRouter.getRailway() != railway) {
      this.switchTrackRouter.setRailway(
          railway.equals(SwitchTrackRouterBlockEntity.Railway.PUBLIC)
          ? null : this.minecraft.getUser().getGameProfile());
      this.sendAttributes();
    }
  }

  private Optional<Tooltip> updateLockButtonTooltip(SwitchTrackRouterBlockEntity.Lock lock) {
    return Optional.of(Tooltip.create(switch (lock) {
      case LOCKED -> Component.translatable(Translations.Screen.ACTION_SIGNAL_BOX_LOCKED,
          this.switchTrackRouter.getOwnerOrThrow().getName());
      case UNLOCKED -> Component.translatable(Translations.Screen.ACTION_SIGNAL_BOX_UNLOCKED);
    }));
  }

  private Optional<Tooltip> updateRailwayButtonTooltip(
      SwitchTrackRouterBlockEntity.Railway railway) {
    return Optional.of(Tooltip.create(switch (railway) {
      case PRIVATE -> Component.translatable(Translations.Screen.SWITCH_TRACK_ROUTER_PRIVATE_RAILWAY_DESC,
          this.switchTrackRouter.getOwnerOrThrow().getName());
      case PUBLIC -> Component.translatable(Translations.Screen.SWITCH_TRACK_ROUTER_PUBLIC_RAILWAY_DESC);
    }));
  }

  @Override
  protected void containerTick() {
    super.containerTick();
    if (this.refreshTimer++ >= REFRESH_INTERVAL_TICKS) {
      this.refreshTimer = 0;
      this.updateButtons();
    }
  }

  private void updateButtons() {
    var canAccess = this.switchTrackRouter.canAccess(this.minecraft.getUser().getGameProfile());
    this.lockButton.active = canAccess;
    this.lockButton.setState(this.switchTrackRouter.getLock());
    this.railwayButton.active = canAccess;
    this.railwayButton.setState(this.switchTrackRouter.getRailway());
  }

  private void sendAttributes() {
    if (!this.switchTrackRouter.canAccess(this.minecraft.getUser().getGameProfile())) {
      return;
    }
    NetworkChannel.GAME.sendToServer(
        new SetSwitchTrackRouterAttributesMessage(this.switchTrackRouter.getBlockPos(),
            this.railwayButton.getState(), this.lockButton.getState()));
  }

  @Override
  protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
    super.renderLabels(poseStack, mouseX, mouseY);
    this.font.draw(poseStack, ROUTING_TABLE, 64, 29, 4210752);
  }
}