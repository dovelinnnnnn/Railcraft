package mods.railcraft.world.item;

import java.util.Objects;
import mods.railcraft.Translations;
import mods.railcraft.api.core.DimensionPos;
import mods.railcraft.api.signal.SignalControllerProvider;
import mods.railcraft.api.signal.SignalReceiverProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class SignalTunerItem extends PairingToolItem {

  public SignalTunerItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult onItemUseFirst(ItemStack itemStack, UseOnContext context) {
    var player = context.getPlayer();
    var level = context.getLevel();
    var pos = context.getClickedPos();
    var blockState = level.getBlockState(pos);

    if (level instanceof ServerLevel serverLevel) {
      if (this.checkAbandonPairing(itemStack, player, serverLevel,
          () -> {
            if (level.getBlockEntity(pos) instanceof SignalControllerProvider provider) {
              provider.getSignalController().stopLinking();
            }
          })) {
        player.displayClientMessage(
            Component.translatable(Translations.Misc.SIGNAL_TUNER_ABANDONED), true);
        return InteractionResult.SUCCESS;
      }

      var blockEntity = level.getBlockEntity(pos);
      if (blockEntity != null) {
        var previousTarget = this.getPeerPos(itemStack);
        if (blockEntity instanceof SignalReceiverProvider signalReceiver
            && previousTarget != null) {
          if (!Objects.equals(pos, previousTarget.getPos())) {
            var previousBlockEntity = level.getBlockEntity(previousTarget.getPos());
            if (previousBlockEntity instanceof SignalControllerProvider signalController) {
              if (blockEntity != previousBlockEntity) {
                signalController.getSignalController().addPeer(signalReceiver);
                signalController.getSignalController().stopLinking();
                player.displayClientMessage(
                    Component.translatable(Translations.Misc.SIGNAL_TUNER_SUCCESS,
                        previousBlockEntity.getBlockState().getBlock().getName(),
                        blockState.getBlock().getName()),
                    true);
                this.clearPeerPos(itemStack);
                return InteractionResult.SUCCESS;
              }
            } else if (level.isLoaded(previousTarget.getPos())) {
              player.displayClientMessage(
                  Component.translatable(Translations.Misc.SIGNAL_TUNER_LOST),
                  true);
              this.clearPeerPos(itemStack);
            } else {
              player.displayClientMessage(
                  Component.translatable(Translations.Misc.SIGNAL_TUNER_UNLOADED),
                  true);
              this.clearPeerPos(itemStack);
            }
          }
        } else if (blockEntity instanceof SignalControllerProvider provider) {
          var controller = provider.getSignalController();
          if (previousTarget == null || !Objects.equals(pos, previousTarget.getPos())) {
            player.displayClientMessage(
                Component.translatable(Translations.Misc.SIGNAL_TUNER_BEGIN,
                    blockState.getBlock().getName()),
                true);
            this.setPeerPos(itemStack, DimensionPos.from(blockEntity));
            controller.startLinking();
          } else {
            player.displayClientMessage(
                Component.translatable(Translations.Misc.SIGNAL_TUNER_ABANDONED,
                    blockState.getBlock().getName()),
                true);
            controller.stopLinking();
            this.clearPeerPos(itemStack);
          }
        } else {
          return InteractionResult.PASS;
        }
      }
    }

    return InteractionResult.sidedSuccess(level.isClientSide());
  }
}
