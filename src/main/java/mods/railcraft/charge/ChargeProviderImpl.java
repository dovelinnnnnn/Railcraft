package mods.railcraft.charge;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import mods.railcraft.api.charge.Charge;
import net.minecraft.server.level.ServerLevel;

public enum ChargeProviderImpl implements Charge.Provider {

  DISTRIBUTION(Charge.distribution);

  private final Charge charge;

  private final Reference2ReferenceOpenHashMap<ServerLevel, ChargeNetworkImpl> networks =
      new Reference2ReferenceOpenHashMap<>();

  ChargeProviderImpl(Charge charge) {
    this.charge = charge;
  }

  public Charge getCharge() {
    return this.charge;
  }

  @Override
  public ChargeNetworkImpl network(ServerLevel level) {
    return this.networks.computeIfAbsent(level, __ -> new ChargeNetworkImpl(this.charge, level));
  }

  public void removeChargeNetwork(ServerLevel level) {
    this.networks.remove(level);
  }
}
