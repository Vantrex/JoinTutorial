package de.vantrex.jointutorial.commands;

import de.vantrex.jointutorial.handler.TutorialHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteLocationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) return true;
        final Player player = (Player) cs;
        if (!player.hasPermission("jointutorial.create")) {
            player.sendMessage("§cDir fehlen die Rechte für diesen Befehl.");
            return true;
        }

        if (TutorialHandler.INSTANCE.isAddMessageProcedure(player)) {
            player.sendMessage("Du fügst bereits Nachrichten hinzu, benutze 'cancel' im Chat um zu bestätigen.");
            return true;
        }
        if (TutorialHandler.INSTANCE.isCreateProcedure(player)) {
            player.sendMessage("Du erstellst bereits eine Position, benutze 'cancel' im Chat um zu bestätigen.");
            return true;
        }
        if (args.length != 1) {
            sendHelp(player);
            return true;
        }
        try {
            int index = Integer.parseInt(args[0]);
            if (TutorialHandler.INSTANCE.getLocation(index) == null) {
                player.sendMessage("Falscher Index! (Size: " + TutorialHandler.INSTANCE.getLocations().size() + ")");
                return false;
            }
            TutorialHandler.INSTANCE.getLocations().remove(index);
            player.sendMessage("Position entfernt!");
        } catch (Exception ignored) {
            sendHelp(player);
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage("PREFIX> Benutze /tutorialdelete <Nummer>");
    }

}
