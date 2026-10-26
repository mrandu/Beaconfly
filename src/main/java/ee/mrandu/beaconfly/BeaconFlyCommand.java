package ee.mrandu.beaconfly;

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

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("beaconfly.reload")) {
                sender.sendMessage("You do not have permission to reload BeaconFly.");
                return true;
            }

            plugin.reloadSettings();
            sender.sendMessage("BeaconFly configuration reloaded.");
            return true;
        }

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

        if (args[0].equalsIgnoreCase("sound")) {
            if (args.length != 2
                    || (!args[1].equalsIgnoreCase("on")
                    && !args[1].equalsIgnoreCase("off"))) {
                player.sendMessage("§dUsage: /beaconfly sound [on|off]");
                return true;
            }

            boolean enabled = args[1].equalsIgnoreCase("on");
            if (plugin.setSoundEnabledFor(player.getUniqueId(), enabled)) {
                player.sendMessage("§dFlight sound " + (enabled ? "enabled" : "disabled"));
            } else {
                player.sendMessage("§cCould not save your sound preference.");
            }
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

            plugin.disableFlight(player);

            player.sendMessage("§dBeaconFly disabled");

            return true;
        }

        player.sendMessage("§dUsage: /beaconfly [on|off|sound <on|off>|reload]");
        return true;
    }
}