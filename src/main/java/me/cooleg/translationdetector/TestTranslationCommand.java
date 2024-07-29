package me.cooleg.translationdetector;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestTranslationCommand implements CommandExecutor {

    private final Component NOT_ENOUGH_ARGS = Component.text("Not enough arguments provided, expected 2").color(NamedTextColor.RED);
    private final Component NOT_FOUND = Component.text("Player could not be found").color(NamedTextColor.RED);
    private final Component SUCCESS = Component.text("Testing player with the provided translation key. Check console for result.").color(NamedTextColor.GREEN);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {sender.sendMessage(NOT_ENOUGH_ARGS); return true;}

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {sender.sendMessage(NOT_FOUND); return true;}

        CheckTranslation.fetchPlayer(player, args[1]);
        sender.sendMessage(SUCCESS);
        return true;
    }

}
