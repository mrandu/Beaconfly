package ee.mrandu.beaconfly;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BeaconFlyPlugin extends JavaPlugin {

    private final Set<UUID> disabledPlayers = new HashSet<>();
    private final Set<UUID> flightEnabled = new HashSet<>();
    private Sound flightSound = Sound.BLOCK_NOTE_BLOCK_CHIME;
    private float flightSoundPitch = 0.6f;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSoundSettings();

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

    private void loadSoundSettings() {
        String soundName = getConfig().getString(
                "flight-sound.sound",
            "minecraft:block.note_block.chime"
        );
        NamespacedKey soundKey = NamespacedKey.fromString(soundName.trim());
        Sound configuredSound = soundKey == null
            ? null
            : Registry.SOUNDS.get(soundKey);
        if (configuredSound == null) {
            getLogger().warning(
                    "Invalid flight-sound.sound '" + soundName
                    + "'; using minecraft:block.note_block.chime"
            );
            flightSound = Sound.BLOCK_NOTE_BLOCK_CHIME;
        } else {
            flightSound = configuredSound;
        }

        double pitch = getConfig().getDouble("flight-sound.pitch", 0.6);
        if (!Double.isFinite(pitch) || pitch < 0.5 || pitch > 2.0) {
            getLogger().warning(
                    "flight-sound.pitch must be between 0.5 and 2.0; using 0.6"
            );
            pitch = 0.6;
        }
        flightSoundPitch = (float) pitch;
    }

    public void reloadSettings() {
        reloadConfig();
        loadSoundSettings();
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

    public void disableFlight(Player player) {
        UUID uuid = player.getUniqueId();
        if (player.getGameMode() == GameMode.CREATIVE
                || player.getGameMode() == GameMode.SPECTATOR) {
            flightEnabled.remove(uuid);
            return;
        }

        player.setFlying(false);
        player.setAllowFlight(false);
        updateFlightState(player, false);
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
                updateFlightState(player, true);

            } else {

                player.setFlying(false);
                player.setAllowFlight(false);
                updateFlightState(player, false);
            }
        }
    }

    private void updateFlightState(Player player, boolean enabled) {
        boolean changed = enabled
                ? flightEnabled.add(player.getUniqueId())
                : flightEnabled.remove(player.getUniqueId());
        if (!changed) {
            return;
        }

        player.sendMessage(enabled
                ? "§dFlight mode enabled"
                : "§dFlight mode disabled");
        player.playSound(
                player.getLocation(),
            flightSound,
                1.0f,
            flightSoundPitch
        );
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