package io.github.berehum.teacups.command.commands;

import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import io.github.berehum.teacups.TeacupsMain;
import io.github.berehum.teacups.attraction.Teacup;
import io.github.berehum.teacups.attraction.components.Seat;
import io.github.berehum.teacups.command.CommandManager;
import io.github.berehum.teacups.command.TeacupCommand;
import io.github.berehum.teacups.command.arguments.TeacupArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

public class KickCommand extends TeacupCommand {

    public KickCommand(final @NonNull TeacupsMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("kick")
                        .argument(TeacupArgument.of("teacup"))
                        .argument(PlayerArgument.optional("player"))
                        .handler(this::setRpm)
        );
    }

    private void setRpm(final @NonNull CommandContext<CommandSender> context) {
        final CommandSender sender = context.getSender();
        final Teacup teacup = context.get("teacup");
        final Optional<Player> optionalPlayer = context.getOptional("player");

        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            for (Seat seat : teacup.getSeats()) {
                if (seat.getPlayer() == player) {
                    seat.dismount();
                }
            }
            sender.sendMessage(ChatColor.GREEN + "Kicked " + player.getName() + " from all seats of " + teacup.getId());
            return;
        }
        for (Seat seat : teacup.getSeats()) {
            seat.dismount();
        }
        sender.sendMessage(ChatColor.GREEN + "Kicked all player from all seats of " + teacup.getId());
    }
}