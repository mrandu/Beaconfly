package ee.mrandu.beaconfly;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BeaconFlyPlugin extends JavaPlugin {

    private final Set<UUID> disabledPlayers = new HashSet<>();
    private final Set<UUID> flightEnabled = new HashSet<>();

    @Override
    public void onEnable() {

        getCommand("beaconfly").setExecutor(
                new BeaconFlyCommand(this)
        );
        Bukkit.getScheduler().runTaskTimer(
                this,
                this::checkPlayers,
                0L,
                100L
        );

        getLogger().info("BeaconFly enabled");
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (flightEnabled.remove(player.getUniqueId())) {
                if (player.getGameMode() != GameMode.CREATIVE
                        && player.getGameMode() != GameMode.SPECTATOR) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                }
            }
        }

        getLogger().info("BeaconFly disabled");
    }

    public boolean isEnabledFor(UUID uuid) {
        return !disabledPlayers.contains(uuid);
    }

    public void setEnabledFor(UUID uuid, boolean enabled) {

        if (enabled) {
            disabledPlayers.remove(uuid);
        } else {
            disabledPlayers.add(uuid);
        }
    }

    public Set<UUID> getFlightEnabled() {
        return flightEnabled;
    }

    public void clearFlightEnabled(UUID uuid) {
        flightEnabled.remove(uuid);
    }

    private void checkPlayers() {

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (player.getGameMode() == GameMode.CREATIVE) {
                continue;
            }

            if (player.getGameMode() == GameMode.SPECTATOR) {
                continue;
            }

            boolean enabled =
                    isEnabledFor(player.getUniqueId());

            boolean hasHaste2 =
                    hasHaste2(player);

            boolean shouldFly =
                    enabled && hasHaste2;

            if (shouldFly) {

                player.setAllowFlight(true);

                if (!flightEnabled.contains(
                        player.getUniqueId())) {

                    player.sendMessage(
                            "§dFlight mode enabled"
                    );

                    flightEnabled.add(
                            player.getUniqueId()
                    );
                }

            } else {

                player.setFlying(false);
                player.setAllowFlight(false);

                if (flightEnabled.contains(
                        player.getUniqueId())) {

                    player.sendMessage(
                            "§dFlight mode disabled"
                    );

                    flightEnabled.remove(
                            player.getUniqueId()
                    );
                }
            }
        }
    }
    private boolean hasHaste2(Player player) {

        PotionEffect effect =
                player.getPotionEffect(
                        PotionEffectType.HASTE
                );

        if (effect == null) {
            return false;
        }

        return effect.getAmplifier() >= 1;
    }
}