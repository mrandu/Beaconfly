package ee.mrandu.beaconfly;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BeaconFlyCommand implements CommandExecutor {

    private final BeaconFlyPlugin plugin;

    public BeaconFlyCommand(BeaconFlyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        if (args.length == 0) {

            player.sendMessage("§dBeaconFly v" + plugin.getDescription().getVersion());

            player.sendMessage(
                    "§dStatus: " +
                            (plugin.isEnabledFor(player.getUniqueId())
                                    ? "Enabled"
                                    : "Disabled")
            );

            return true;
        }

        if (args[0].equalsIgnoreCase("on")) {

            plugin.setEnabledFor(
                    player.getUniqueId(),
                    true
            );

            player.sendMessage("§dBeaconFly enabled");

            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {

            plugin.setEnabledFor(
                    player.getUniqueId(),
                    false
            );

                if (player.getGameMode() != GameMode.CREATIVE
                    && player.getGameMode() != GameMode.SPECTATOR) {
                player.setFlying(false);
                player.setAllowFlight(false);
            }
            plugin.clearFlightEnabled(player.getUniqueId());

            player.sendMessage("§dBeaconFly disabled");

            return true;
        }

        player.sendMessage("§dUsage: /beaconfly [on|off]");
        return true;
    }
}