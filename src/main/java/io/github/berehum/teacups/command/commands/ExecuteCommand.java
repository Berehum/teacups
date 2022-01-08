package io.github.berehum.teacups.command.commands;

import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.StringArrayArgument;
import cloud.commandframework.context.CommandContext;
import io.github.berehum.teacups.TeacupsMain;
import io.github.berehum.teacups.attraction.components.Teacup;
import io.github.berehum.teacups.command.CommandManager;
import io.github.berehum.teacups.command.TeacupCommand;
import io.github.berehum.teacups.command.arguments.TeacupArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;

public class ExecuteCommand extends TeacupCommand {

    private static final String commandString = "command";

    public ExecuteCommand(final @NonNull TeacupsMain plugin, final @NonNull CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.commandManager.registerSubcommand(builder ->
                builder.literal("execute", "command", "cmd")
                        .permission("teacups.command.execute")
                        .argument(EnumArgument.of(ExecuteType.class, "execute type"))
                        .argument(TeacupArgument.of(Teacup.name))
                        .argument(StringArrayArgument.of(commandString,
                                (commandSenderCommandContext, s) -> Arrays.asList("Command (without /)", "%player% for the player's name", "Example: eco give %player% 20")))
                        .handler(this::setRpm)
        );
    }

    private void setRpm(final @NonNull CommandContext<CommandSender> context) {
        final CommandSender sender = context.getSender();
        final Teacup teacup = context.get(Teacup.name);
        final ExecuteType executeType = context.get("execute type");
        final String[] commandArray = context.get(commandString);

        StringBuilder builder = new StringBuilder();
        for (String arg : commandArray) {
            builder.append(arg).append(" ");
        }
        final String command = builder.substring(0, builder.length() - 1);

        final CommandSender commandExecutor = Bukkit.getConsoleSender();

        if (executeType == ExecuteType.CONSOLE) {
            Bukkit.dispatchCommand(commandExecutor, command);
            return;
        } else {
            for (Player player : teacup.getPlayers()) {
                if (player == null) continue;
                if (executeType == ExecuteType.CONSOLE_FOR_EVERY_PLAYER) {
                    Bukkit.dispatchCommand(commandExecutor, command.replace("%player%", player.getName()));
                    continue;
                }
                Bukkit.dispatchCommand(player, command.replace("%player%", player.getName()));
            }
        }
        sender.sendMessage(ChatColor.GREEN + "Execute " + executeType.name().toLowerCase() + " command on " + teacup.getId());
    }

    private enum ExecuteType {
        PLAYER, CONSOLE, CONSOLE_FOR_EVERY_PLAYER;
    }
}
